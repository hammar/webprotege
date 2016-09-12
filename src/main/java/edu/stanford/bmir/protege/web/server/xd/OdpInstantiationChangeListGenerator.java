package edu.stanford.bmir.protege.web.server.xd;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Optional;

import edu.stanford.bmir.protege.web.server.change.ChangeGenerationContext;
import edu.stanford.bmir.protege.web.server.change.ChangeListGenerator;
import edu.stanford.bmir.protege.web.server.change.OntologyChangeList;
import edu.stanford.bmir.protege.web.server.logging.WebProtegeLogger;
import edu.stanford.bmir.protege.web.server.owlapi.OWLAPIProject;
import edu.stanford.bmir.protege.web.server.owlapi.RenameMap;
import edu.stanford.bmir.protege.web.shared.DataFactory;
import edu.stanford.bmir.protege.web.shared.xd.data.CodpInstantiationMethod;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.LabelOrIri;
import edu.stanford.bmir.protege.web.shared.xd.data.CodpInstantiation;
import edu.stanford.bmir.protege.web.shared.xd.data.PropertyRestriction;
import edu.stanford.bmir.protege.web.shared.xd.data.PropertyRestriction.ValueConstraint;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.AbstractEquivalenceAlignment;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.AbstractSubsumptionAlignment;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.PropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.util.TreeMethods;

public class OdpInstantiationChangeListGenerator implements ChangeListGenerator<OWLEntity> {

	
	@Inject private WebProtegeLogger log;
	private String XdpServiceUriBase;
	
	private Map<String,OWLEntity> freshEntities;
	private Map<IRI,String> clonedClassesToNewLabelsMap;
	private Set<OWLOntology> odpClosure;
	
	public OdpInstantiationChangeListGenerator(CodpInstantiation instantiation) {
		super();
		
		this.instantiation = instantiation;
		this.freshEntities = new HashMap<String,OWLEntity>();
		this.clonedClassesToNewLabelsMap = new HashMap<IRI,String>();
		
		try {
			Properties XdServiceProperties = new Properties();
			XdServiceProperties.load(GetOdpContentsHandler.class.getResourceAsStream("XdpService.properties"));
			XdpServiceUriBase = XdServiceProperties.getProperty("XdpServiceUriBase");
		} 
		catch (IOException e) {
			XdpServiceUriBase = "http://localhost:8080";
			log.info(String.format("Unable to find configuration file %s. Setting XdpServiceUriBase to default value %s.", "xdService.properties", XdpServiceUriBase));
		}
	}

	private CodpInstantiation instantiation;
	
