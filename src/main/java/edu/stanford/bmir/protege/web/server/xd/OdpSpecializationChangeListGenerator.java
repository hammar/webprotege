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
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Optional;

import edu.stanford.bmir.protege.web.server.change.ChangeGenerationContext;
import edu.stanford.bmir.protege.web.server.change.ChangeListGenerator;
import edu.stanford.bmir.protege.web.server.change.OntologyChangeList;
import edu.stanford.bmir.protege.web.server.logging.WebProtegeLogger;
import edu.stanford.bmir.protege.web.server.owlapi.OWLAPIProject;
import edu.stanford.bmir.protege.web.server.owlapi.RenameMap;
import edu.stanford.bmir.protege.web.shared.DataFactory;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.LabelOrIri;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpSpecialization;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.SubsumptionAlignment;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.PropertyFrame;

public class OdpSpecializationChangeListGenerator implements ChangeListGenerator<OWLEntity> {

	
	@Inject private WebProtegeLogger log;
	private String XdpServiceUriBase;
	
	private Map<String,OWLEntity> freshEntities;
	private Set<OWLOntology> odpClosure;
	
	public OdpSpecializationChangeListGenerator(OdpSpecialization specialization) {
		super();
		
		this.specialization = specialization;
		this.freshEntities = new HashMap<String,OWLEntity>();
		
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

	private OdpSpecialization specialization;
	
	@Override
	public OntologyChangeList<OWLEntity> generateChanges(OWLAPIProject project, ChangeGenerationContext context) {
		
		// Initiate map used to keep track of created OWL Entities when linking restrictions and such
		// involving both newly created classes and properties
		
		
		// Initiate change builder
		OntologyChangeList.Builder<OWLEntity> builder = new OntologyChangeList.Builder<OWLEntity>();
		
		// Load ODP closure
		if (odpClosure == null) {
			odpClosure = getOdpClosure(specialization.getOdpIri());
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
		
		// 1. Create class hierarchy from input specialization
		for (FrameTreeNode<OntologyEntityFrame> classTree: specialization.getClassFrameTrees()) {
			Set<OWLAxiom> axioms = generateFrameTreeCreationAxioms(project, classTree, Optional.<OWLEntity>absent());
			for (OWLAxiom axiom: axioms) {
				builder.addAxiom(project.getRootOntology(), axiom);
			}
		}
		
		// 2. Create data property hierarchy from input specialization
		for (FrameTreeNode<OntologyEntityFrame> dataPropertyTree: specialization.getDataPropertyFrameTrees()) {
			Set<OWLAxiom> axioms = generateFrameTreeCreationAxioms(project, dataPropertyTree, Optional.<OWLEntity>absent());
			for (OWLAxiom axiom: axioms) {
				builder.addAxiom(project.getRootOntology(), axiom);
			}
		}
		
		// 3. Create object property hierarchy from input specialization
		for (FrameTreeNode<OntologyEntityFrame> objectPropertyTree: specialization.getObjectPropertyFrameTrees()) {
			Set<OWLAxiom> axioms = generateFrameTreeCreationAxioms(project, objectPropertyTree, Optional.<OWLEntity>absent());
			for (OWLAxiom axiom: axioms) {
				builder.addAxiom(project.getRootOntology(), axiom);
			}
		}
		
		// 4. TODO: Create existential/universal restriction axioms on classes using properties..
		
		// 5. Create alignment axioms
		for (Alignment alignment: specialization.getAlignments()) {
			if (alignment instanceof SubsumptionAlignment) {
				OntologyEntityFrame superFrame = ((SubsumptionAlignment) alignment).getSuperEntity();
				OntologyEntityFrame subFrame = ((SubsumptionAlignment) alignment).getSubEntity();
				OWLEntity superEntity = getEntityFromFrame(superFrame);
				OWLEntity subEntity = getEntityFromFrame(subFrame);
				Optional<OWLAxiom> subsumptionAxiom = generateSubsumptionAxiom(project,superEntity,subEntity);
				if (subsumptionAxiom.isPresent()) {
					builder.addAxiom(project.getRootOntology(), subsumptionAxiom.get());
				}
			}
		}
		
		// 6. Build change list and return it
		return builder.build();
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
		OWLAxiom subsumptionAxiom;
		if (subEntity instanceof OWLClass) {
			subsumptionAxiom = project.getDataFactory().getOWLSubClassOfAxiom((OWLClass)subEntity, (OWLClass)superEntity);
		}
		else if (subEntity instanceof OWLDataProperty) {
			subsumptionAxiom = project.getDataFactory().getOWLSubDataPropertyOfAxiom((OWLDataProperty)subEntity, (OWLDataProperty)superEntity);
		}
		else {
			subsumptionAxiom = project.getDataFactory().getOWLSubObjectPropertyOfAxiom((OWLObjectProperty)subEntity, (OWLObjectProperty)superEntity);
		}
		return Optional.of(subsumptionAxiom);
	}

	public Map<String,String> getPrefixes() {
		Map<String,String> prefixes = new HashMap<String,String>();
		if (odpClosure == null) {
			odpClosure = getOdpClosure(specialization.getOdpIri());
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
		String entityLabel = frame.getLabel();
		
		// Generate an entity declaration axiom of the correct type
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
	 * Retrieve or create an OWL Entity from an OntologyEntityFrame object. If the frame object has an assigned minted
	 * IRI then an entity of the correct type is created using the OWLAPI Data Factory from that IRI. Otherwise, if 
	 * the label is associated with a freshly created (but not yet persisted to ontology) entity, then that freshly
	 * created entity is fetched from cache. Otherwise, a new entity is created and persisted in the fresh entity
	 * cache. 
	 * @param frame
	 * @return
	 */
	private OWLEntity getEntityFromFrame(OntologyEntityFrame frame) {
		Optional<IRI> entityIri = frame.getIri();
		String entityLabel = frame.getLabel();
		if (entityIri.isPresent()) {
			// An IRI exists already, use DataFactory to construct corresponding entity type
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
		else if (freshEntities.containsKey(entityLabel)) {
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
