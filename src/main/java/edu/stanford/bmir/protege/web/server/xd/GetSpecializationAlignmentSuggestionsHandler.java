package edu.stanford.bmir.protege.web.server.xd;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.inject.Inject;

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
import edu.stanford.bmir.protege.web.server.xd.util.OntologyOperations;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetSpecializationAlignmentSuggestionsAction;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.EquivalentClassesAlignment;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.SubClassAlignment;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;
import edu.stanford.bmir.protege.web.shared.xd.results.GetSpecializationAlignmentSuggestionsResult;

public class GetSpecializationAlignmentSuggestionsHandler extends AbstractHasProjectActionHandler<GetSpecializationAlignmentSuggestionsAction,GetSpecializationAlignmentSuggestionsResult> {

	@Inject
	public GetSpecializationAlignmentSuggestionsHandler(OWLAPIProjectManager projectManager) {
		super(projectManager);
	}

	@Override
	public Class<GetSpecializationAlignmentSuggestionsAction> getActionClass() {
		return GetSpecializationAlignmentSuggestionsAction.class;
	}

	@Override
	protected RequestValidator<GetSpecializationAlignmentSuggestionsAction> getAdditionalRequestValidator(
			GetSpecializationAlignmentSuggestionsAction action,
			RequestContext requestContext) {
		return new UserHasProjectReadPermissionValidator<GetSpecializationAlignmentSuggestionsAction>();
	}
	
	private Set<OntologyEntityFrame> flattenFrameTree(FrameTreeNode<OntologyEntityFrame> treeNode) {
		Set<OntologyEntityFrame> retVal = new HashSet<OntologyEntityFrame>();
		retVal.add(treeNode.getData());
		for (FrameTreeNode<OntologyEntityFrame> childNode: treeNode.getChildren()) {
			retVal.addAll(flattenFrameTree(childNode));
		}
		return retVal;
	}
	
	private Boolean isSubEntityOf(OntologyEntityFrame specializationFrame, OWLEntity existingEntity) {
		// TODO: Build this
		Random rand = new Random();
		return rand.nextBoolean();
	}
	
	private Boolean isSubEntityOf(OWLEntity existingEntity, OntologyEntityFrame specializationFrame) {
		// TODO: Build this
		Random rand = new Random();
		return rand.nextBoolean();
	}
	
	@Override
	protected GetSpecializationAlignmentSuggestionsResult execute(GetSpecializationAlignmentSuggestionsAction action,
			OWLAPIProject project, ExecutionContext executionContext) {
		Set<Alignment> alignments = new HashSet<Alignment>();
		
		OWLOntology ontology = project.getRootOntology();
		
		// 1. Generate class alignment suggestions
		Set<OntologyEntityFrame> specializationClasses = flattenFrameTree(action.getClasses());
		Set<OWLClass> allOntologyClasses = ontology.getClassesInSignature(true);
		for (OntologyEntityFrame specializedClass: specializationClasses) {
			for (OWLClass ontologyClass: allOntologyClasses) {
				Boolean specializationIsSubEntity = isSubEntityOf(specializedClass, ontologyClass);
				Boolean existingIsSubEntity = isSubEntityOf(ontologyClass, specializedClass);
				
				if (specializationIsSubEntity && existingIsSubEntity) {
					// Construct equality alignment
					OntologyEntityFrame ontologyFrame = OntologyOperations.getFrame(ontologyClass, ontology, false);
					alignments.add(new EquivalentClassesAlignment((ClassFrame)specializedClass, (ClassFrame)ontologyFrame));
				}
				else if (specializationIsSubEntity) {
					// Construct one-way subsumption
					OntologyEntityFrame ontologyFrame = OntologyOperations.getFrame(ontologyClass, ontology, false);
					alignments.add(new SubClassAlignment((ClassFrame)ontologyFrame, (ClassFrame)specializedClass));
				}
				else if (existingIsSubEntity) {
					// Construct other way subsumption
					OntologyEntityFrame ontologyFrame = OntologyOperations.getFrame(ontologyClass, ontology, false);
					alignments.add(new SubClassAlignment((ClassFrame)specializedClass, (ClassFrame)ontologyFrame));
				}
			}
		}
		
		// 2. Generate data property alignment suggestions
		Set<OntologyEntityFrame> specializationDataProperties = flattenFrameTree(action.getDataProperties());
		Set<OWLDataProperty> allOntologyDataProperties = ontology.getDataPropertiesInSignature(true);
		// TODO: Implement the below
		
		// 3. Generate object property alignment suggestions
		Set<OntologyEntityFrame> specializationObjectProperties = flattenFrameTree(action.getObjectProperties());
		Set<OWLObjectProperty> allOntologyObjectProperties = ontology.getObjectPropertiesInSignature(true);
		// TODO: Implement the below
		
		return new GetSpecializationAlignmentSuggestionsResult(alignments);
	}
}