	/**
	 *  Returns a filtered subset of the input set of class or property trees, containing
	 *  only those that have been specialized in this wizard (e.g., those that do not have
	 *  any minted IRI), flattened to a set of root nodes.
	 * @param inputFrameTreeRoot - root node to search through
	 * @return
	 */
	private Set<FrameTreeNode<OntologyEntityFrame>> getSpecializedEntityTrees(FrameTreeNode<OntologyEntityFrame> inputFrameTreeRoot) {
		Set<FrameTreeNode<OntologyEntityFrame>> specializedEntityRoots = new HashSet<FrameTreeNode<OntologyEntityFrame>>();
		// This node has an IRI, e.g., is a pre-existing concept in the ontology, e.g., recurse deeper into child nodes.
		if (inputFrameTreeRoot.getData().getIri().isPresent()) {
			for (FrameTreeNode<OntologyEntityFrame> childNode: inputFrameTreeRoot.getChildren()) {
				specializedEntityRoots.addAll(getSpecializedEntityTrees(childNode));
			}
		}
		// This node has no IRI so it is a specialized subtree. Add it to the set to be returned.
		else {
			specializedEntityRoots.add(inputFrameTreeRoot);
		}
		return specializedEntityRoots;
	}
	
	
	/**
	 *  Returns a filtered subset of the input set of class or property trees, containing
	 *  only those that have been cloned in this wizard (e.g., those that have a cloned label), 
	 *  flattened to a set of root nodes.
	 * @param inputFrameTreeRoot - root node to search through
	 * @return
	 */
	private Set<FrameTreeNode<OntologyEntityFrame>> getClonedEntityTrees(FrameTreeNode<OntologyEntityFrame> inputFrameTreeRoot) {
		
		FrameTreeNode<OntologyEntityFrame> clonedTree = TreeMethods.filterTreeKeepingClonedEntities(inputFrameTreeRoot,null);
		Set<FrameTreeNode<OntologyEntityFrame>> retVal = new HashSet<FrameTreeNode<OntologyEntityFrame>>(clonedTree.getChildren());
		return retVal;
		/*
		Set<FrameTreeNode<OntologyEntityFrame>> clonedEntityRoots = new HashSet<FrameTreeNode<OntologyEntityFrame>>();
		// This node has no cloned label, e.g., is an ODP-level construct not to be included in target instantiation, e.g., recurse deeper into child nodes.
		if (!inputFrameTreeRoot.getData().getClonedLabel().isPresent()) {
			for (FrameTreeNode<OntologyEntityFrame> childNode: inputFrameTreeRoot.getChildren()) {
				clonedEntityRoots.addAll(getSpecializedEntityTrees(childNode));
			}
		}
		// This node has a cloned label so it is a cloned subtree. Add it to the set to be returned.
		else {
			clonedEntityRoots.add(inputFrameTreeRoot);
		}
		return clonedEntityRoots;*/
	}
	
	
	/**
	 * Construct an ontology change list builder using the specialization strategy, i.e. by importing the
	 * entire existing ODP transitive closure and adding on subclasses and subproperties when instantiating
	 * the ODP.
	 * @param project
	 * @return
	 */
	private OntologyChangeList.Builder<OWLEntity> makeBuilderBySpecialization(OWLAPIProject project) {
		OntologyChangeList.Builder<OWLEntity> builder = new OntologyChangeList.Builder<OWLEntity>();
		
		// Load ODP closure
		if (odpClosure == null) {
			odpClosure = getOdpClosure(instantiation.getOdpIri());
		}
		
		// For every axiom in ODP closure, add to change builder (e.g., copy the ODP closure into the target ontology)
		// But filter out annotation property declarations and ODP metadata cruft
		for (OWLOntology odpImport: odpClosure) {
			if (odpImport.getOntologyID().getOntologyIRI().toString().contains("http://www.ontologydesignpatterns.org/schemas/cpannotationschema.owl")) {
				continue;
			}
			Set<OWLAxiom> allOdpImportAxioms = odpImport.getAxioms();
			for (OWLAxiom axiom: allOdpImportAxioms) {
				Boolean includeAxiom = true;
				if (axiom.getAxiomType() == AxiomType.DECLARATION) {
					// Filter out some stuff we don't want
					OWLEntity declaredEntity = ((OWLDeclarationAxiom) axiom).getEntity();
					IRI entityIri = declaredEntity.getIRI();
					if (entityIri.isReservedVocabulary()) {
						includeAxiom = false;
					}
				}
				if (includeAxiom) {
					builder.addAxiom(project.getRootOntology(), axiom);
				}
			}
		}
		
		// 0. Now filter out only the specialized frames, i.e., those that do not yet have a minted IRI for further processing
		Set<FrameTreeNode<OntologyEntityFrame>> specializedClasses = getSpecializedEntityTrees(instantiation.getClassFrameTree());
		Set<FrameTreeNode<OntologyEntityFrame>> specializedObjectProperties = getSpecializedEntityTrees(instantiation.getObjectPropertyFrameTree());
		Set<FrameTreeNode<OntologyEntityFrame>> specializedDataProperties = getSpecializedEntityTrees(instantiation.getDataPropertyFrameTree());
		
		// 1. Generate and add creation axioms for each of the frame tree sets
		generateAndAddFrameTreeCreationAxioms(project, builder, specializedClasses);
		generateAndAddFrameTreeCreationAxioms(project, builder, specializedObjectProperties);
		generateAndAddFrameTreeCreationAxioms(project, builder, specializedDataProperties);
		
		// 2. Create existential/universal restriction axioms on classes using properties, ignoring top node
		for (FrameTreeNode<OntologyEntityFrame> classTree: specializedClasses) {
			Set<OWLAxiom> axioms = generateComplexRestrictionAxioms(project, classTree);
			for (OWLAxiom axiom: axioms) {
				builder.addAxiom(project.getRootOntology(), axiom);
			}
		}
		
		// 3. Create alignment axioms
		generateAndAddAlignmentAxioms(project, builder);
		
		return builder;
	}
	
