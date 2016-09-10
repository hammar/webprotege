package edu.stanford.bmir.protege.web.server.xd;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import edu.stanford.bmir.protege.web.server.dispatch.AbstractHasProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.UserHasProjectReadPermissionValidator;
import edu.stanford.bmir.protege.web.server.owlapi.OWLAPIProject;
import edu.stanford.bmir.protege.web.server.owlapi.OWLAPIProjectManager;
import edu.stanford.bmir.protege.web.server.xd.log.XdpLogger;
import edu.stanford.bmir.protege.web.server.xd.util.AnnotationOperations;
import edu.stanford.bmir.protege.web.server.xd.util.OntologyOperations;
import edu.stanford.bmir.protege.web.shared.DataFactory;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetInstantiationAlignmentSuggestionsAction;
import edu.stanford.bmir.protege.web.shared.xd.data.CodpInstantiationMethod;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.EquivalentClassesAlignment;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.EquivalentDataPropertiesAlignment;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.EquivalentObjectPropertiesAlignment;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.SubClassAlignment;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.SubDataPropertyAlignment;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.SubObjectPropertyAlignment;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;
import edu.stanford.bmir.protege.web.shared.xd.results.GetInstantiationAlignmentSuggestionsResult;

public class GetInstantiationAlignmentSuggestionsHandler extends AbstractHasProjectActionHandler<GetInstantiationAlignmentSuggestionsAction,GetInstantiationAlignmentSuggestionsResult> {

	private final XdpLogger xdpLog;
	
	@Inject
	public GetInstantiationAlignmentSuggestionsHandler(OWLAPIProjectManager projectManager) {
		super(projectManager);
		this.xdpLog = XdpLogger.getInstance();
	}

	@Override
	public Class<GetInstantiationAlignmentSuggestionsAction> getActionClass() {
		return GetInstantiationAlignmentSuggestionsAction.class;
	}

	@Override
	protected RequestValidator<GetInstantiationAlignmentSuggestionsAction> getAdditionalRequestValidator(
			GetInstantiationAlignmentSuggestionsAction action,
			RequestContext requestContext) {
		return new UserHasProjectReadPermissionValidator<GetInstantiationAlignmentSuggestionsAction>();
	}
	
	private Set<OntologyEntityFrame> flattenFrameTree(FrameTreeNode<OntologyEntityFrame> treeNode) {
		Set<OntologyEntityFrame> retVal = new HashSet<OntologyEntityFrame>();
		retVal.add(treeNode.getData());
		for (FrameTreeNode<OntologyEntityFrame> childNode: treeNode.getChildren()) {
			retVal.addAll(flattenFrameTree(childNode));
		}
		return retVal;
	}
	
