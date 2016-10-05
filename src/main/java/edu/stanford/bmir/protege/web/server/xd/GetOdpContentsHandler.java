package edu.stanford.bmir.protege.web.server.xd;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.springframework.web.client.RestTemplate;

import de.uni_stuttgart.vis.vowl.owl2vowl.Owl2Vowl;
import edu.stanford.bmir.protege.web.server.dispatch.ActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.NullValidator;
import edu.stanford.bmir.protege.web.server.logging.WebProtegeLogger;
import edu.stanford.bmir.protege.web.server.xd.log.XdpLogger;
import edu.stanford.bmir.protege.web.server.xd.util.OntologyOperations;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetOdpContentsAction;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpContentsResult;
import edu.stanford.bmir.protege.web.shared.xd.util.TreeMethods;

public class GetOdpContentsHandler implements ActionHandler<GetOdpContentsAction,GetOdpContentsResult> {

	private final XdpLogger xdpLog;
	private final WebProtegeLogger log;
	private String XdpServiceUriBase;
	
	@Inject
	public GetOdpContentsHandler(WebProtegeLogger logger) {
		super();
		this.log = logger;
		this.xdpLog = XdpLogger.getInstance();
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
	
	@Override
	public Class<GetOdpContentsAction> getActionClass() {
		return GetOdpContentsAction.class;
	}

	@Override
	public RequestValidator<GetOdpContentsAction> getRequestValidator(
			GetOdpContentsAction action, RequestContext requestContext) {
		return NullValidator.get();
	}

	@Override
	public GetOdpContentsResult execute(GetOdpContentsAction action,
			ExecutionContext executionContext) {
		try {
			// Fetch ODP as Turtle-formatted string from XdpService via REST
			RestTemplate restTemplate = new RestTemplate();
			String queryUri = String.format("%s/retrieve/odpBuildingBlockTurtle?iri=%s", XdpServiceUriBase, action.getOdpUri());
			String turtleRepresentation = restTemplate.getForObject(queryUri, String.class);
			
			// Load ODP as in-memory OWLOntology representation
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
	        config = config.setFollowRedirects(false);
	        config = config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
	        StreamDocumentSource sds = new StreamDocumentSource(IOUtils.toInputStream(turtleRepresentation));
	        OWLOntology odp = manager.loadOntologyFromOntologyDocument(sds, config);
			
	        // Set up reasoner (required for traversing class/property hierarchies)
	        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
	        OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(odp);
	        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY, InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.DATA_PROPERTY_HIERARCHY);
	        
	        FrameTreeNode<OntologyEntityFrame> classes = getClassFrames(odp, reasoner);
	        FrameTreeNode<OntologyEntityFrame> objectProperties = getObjectPropertyFrames(odp, reasoner);
	        FrameTreeNode<OntologyEntityFrame> datatypeProperties = getDatatypeFrames(odp, reasoner);
	        
	        // Enrich class tree with restrictions from the ontology 
	        addRestrictionsToClassFrames(odp, classes, objectProperties);
	        
	        // Get VOWL representation for visualisation purposes
	        OWLOntologyID ontologyId = odp.getOntologyID();
	        String ontologyIdString;
	        if (ontologyId.isAnonymous()) {
	        	ontologyIdString = ontologyId.toString();
			}
			else {
				ontologyIdString = ontologyId.getOntologyIRI().toString();
			}
	        Owl2Vowl owl2Vowl = new Owl2Vowl(odp, ontologyIdString);
	        String odpAsJsonString = owl2Vowl.getJsonAsString();
	        
	        // Log request and user ID for later analysis
	        xdpLog.logOdpContentsRetrieved(executionContext.getUserId(), action.getOdpUri());
	        
	        return new GetOdpContentsResult(IRI.create(action.getOdpUri()), odpAsJsonString, classes,objectProperties,datatypeProperties);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void addRestrictionsToClassFrames(OWLOntology odp, FrameTreeNode<OntologyEntityFrame> classes, FrameTreeNode<OntologyEntityFrame> objectProperties) {
		Set<OntologyEntityFrame> flattenedClasses = TreeMethods.flattenFrameTreeToSet(classes);
		Map<IRI,ClassFrame> iriToClassFrameMap = new HashMap<IRI,ClassFrame>();
		for (OntologyEntityFrame frame: flattenedClasses) {
			if (frame instanceof ClassFrame && frame.getIri().isPresent()) {
				ClassFrame classFrame = (ClassFrame)frame;
				iriToClassFrameMap.put(frame.getIri().get(), classFrame);
			}
		}
		Set<OntologyEntityFrame> flattenedObjectProperties = TreeMethods.flattenFrameTreeToSet(objectProperties);
		Map<IRI,ObjectPropertyFrame> iriToObjectPropertyFrameMap = new HashMap<IRI,ObjectPropertyFrame>();
		for (OntologyEntityFrame frame: flattenedObjectProperties) {
			if (frame instanceof ObjectPropertyFrame && frame.getIri().isPresent()) {
				ObjectPropertyFrame opFrame = (ObjectPropertyFrame)frame;
				iriToObjectPropertyFrameMap.put(frame.getIri().get(), opFrame);
			}
		}
		for (OntologyEntityFrame frame: flattenedClasses) {
			if (frame instanceof ClassFrame) {
				ClassFrame classFrame = (ClassFrame)frame;
				OntologyOperations.addRestrictionsToFrame(classFrame, odp, iriToClassFrameMap, iriToObjectPropertyFrameMap);
			}
		}
	}
	
	private FrameTreeNode<OntologyEntityFrame> getObjectPropertyFrames(OWLOntology ont, OWLReasoner reasoner) {
		
        // Extract metadata from top node (i.e., OWL:TopObjectProperty)
		Node<OWLObjectPropertyExpression> topNode = reasoner.getTopObjectPropertyNode();
        OWLObjectPropertyExpression topPropertyExpression = topNode.getRepresentativeElement();
        OWLObjectProperty topObjectProperty = (OWLObjectProperty)topPropertyExpression;
        
        ObjectPropertyFrame opf = (ObjectPropertyFrame)OntologyOperations.getFrame(topObjectProperty, ont);
        
        // Create the tree with only OWL:TopObjectProperty in it
        FrameTreeNode<OntologyEntityFrame> tree = new FrameTreeNode<OntologyEntityFrame>(opf);
        
        // Recursively add child properties (but not property expressions, and not bottoms)
        for (Node<OWLObjectPropertyExpression> childNode: reasoner.getSubObjectProperties(topObjectProperty, true)) {
        	if (!childNode.isBottomNode()) {
        		for (OWLObjectPropertyExpression ope: childNode.getEntities()) {
        			if (ope instanceof OWLObjectProperty) {
        				OWLObjectProperty childProperty = (OWLObjectProperty)ope;
        				addObjectPropertyToTree(childProperty,tree,reasoner, ont);
        			}
        		}
        	}
        }
        return tree;
	}


	
	private FrameTreeNode<OntologyEntityFrame> getDatatypeFrames(OWLOntology ont, OWLReasoner reasoner) {
		// Extract metadata from top node (i.e., OWL:TopDataProperty)
		Node<OWLDataProperty> topNode = reasoner.getTopDataPropertyNode();
        OWLDataProperty topProperty = topNode.getRepresentativeElement();
        
        DataPropertyFrame dpf = (DataPropertyFrame)OntologyOperations.getFrame(topProperty, ont);
        
        // Create the tree with only OWL:TopDataProperty in it
        FrameTreeNode<OntologyEntityFrame> tree = new FrameTreeNode<OntologyEntityFrame>(dpf);
        
        // Recursively add child properties (but not property expressions, and not bottoms)
        for (Node<OWLDataProperty> childNode: reasoner.getSubDataProperties(topProperty, true)) {
        	if (!childNode.isBottomNode()) {
        		for (OWLDataProperty childProperty: childNode.getEntities()) {
        			addDataPropertyToTree(childProperty,tree,reasoner, ont);
        		}
        	}
        }
        return tree;
	}
	

	/**
	 * Converts the class hierarchy in a given ontology into a tree of EntityMetadata frames. Uses
	 * a provided reasoner to walk the class inheritance hierarchy.
	 * @param ont
	 * @return
	 */
	private FrameTreeNode<OntologyEntityFrame> getClassFrames(OWLOntology ont, OWLReasoner reasoner) {
        // Extract metadata from top node (i.e., OWL:Thing)
		Node<OWLClass> topNode = reasoner.getTopClassNode();
        OWLClass owlThing = topNode.getRepresentativeElement();
        ClassFrame cf = (ClassFrame)OntologyOperations.getFrame(owlThing, ont);
        
        // Create the tree with only OWL:Thing in it
        FrameTreeNode<OntologyEntityFrame> tree = new FrameTreeNode<OntologyEntityFrame>(cf);
        
        // Recursively add child classes
        for (Node<OWLClass> childNode: reasoner.getSubClasses(topNode.getRepresentativeElement(), true)) {
        	addClassToTree(childNode,tree,reasoner, ont);
        }
        return tree;
	}
	

	
	/**
	 * Recursively convert OWLClass nodes provided from reasoner to a Tree of ClassFrames 
	 * suitable for sending over the wire to client.
	 * @param node
	 * @param tree
	 * @param reasoner
	 * @param ont
	 */
	private void addClassToTree(Node<OWLClass> node, FrameTreeNode<OntologyEntityFrame> tree, OWLReasoner reasoner, OWLOntology ont) {
		if (!node.isBottomNode()) {
			for (OWLClass cls: node.getEntities()) {
				ClassFrame cf = (ClassFrame)OntologyOperations.getFrame(cls, ont);
				FrameTreeNode<OntologyEntityFrame> emNode = tree.addChild(cf);
				for (Node<OWLClass> childNode: reasoner.getSubClasses(node.getRepresentativeElement(), true)) {
					addClassToTree(childNode, emNode, reasoner, ont);
				}
			}
		}
	}
	
	/**
	 * Recursively convert OWLObjectPropertyExpression nodes provided from reasoner to a Tree of EntityMetadata frames 
	 * suitable for sending over the wire to client.
	 * @param node
	 * @param tree
	 * @param reasoner
	 * @param ont
	 */
	private void addObjectPropertyToTree(OWLObjectProperty property, FrameTreeNode<OntologyEntityFrame> tree, 
			OWLReasoner reasoner, OWLOntology ont) {
		OntologyEntityFrame opf = (OntologyEntityFrame)OntologyOperations.getFrame(property, ont);
		FrameTreeNode<OntologyEntityFrame> emNode = tree.addChild(opf);
		for (Node<OWLObjectPropertyExpression> childNode: reasoner.getSubObjectProperties(property, true)) {
        	if (!childNode.isBottomNode()) {
        		for (OWLObjectPropertyExpression childOpe: childNode.getEntities()) {
		        	if (childOpe instanceof OWLObjectProperty) {
		        		OWLObjectProperty childProperty = (OWLObjectProperty)childOpe;
		        		addObjectPropertyToTree(childProperty,emNode,reasoner, ont);
		        	}
        		}
        	}
		}
	}
	
	
	/**
	 * Recursively convert OWLDataProperty nodes provided from reasoner to a Tree of EntityMetadata frames 
	 * suitable for sending over the wire to client.
	 * @param node
	 * @param tree
	 * @param reasoner
	 * @param ont
	 */
	private void addDataPropertyToTree(OWLDataProperty property, FrameTreeNode<OntologyEntityFrame> tree, 
			OWLReasoner reasoner, OWLOntology ont) {
		DataPropertyFrame dpf = (DataPropertyFrame)OntologyOperations.getFrame(property, ont);
		FrameTreeNode<OntologyEntityFrame> emNode = tree.addChild(dpf);
		for (Node<OWLDataProperty> childNode: reasoner.getSubDataProperties(property, true)) {
        	if (!childNode.isBottomNode()) {
        		for (OWLDataProperty childProperty: childNode.getEntities()) {
        			addDataPropertyToTree(childProperty,emNode,reasoner, ont);
        		}
        	}
		}
	}
}