	/**
	 * Using the set of alignments held within this change list generator's instantiation member variable, 
	 * create equivalent OWL subsumption or equivalence axioms. NOTE that for this to work properly the generateAndAddFrameTreeCreationAxioms
	 * method must first have been such that all classes, object properties, and datatype properties that the
	 * instantiation creates have been placed in the freshEntities member variable. 
	 * @param project
	 * @param builder
	 */
	private void generateAndAddAlignmentAxioms(OWLAPIProject project, OntologyChangeList.Builder<OWLEntity> builder) {
		for (Alignment alignment: instantiation.getAlignments()) {
			if (alignment instanceof AbstractSubsumptionAlignment) {
				OntologyEntityFrame superFrame = ((AbstractSubsumptionAlignment) alignment).getSuperEntity();
				OntologyEntityFrame subFrame = ((AbstractSubsumptionAlignment) alignment).getSubEntity();
				OWLEntity superEntity = getEntityFromFrame(superFrame);
				OWLEntity subEntity = getEntityFromFrame(subFrame);
				Optional<OWLAxiom> subsumptionAxiom = generateSubsumptionAxiom(project,superEntity,subEntity);
				if (subsumptionAxiom.isPresent()) {
					builder.addAxiom(project.getRootOntology(), subsumptionAxiom.get());
				}
			}
			else if (alignment instanceof AbstractEquivalenceAlignment) {
				OntologyEntityFrame homeFrame = ((AbstractEquivalenceAlignment) alignment).getHomeEntity();
				OntologyEntityFrame otherFrame = ((AbstractEquivalenceAlignment) alignment).getOtherEntity();
				OWLEntity homeEntity = getEntityFromFrame(homeFrame);
				OWLEntity otherEntity = getEntityFromFrame(otherFrame);
				Optional<OWLAxiom> equivalenceAxiom = generateEquivalenceAxiom(project,homeEntity,otherEntity);
				if (equivalenceAxiom.isPresent()) {
					builder.addAxiom(project.getRootOntology(), equivalenceAxiom.get());
				}
			}
		}
	}
	