	/**
	 * Estimate whether two strings are sub/superterms using some very basic string matching
	 * heuristics (substring inclusion, longest joint substring, etc.)
	 * @param first
	 * @param second
	 * @return
	 */
	private Boolean isSubTerm(String first, String second) {
		
		// If either term is empty, then do not suggest subterm relationship but bail out instead
		if (first.replace(" ", "").equalsIgnoreCase("") || second.replace(" ", "").equalsIgnoreCase("")) {
			return false;
		}
		
		// If the terms are identical, suggest bi-directional subsumption, e.g. equivalence)
		if (first.equalsIgnoreCase(second)) {
			return true;
		}
		
		// If first term contains second, e.g., first is a strict extension of second, assume second
		// is superterm and first is subterm
		if (first.contains(second)) {
			return true;
		}
		
		// If the longest shared substring is greater than or equal to 4 (arbitrary choice?) 
		// and the first is longer, suggest subsumption using same rationale as above
		if (longestSubstr(first,second) > 3) {
			if (first.length() > second.length()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Estimate whether a specialization frame is a sub entity of an existing OWL entity. 
	 * @param specializationFrame
	 * @param existingEntity
	 * @param ontology
	 * @return
	 */
	private Boolean isSubEntityOf(OntologyEntityFrame specializationFrame, OWLEntity existingEntity, OWLOntology ontology) {
		String existingLabel = AnnotationOperations.getLabel(existingEntity, ontology, false);
		String existingFragment = existingEntity.getIRI().getFragment();
		String specializationLabel = specializationFrame.getCurrentLabel();
		String specializationFragment = "";
		if (specializationFrame.getIri().isPresent()) {
			specializationFragment = specializationFrame.getIri().get().getFragment();
		}
		return (isSubTerm(specializationLabel,existingLabel) || isSubTerm(specializationFragment,existingFragment));
	}
	
	/**
	 * Estimate whether an existing OWL entity is a sub entity of a specialization frame. 
	 * @param existingEntity
	 * @param specializationFrame
	 * @param ontology
	 * @return
	 */
	private Boolean isSubEntityOf(OWLEntity existingEntity, OntologyEntityFrame specializationFrame, OWLOntology ontology) {
		String existingLabel = AnnotationOperations.getLabel(existingEntity, ontology, false);
		String existingFragment = existingEntity.getIRI().getFragment();
		String specializationLabel = specializationFrame.getCurrentLabel();
		String specializationFragment = "";
		if (specializationFrame.getIri().isPresent()) {
			specializationFragment = specializationFrame.getIri().get().getFragment();
		}
		return (isSubTerm(existingLabel,specializationLabel) || isSubTerm(existingFragment,specializationFragment));
	}
	
	/**
	 * Calculate the length of the longest shared substring between two input strings.
	 * @param first
	 * @param second
	 * @return
	 */
	private static int longestSubstr(String first, String second) {
	    if (first == null || second == null || first.length() == 0 || second.length() == 0) {
	        return 0;
	    }
	    int maxLen = 0;
	    int fl = first.length();
	    int sl = second.length();
	    int[][] table = new int[fl][sl];

	    for (int i = 0; i < fl; i++) {
	        for (int j = 0; j < sl; j++) {
	            if (first.charAt(i) == second.charAt(j)) {
	                if (i == 0 || j == 0) {
	                    table[i][j] = 1;
	                }
	                else {
	                    table[i][j] = table[i - 1][j - 1] + 1;
	                }
	                if (table[i][j] > maxLen) {
	                    maxLen = table[i][j];
	                }
	            }
	        }
	    }
	    return maxLen;
	}
	
	/**
	 * Compare every combination of input specialization ontology entity frame and existing OWL entity and suggest any
	 * possible equivalency or subsumption alignments.
	 * @param specializationFrames
	 * @param ontologyEntites
	 * @param ontology
	 * @return
	 */
	private Set<Alignment> generateCandidateAlignments(Set<OntologyEntityFrame> specializationFrames, Set<? extends OWLEntity> ontologyEntites, OWLOntology ontology) {
		Set<Alignment> retVal = new HashSet<Alignment>();
		for (OntologyEntityFrame specializedFrame: specializationFrames) {
			for (OWLEntity ontologyEntity: ontologyEntites) {
				
				// By default neither entity is subclass of other
				Boolean specializationIsSubEntity = false;
				Boolean existingIsSubEntity = false;
				Boolean entitiesAreEquivalent = false;
				
				Boolean entitiesHaveSameNamespace = false;
				// If frame has an IRI already, compare the IRIs
				if (specializedFrame.getIri().isPresent()) {
					IRI specializedFrameIri = specializedFrame.getIri().get();
					IRI ontologyEntityIri = ontologyEntity.getIRI();
					if (specializedFrameIri.getNamespace().equalsIgnoreCase(ontologyEntityIri.getNamespace())) {
						entitiesHaveSameNamespace = true;
					}
				}
				
				// Do not suggest alignments for entities which are from the same ontology namespace to begin 
				// with (assume that that namespace is reasonably structured at the outset..)
				if (!entitiesHaveSameNamespace) {
					specializationIsSubEntity = isSubEntityOf(specializedFrame, ontologyEntity, ontology);
					existingIsSubEntity = isSubEntityOf(ontologyEntity, specializedFrame, ontology);
					entitiesAreEquivalent = specializationIsSubEntity && existingIsSubEntity;
				}
				
				if (entitiesAreEquivalent) {
					// Construct equality alignment
					OntologyEntityFrame ontologyFrame = OntologyOperations.getFrame(ontologyEntity, ontology, false);
					if (specializedFrame instanceof ClassFrame) {
						retVal.add(new EquivalentClassesAlignment((ClassFrame)specializedFrame, (ClassFrame)ontologyFrame));
					}
					else if (specializedFrame instanceof DataPropertyFrame) {
						retVal.add(new EquivalentDataPropertiesAlignment((DataPropertyFrame)specializedFrame, (DataPropertyFrame)ontologyFrame));
					}
					else if (specializedFrame instanceof ObjectPropertyFrame) {
						retVal.add(new EquivalentObjectPropertiesAlignment((ObjectPropertyFrame)specializedFrame, (ObjectPropertyFrame)ontologyFrame));
					}
				}
				if (specializationIsSubEntity) {
					// Construct one-way subsumption
					OntologyEntityFrame ontologyFrame = OntologyOperations.getFrame(ontologyEntity, ontology, false);
					if (specializedFrame instanceof ClassFrame) {
						retVal.add(new SubClassAlignment((ClassFrame)ontologyFrame, (ClassFrame)specializedFrame));
					}
					else if (specializedFrame instanceof DataPropertyFrame) {
						retVal.add(new SubDataPropertyAlignment((DataPropertyFrame)ontologyFrame, (DataPropertyFrame)specializedFrame));
					}
					else if (specializedFrame instanceof ObjectPropertyFrame) {
						retVal.add(new SubObjectPropertyAlignment((ObjectPropertyFrame)ontologyFrame, (ObjectPropertyFrame)specializedFrame));
					}
				}
				if (existingIsSubEntity) {
					// Construct other way subsumption
					OntologyEntityFrame ontologyFrame = OntologyOperations.getFrame(ontologyEntity, ontology, false);
					if (specializedFrame instanceof ClassFrame) {
						retVal.add(new SubClassAlignment((ClassFrame)specializedFrame, (ClassFrame)ontologyFrame));
					}
					else if (specializedFrame instanceof DataPropertyFrame) {
						retVal.add(new SubDataPropertyAlignment((DataPropertyFrame)specializedFrame, (DataPropertyFrame)ontologyFrame));
					}
					else if (specializedFrame instanceof ObjectPropertyFrame) {
						retVal.add(new SubObjectPropertyAlignment((ObjectPropertyFrame)specializedFrame, (ObjectPropertyFrame)ontologyFrame));
					}
				}
			}
		}
		return retVal;
	}
	
	private Set<OntologyEntityFrame> pruneNonClonedFrames(Set<OntologyEntityFrame> inputFrames) {
		Iterator<OntologyEntityFrame> iterator = inputFrames.iterator();
		while (iterator.hasNext()) {
			OntologyEntityFrame frame = iterator.next();
			if (!frame.getClonedLabel().isPresent()) {
				iterator.remove();
			}
		}
		return inputFrames;
	}
	
	@Override
	protected GetInstantiationAlignmentSuggestionsResult execute(GetInstantiationAlignmentSuggestionsAction action,
			OWLAPIProject project, ExecutionContext executionContext) {
		
		OWLOntology ontology = project.getRootOntology();
		Set<OntologyEntityFrame> instantiationClasses = flattenFrameTree(action.getClasses());
		Set<OntologyEntityFrame> instantiationObjectProperties = flattenFrameTree(action.getObjectProperties());
		Set<OntologyEntityFrame> instantiationDataProperties = flattenFrameTree(action.getDataProperties());
		
		// 0. If doing template-based instantiation, only generate alignment suggestions for the frames that have been cloned
		if (action.getInstantiationMethod() == CodpInstantiationMethod.TEMPLATE_BASED) {
			instantiationClasses = pruneNonClonedFrames(instantiationClasses);
			instantiationObjectProperties = pruneNonClonedFrames(instantiationObjectProperties);
			instantiationDataProperties = pruneNonClonedFrames(instantiationDataProperties);
		}
		
		// 1. Create return value
		Set<Alignment> alignments = new HashSet<Alignment>();
		
		// 2. Generate class alignment suggestions
		Set<OWLClass> allOntologyClasses = ontology.getClassesInSignature(true);
		OWLClass owlThing = DataFactory.getOWLThing();
		allOntologyClasses.remove(owlThing);
		OWLClass owlNothing = DataFactory.getOWLClass(IRI.create("http://www.w3.org/2002/07/owl#Nothing"));
		allOntologyClasses.remove(owlNothing);
		alignments.addAll(generateCandidateAlignments(instantiationClasses, allOntologyClasses, ontology));

		// 3. Generate object property alignment suggestions
		Set<OWLObjectProperty> allOntologyObjectProperties = ontology.getObjectPropertiesInSignature(true);
		alignments.addAll(generateCandidateAlignments(instantiationObjectProperties, allOntologyObjectProperties, ontology));
		
		// 4. Generate data property alignment suggestions
		Set<OWLDataProperty> allOntologyDataProperties = ontology.getDataPropertiesInSignature(true);
		alignments.addAll(generateCandidateAlignments(instantiationDataProperties, allOntologyDataProperties, ontology));

		// 5. Log generated suggestions for later analysis
		xdpLog.logSuggestedOdpAlignments(executionContext.getUserId(), project, alignments);
		
		return new GetInstantiationAlignmentSuggestionsResult(alignments);
	}
}
