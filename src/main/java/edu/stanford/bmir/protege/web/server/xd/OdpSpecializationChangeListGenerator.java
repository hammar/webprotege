package edu.stanford.bmir.protege.web.server.xd;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
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
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.PropertyFrame;

public class OdpSpecializationChangeListGenerator implements ChangeListGenerator<OWLEntity> {

	
	@Inject private WebProtegeLogger log;
	private String XdpServiceUriBase;
	
	private Map<String,OWLEntity> freshEntities;
	
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
		
		// Load ODP
		OWLOntology odp = getOdp(specialization.getOdpIri());
		
		// For every axiom in ODP, add to change builder (e.g., copy the ODP into the target ontology)
		Set<OWLAxiom> allOdpAxioms = odp.getAxioms();
		for (OWLAxiom axiom: allOdpAxioms) {
			builder.addAxiom(project.getRootOntology(), axiom);
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
		
		// 5. TODO: Create alignment axioms
		
		// 6. Build change list and return it
		return builder.build();
	}

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
		// TODO Auto-generated method stub
		return null;
	}

	private OWLOntology getOdp(IRI odpIri) {
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
	        OWLOntology odp = manager.loadOntologyFromOntologyDocument(sds, config);
	        return odp;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