	/**
	 * From a set of input ontology entity frame trees, generate the equivalent OWL definition axioms, including domain and ranges
	 * of properties (NOTE! that for the latter to work, this method MUST first have been run over class frame entity trees, so that
	 * any newly minted classes exist within the freshEntities member variable on this change list generator object).  
	 * @param project
	 * @param builder
	 * @param frameTrees
	 */
	private void generateAndAddFrameTreeCreationAxioms(OWLAPIProject project, OntologyChangeList.Builder<OWLEntity> builder, Set<FrameTreeNode<OntologyEntityFrame>> frameTrees) {
		for (FrameTreeNode<OntologyEntityFrame> frameTree: frameTrees) {
			Set<OWLAxiom> axioms = generateFrameTreeCreationAxioms(project, frameTree, Optional.<OWLEntity>absent());
			for (OWLAxiom axiom: axioms) {
				builder.addAxiom(project.getRootOntology(), axiom);
			}
		}
	}	

	
	/**
	 * Recursive update a property frame tree so that domain and ranges are translated from CODP entity IRIs into
	 * the new labels of the cloned entities created using the template-based instantiation method.
	 * @param inputFrameTree
	 */
	private void updatePropertyDomainAndRanges(FrameTreeNode<OntologyEntityFrame> inputFrameTree) {
		if (inputFrameTree.getData() instanceof PropertyFrame) {
			PropertyFrame pf = (PropertyFrame)inputFrameTree.getData();
			
			// Update property frame domains
			Set<LabelOrIri> domains = pf.getDomains();
			for (LabelOrIri domain: domains) {
				if (domain.getIri().isPresent()) {
					IRI domainIri = domain.getIri().get();
					// If the class which is domain of this property has been cloned, update domain to point
					// to the cloned label rather than the original IRI
					if (this.clonedClassesToNewLabelsMap.containsKey(domainIri)) {
						domain.removeIri();
						domain.setLabel(this.clonedClassesToNewLabelsMap.get(domainIri));
					}
					else {
						Optional<FrameTreeNode<OntologyEntityFrame>> domainSubFrameTree = TreeMethods.getSubFrameTreeByIri(domainIri, instantiation.getClassFrameTree());
						if (domainSubFrameTree.isPresent()) {
							OntologyEntityFrame narrowerDomain = TreeMethods.getNarrowestFrame(domainIri, domainSubFrameTree.get());
							domain.removeIri();
							domain.setLabel(narrowerDomain.getCurrentLabel());
						}
					}
				}
			}
			
			// Update object property frame ranges
			if (pf instanceof ObjectPropertyFrame) {
				ObjectPropertyFrame opf = (ObjectPropertyFrame)pf;
				Set<LabelOrIri> ranges = opf.getRanges();
				for (LabelOrIri range: ranges) {
					if (range.getIri().isPresent()) {
						IRI rangeIri = range.getIri().get();
						// If the class which is range of this property has been cloned, update range to point
						// to the cloned label rather than the original IRI
						if (this.clonedClassesToNewLabelsMap.containsKey(range)) {
							range.removeIri();
							range.setLabel(this.clonedClassesToNewLabelsMap.get(rangeIri));
						}
						else {
							Optional<FrameTreeNode<OntologyEntityFrame>> rangeSubFrameTree = TreeMethods.getSubFrameTreeByIri(rangeIri, instantiation.getClassFrameTree());
							if (rangeSubFrameTree.isPresent()) {
								OntologyEntityFrame narrowerDomain = TreeMethods.getNarrowestFrame(rangeIri, rangeSubFrameTree.get());
								range.removeIri();
								range.setLabel(narrowerDomain.getCurrentLabel());
							}
						}
					}
				}
			}
		}
		
		for (FrameTreeNode<OntologyEntityFrame> childFrameTree: inputFrameTree.getChildren()) {
			updatePropertyDomainAndRanges(childFrameTree);
		}
	}
	
	/**
	 * Make an ontology change list builder by adhering to the template-based instantiation method, i.e., by
	 * cloning only some of the entities from scratch, regardless of existing ODP imports hierarchy or IRIs, but keeping
	 * the conceptual structure of the CODP.
	 * @param project
	 * @return
	 */
	private OntologyChangeList.Builder<OWLEntity> makeBuilderByTemplate(OWLAPIProject project) {
		OntologyChangeList.Builder<OWLEntity> builder = new OntologyChangeList.Builder<OWLEntity>();
		
		// 1. Prune the input trees, keeping only such entities which have cloned labels (e.g., have been selected by
		// client side code to be instantiated into the final ontology).
		Set<FrameTreeNode<OntologyEntityFrame>> clonedClasses = getClonedEntityTrees(instantiation.getClassFrameTree());
		Set<FrameTreeNode<OntologyEntityFrame>> clonedObjectProperties = getClonedEntityTrees(instantiation.getObjectPropertyFrameTree());
		Set<FrameTreeNode<OntologyEntityFrame>> clonedDataProperties = getClonedEntityTrees(instantiation.getDataPropertyFrameTree());

		// 2. Generate and add class creation axioms.
		generateAndAddFrameTreeCreationAxioms(project, builder, clonedClasses);
		
		// 3. Update the property trees - remove any domain or range references to existing class IRIs, instead replacing them
		// by references to the newly cloned class labels, so that the generateAndAddFrameTreeCreationAxioms method can work on these
		// trees just as if they were trees from the specialization based approach.
		for (FrameTreeNode<OntologyEntityFrame> clonedObjectPropertyTree: clonedObjectProperties) {
			updatePropertyDomainAndRanges(clonedObjectPropertyTree);
		}
		for (FrameTreeNode<OntologyEntityFrame> clonedDataPropertyTree: clonedDataProperties) {
			updatePropertyDomainAndRanges(clonedDataPropertyTree);
		}
		
		// 4. Generate and add property creation axioms.
		generateAndAddFrameTreeCreationAxioms(project, builder, clonedObjectProperties);
		generateAndAddFrameTreeCreationAxioms(project, builder, clonedDataProperties);
		
		// 5. Create existential/universal restriction axioms on classes using properties, ignoring top node
		for (FrameTreeNode<OntologyEntityFrame> classTree: clonedClasses) {
			Set<OWLAxiom> axioms = generateComplexRestrictionAxioms(project, classTree);
			for (OWLAxiom axiom: axioms) {
				builder.addAxiom(project.getRootOntology(), axiom);
			}
		}
		
		// 6. Create alignment axioms
		generateAndAddAlignmentAxioms(project, builder);
		
		return builder;
	}
	
	
	@Override
	public OntologyChangeList<OWLEntity> generateChanges(OWLAPIProject project, ChangeGenerationContext context) {		
		
		// Initiate change builder
		OntologyChangeList.Builder<OWLEntity> builder;
		
		if (this.instantiation.getInstantiationMethod() == CodpInstantiationMethod.TEMPLATE_BASED) {
			builder = makeBuilderByTemplate(project);
		}
		else {
			builder = makeBuilderBySpecialization(project);
		}
		
		// Build change list and return it
		return builder.build();
	}

	private Optional<OWLQuantifiedObjectRestriction> generateObjectPropertyRestriction(OWLAPIProject project, PropertyRestriction pr) {
		OWLQuantifiedObjectRestriction restriction;
		ObjectPropertyFrame propertyFrame = pr.getProperty();
		ClassFrame targetFrame = pr.getTarget();
		ValueConstraint vc = pr.getValueConstraint();
		OWLObjectProperty property = (OWLObjectProperty)getEntityFromFrame(propertyFrame);
		OWLClass targetClass = (OWLClass)getEntityFromFrame(targetFrame);
		if (vc == ValueConstraint.ONLY) {
			restriction = project.getDataFactory().getOWLObjectAllValuesFrom(property, targetClass);
			return Optional.of(restriction);
		}
		else if (vc == ValueConstraint.SOME) {
			restriction = project.getDataFactory().getOWLObjectSomeValuesFrom(property, targetClass);
			return Optional.of(restriction);
		}
		else {
			return Optional.absent();
		}
	}
	
	private Set<OWLAxiom> generateComplexRestrictionAxioms(OWLAPIProject project, FrameTreeNode<OntologyEntityFrame> startNode) {
		Set<OWLAxiom> allAxioms = new HashSet<OWLAxiom>();
		OntologyEntityFrame currentFrame = startNode.getData();
		if (currentFrame instanceof ClassFrame){
			OWLClass currentClass = (OWLClass)getEntityFromFrame(currentFrame);
			for (PropertyRestriction pr: ((ClassFrame) currentFrame).getEquivalentToRestrictions()) {
				// Create equivalent class property restriction axioms
				Optional<OWLQuantifiedObjectRestriction> restriction = generateObjectPropertyRestriction(project, pr);
				if (restriction.isPresent()) {
					allAxioms.add(project.getDataFactory().getOWLEquivalentClassesAxiom(currentClass, restriction.get()));
				}
			}
			for (PropertyRestriction pr: ((ClassFrame) currentFrame).getSubClassOfRestrictions()) {
				// Create subclass of restriction axioms
				Optional<OWLQuantifiedObjectRestriction> restriction = generateObjectPropertyRestriction(project, pr);
				if (restriction.isPresent()) {
					allAxioms.add(project.getDataFactory().getOWLSubClassOfAxiom(currentClass, restriction.get()));
				}
			}
		}
		
		// Recurse through children
		for (FrameTreeNode<OntologyEntityFrame> childNode: startNode.getChildren()) {
			allAxioms.addAll(generateComplexRestrictionAxioms(project, childNode));
		}
		return allAxioms;
	}


	/**
	 * Takes two OWL Entities (classes, data properties, or object properties) as input and returns an equivalence axiom
	 * such that the two are equivalent. If the two input entities are not of the same type, the optional that is returned 
	 * will be empty.
	 * @param project
	 * @param firstEntity
	 * @param secondEntity
	 * @return
	 */
	private Optional<OWLAxiom> generateEquivalenceAxiom(OWLAPIProject project, OWLEntity firstEntity, OWLEntity secondEntity) {
		if (firstEntity.getClass() != secondEntity.getClass()) {
			return Optional.absent();
		}
		OWLAxiom equivalenceAxiom = null;
		if (firstEntity instanceof OWLClass) {
			equivalenceAxiom = project.getDataFactory().getOWLEquivalentClassesAxiom((OWLClass)firstEntity, (OWLClass)secondEntity);
		}
		else if (firstEntity instanceof OWLDataProperty) {
			equivalenceAxiom = project.getDataFactory().getOWLEquivalentDataPropertiesAxiom((OWLDataProperty)firstEntity, (OWLDataProperty)secondEntity);
		}
		else if (firstEntity instanceof OWLObjectProperty ){
			equivalenceAxiom = project.getDataFactory().getOWLEquivalentObjectPropertiesAxiom((OWLObjectProperty)firstEntity, (OWLObjectProperty)secondEntity);
		}
		return Optional.fromNullable(equivalenceAxiom);
	}

	
	/**
	 * Takes two OWL Entities (classes, data properties, or object properties) as input and returns a subsumption axiom
	 * such that one is a subclass/subdataproperty/subobjectproperty of the other. If the two input entities are not of
	 * the same type, the optional that is returned will be empty.
	 * @param project
	 * @param superEntity
	 * @param subEntity
	 * @return
	 */
	private Optional<OWLAxiom> generateSubsumptionAxiom(OWLAPIProject project, OWLEntity superEntity, OWLEntity subEntity) {
		if (superEntity.getClass() != subEntity.getClass()) {
			return Optional.absent();
		}
		OWLAxiom subsumptionAxiom = null;
		if (subEntity instanceof OWLClass) {
			subsumptionAxiom = project.getDataFactory().getOWLSubClassOfAxiom((OWLClass)subEntity, (OWLClass)superEntity);
		}
		else if (subEntity instanceof OWLDataProperty) {
			subsumptionAxiom = project.getDataFactory().getOWLSubDataPropertyOfAxiom((OWLDataProperty)subEntity, (OWLDataProperty)superEntity);
		}
		else if (subEntity instanceof OWLObjectProperty ){
			subsumptionAxiom = project.getDataFactory().getOWLSubObjectPropertyOfAxiom((OWLObjectProperty)subEntity, (OWLObjectProperty)superEntity);
		}
		return Optional.fromNullable(subsumptionAxiom);
	}

	public Map<String,String> getPrefixes() {
		// TODO: Fix this to only include reasonable prefixes if template-based instantiation.
		Map<String,String> prefixes = new HashMap<String,String>();
		if (odpClosure == null) {
			odpClosure = getOdpClosure(instantiation.getOdpIri());
		}
		for (OWLOntology odpImport: odpClosure) {
			OWLOntologyFormat format = odpImport.getOWLOntologyManager().getOntologyFormat(odpImport);
			if (format.isPrefixOWLOntologyFormat()) {
				Map<String,String> odpImportPrefixMap = format.asPrefixOWLOntologyFormat().getPrefixName2PrefixMap();
				for (Entry<String,String> mapping: odpImportPrefixMap.entrySet()) {
					String key = mapping.getKey();
					String value = mapping.getValue();
					
					// Don't include the ODP annotation namespace
					if (!value.contains("http://www.ontologydesignpatterns.org/schemas/cpannotationschema.owl#")) {
						
						if (key == ":") {
							// Default prefix - create new prefix mapping from ontology URI
							String odpImportFragment = odpImport.getOntologyID().getOntologyIRI().getFragment();
							prefixes.put(odpImportFragment, value);
						}
						else if (!(prefixes.containsKey(key) || prefixes.containsValue(value))) {
							// Neither prefix name nor value already exists, so create them
							prefixes.put(key, value);
						}
						else if (prefixes.containsKey(key) && !prefixes.get(key).equalsIgnoreCase(value)) {
							// Prefix already exists but value differs - set new semi-random prefix
							Random rand = new Random();
							String newKey = key + rand.nextInt(10000);
							prefixes.put(newKey, value);
						}
					}
				}
			}
		}
		return prefixes;
	}
	
	
	/**
	 * Creates a set of OWL axioms that make up a subsumption hierarchy (of classes, data properties, 
	 * or object properties) from recursively walking a tree of OntologyEntityFrame objects.
	 * @param project
	 * @param tree
	 * @param parent
	 * @return
	 */
	private Set<OWLAxiom> generateFrameTreeCreationAxioms(OWLAPIProject project, FrameTreeNode<OntologyEntityFrame> tree, Optional<OWLEntity> parent) {
		Set<OWLAxiom> allAxioms = new HashSet<OWLAxiom>();
		
		// Get data about the current frame
		OntologyEntityFrame frame = tree.getData();
		String entityLabel = frame.getCurrentLabel();
		
		// Generate an entity declaration axiom of the correct type
		OWLEntity freshEntity;
		if (frame instanceof ClassFrame) {
			freshEntity = DataFactory.getFreshOWLEntity(EntityType.CLASS, entityLabel);
			if (frame.getClonedLabel().isPresent() && frame.getIri().isPresent()) {
				this.clonedClassesToNewLabelsMap.put(frame.getIri().get(), entityLabel);
			}
		}
		else if (frame instanceof DataPropertyFrame) {
			freshEntity = DataFactory.getFreshOWLEntity(EntityType.DATA_PROPERTY, entityLabel);
		}
		else {
			freshEntity = DataFactory.getFreshOWLEntity(EntityType.OBJECT_PROPERTY, entityLabel);
		}
		freshEntities.put(entityLabel, freshEntity);
		allAxioms.add(project.getDataFactory().getOWLDeclarationAxiom(freshEntity));
		
		
		// If there is a parent entity, create an appropriate subsumption axiom to link these up 
		if (parent.isPresent()) {
			OWLAxiom subsumptionAxiom;
			if (freshEntity instanceof OWLClass) {
				subsumptionAxiom = project.getDataFactory().getOWLSubClassOfAxiom((OWLClass)freshEntity, (OWLClass)parent.get());
			}
			else if (freshEntity instanceof OWLDataProperty) {
				subsumptionAxiom = project.getDataFactory().getOWLSubDataPropertyOfAxiom((OWLDataProperty)freshEntity, (OWLDataProperty)parent.get());
			}
			else {
				subsumptionAxiom = project.getDataFactory().getOWLSubObjectPropertyOfAxiom((OWLObjectProperty)freshEntity, (OWLObjectProperty)parent.get());
			}
			allAxioms.add(subsumptionAxiom);
		}
		
		
		// If the frame is a property frame, create RDFS domain/range restrictions
		// Note that this assumes class definition axioms have already been created!
		if (frame instanceof PropertyFrame) {
			
			// First we do rdfs:domain
			Set<LabelOrIri> domains = ((PropertyFrame) frame).getDomains();
			for (LabelOrIri domainFromFrame: domains) {
				OWLClass domain;
				if (domainFromFrame.getIri().isPresent()) {
					// Get domain OWL class from frame IRI 
					domain = DataFactory.getOWLClass(domainFromFrame.getIri().get());
				}
				else {
					// Get domain OWL class from freshentities matching label
					domain = (OWLClass)freshEntities.get(domainFromFrame.getLabel().get());
				}
				if (domain != null) {
					OWLAxiom domainAxiom;
					if (frame instanceof DataPropertyFrame) {
						domainAxiom  = DataFactory.get().getOWLDataPropertyDomainAxiom((OWLDataProperty)freshEntity,domain);
					}
					else {
						domainAxiom  = DataFactory.get().getOWLObjectPropertyDomainAxiom((OWLObjectProperty)freshEntity,domain);
					}
					allAxioms.add(domainAxiom);
				}
			}
			
			// Then we do rdfs:range. Only support object properties at this time..
			if (frame instanceof ObjectPropertyFrame) {
				for (LabelOrIri rangeFromFrame: ((ObjectPropertyFrame) frame).getRanges()) {
					OWLClass range;
					if (rangeFromFrame.getIri().isPresent()) {
						// Get range OWL class from frame IRI 
						range = DataFactory.getOWLClass(rangeFromFrame.getIri().get());
					}
					else {
						// Get domain OWL class from freshentities matching label
						range = (OWLClass)freshEntities.get(rangeFromFrame.getLabel().get());
					}
					if (range != null) {
						OWLAxiom rangeAxiom  = DataFactory.get().getOWLObjectPropertyRangeAxiom((OWLObjectProperty)freshEntity,range);
						allAxioms.add(rangeAxiom);
					}
				}
			}
		}
		
		// Recurse over children
		for (FrameTreeNode<OntologyEntityFrame> childTree: tree.getChildren()) {
			allAxioms.addAll(generateFrameTreeCreationAxioms(project, childTree, Optional.of(freshEntity)));
		}
		
		return allAxioms;
	}
	
	@Override
	public OWLEntity getRenamedResult(OWLEntity result, RenameMap renameMap) {
		return result;
	}
	
	/**
	 * Retrieve or create an OWL Entity from an OntologyEntityFrame object. If the frame is intended to be cloned or 
	 * if it has no minted IRI already, then the entity is either created and added to the fresh entities cache, 
	 * or fetched from that cache (if it exists there already). Otherwise an entity of the correct type is created 
	 * using the OWLAPI Data Factory from the frame IRI. 
	 * @param frame
	 * @return
	 */
	private OWLEntity getEntityFromFrame(OntologyEntityFrame frame) {
		
		Boolean isFrameCloned = frame.getClonedLabel().isPresent();
		Optional<IRI> entityIri = frame.getIri();
		
		// If this is an entity intended to be cloned, or if no prior IRI exists, either 
		// retrieve the entity from the fresh entities cache or generate it new.
		if (isFrameCloned || !entityIri.isPresent()) {
			String entityLabel = frame.getCurrentLabel();
			if (freshEntities.containsKey(entityLabel)) {
				// This is a newly created entity that has not yet been persisted to the ontology
				if (frame instanceof ClassFrame) {
					return (OWLClass)freshEntities.get(entityLabel);
				}
				else if (frame instanceof DataPropertyFrame) {
					return (OWLDataProperty)freshEntities.get(entityLabel);
				}
				else {
					return (OWLObjectProperty)freshEntities.get(entityLabel);
				}
			}
			else {
				// Create a new entity and return
				OWLEntity freshEntity;
				if (frame instanceof ClassFrame) {
					freshEntity = DataFactory.getFreshOWLEntity(EntityType.CLASS, entityLabel);
				}
				else if (frame instanceof DataPropertyFrame) {
					freshEntity = DataFactory.getFreshOWLEntity(EntityType.DATA_PROPERTY, entityLabel);
				}
				else {
					freshEntity = DataFactory.getFreshOWLEntity(EntityType.OBJECT_PROPERTY, entityLabel);
				}
				freshEntities.put(entityLabel, freshEntity);
				return freshEntity;
			}
		}
		// An IRI exists already, use DataFactory to construct corresponding entity type 
		else {
			if (frame instanceof ClassFrame) {
				return DataFactory.getOWLClass(entityIri.get());
			}
			else if (frame instanceof DataPropertyFrame) {
				return DataFactory.getOWLDataProperty(entityIri.get());
			}
			else {
				return DataFactory.getOWLObjectProperty(entityIri.get());
			}
		}
	}

	private Set<OWLOntology> getOdpClosure(IRI odpIri) {
		try {
			// Fetch ODP as Turtle-formatted string from XdpService via REST
			RestTemplate restTemplate = new RestTemplate();
			String queryUri = String.format("%s/retrieve/odpBuildingBlockTurtle?uri=%s", XdpServiceUriBase, odpIri.toString());
			String turtleRepresentation = restTemplate.getForObject(queryUri, String.class);
			
			// Load ODP as in-memory OWLOntology representation
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
	        config = config.setFollowRedirects(false);
	        config = config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
	        StreamDocumentSource sds = new StreamDocumentSource(IOUtils.toInputStream(turtleRepresentation));
	        manager.loadOntologyFromOntologyDocument(sds, config);
	        return manager.getOntologies();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
