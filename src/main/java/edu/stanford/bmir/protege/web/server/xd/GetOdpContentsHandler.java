package edu.stanford.bmir.protege.web.server.xd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.springframework.web.client.RestTemplate;

import edu.stanford.bmir.protege.web.server.dispatch.ActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.NullValidator;
import edu.stanford.bmir.protege.web.server.logging.WebProtegeLogger;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetOdpContentsAction;
import edu.stanford.bmir.protege.web.shared.xd.data.LabelOrIri;
import edu.stanford.bmir.protege.web.shared.xd.data.XdTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpContentsResult;

public class GetOdpContentsHandler implements ActionHandler<GetOdpContentsAction,GetOdpContentsResult> {

	private final WebProtegeLogger log;
	private String XdpServiceUriBase;
	
	private OWLAnnotationProperty rdfsLabel;
	private OWLAnnotationProperty rdfsComment;
	
	@Inject
	public GetOdpContentsHandler(WebProtegeLogger logger) {
		super();
		this.log = logger;
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
			String queryUri = String.format("%s/retrieve/odpBuildingBlockTurtle?uri=%s", XdpServiceUriBase, action.getOdpUri());
			String turtleRepresentation = restTemplate.getForObject(queryUri, String.class);
			
			// Load ODP as in-memory OWLOntology representation
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
	        config = config.setFollowRedirects(false);
	        config = config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
	        StreamDocumentSource sds = new StreamDocumentSource(IOUtils.toInputStream(turtleRepresentation));
	        OWLOntology odp = manager.loadOntologyFromOntologyDocument(sds, config);
	        
			// Create often used properties
			OWLDataFactory df = manager.getOWLDataFactory();
			rdfsLabel = df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
	        rdfsComment = df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());
			
	        // Set up reasoner (required for traversing class/property hierarchies)
	        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
	        OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(odp);
	        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY, InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.DATA_PROPERTY_HIERARCHY);
	        
	        XdTreeNode<ClassFrame> classes = getClassFrames(odp, reasoner);
	        XdTreeNode<ObjectPropertyFrame> objectProperties = getObjectPropertyFrames(odp, reasoner);
	        XdTreeNode<DataPropertyFrame> datatypeProperties = getDatatypeFrames(odp, reasoner);
	        
	        return new GetOdpContentsResult(classes,objectProperties,datatypeProperties);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private XdTreeNode<ObjectPropertyFrame> getObjectPropertyFrames(OWLOntology ont, OWLReasoner reasoner) {
		
        // Extract metadata from top node (i.e., OWL:TopObjectProperty)
		Node<OWLObjectPropertyExpression> topNode = reasoner.getTopObjectPropertyNode();
        OWLObjectPropertyExpression topPropertyExpression = topNode.getRepresentativeElement();
        OWLObjectProperty topObjectProperty = (OWLObjectProperty)topPropertyExpression;
        
        ObjectPropertyFrame opf = (ObjectPropertyFrame)getFrame(topObjectProperty, ont);
        
        // Create the tree with only OWL:TopObjectProperty in it
        XdTreeNode<ObjectPropertyFrame> tree = new XdTreeNode<ObjectPropertyFrame>(opf);
        
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


	
	private XdTreeNode<DataPropertyFrame> getDatatypeFrames(OWLOntology ont, OWLReasoner reasoner) {
		// Extract metadata from top node (i.e., OWL:TopDataProperty)
		Node<OWLDataProperty> topNode = reasoner.getTopDataPropertyNode();
        OWLDataProperty topProperty = topNode.getRepresentativeElement();
        
        DataPropertyFrame dpf = (DataPropertyFrame)getFrame(topProperty, ont);
        
        // Create the tree with only OWL:TopDataProperty in it
        XdTreeNode<DataPropertyFrame> tree = new XdTreeNode<DataPropertyFrame>(dpf);
        
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
	private XdTreeNode<ClassFrame> getClassFrames(OWLOntology ont, OWLReasoner reasoner) {
        // Extract metadata from top node (i.e., OWL:Thing)
		Node<OWLClass> topNode = reasoner.getTopClassNode();
        OWLClass owlThing = topNode.getRepresentativeElement();
        ClassFrame cf = (ClassFrame)getFrame(owlThing, ont);
        
        // Create the tree with only OWL:Thing in it
        XdTreeNode<ClassFrame> tree = new XdTreeNode<ClassFrame>(cf);
        
        // Recursively add child classes
        for (Node<OWLClass> childNode: reasoner.getSubClasses(topNode.getRepresentativeElement(), true)) {
        	addClassToTree(childNode,tree,reasoner, ont);
        }
        return tree;
	}
	
	/**
	 * Generate an Ontology Entity Frame from an OWL Entity
	 * @param cls
	 * @param ont
	 * @return
	 */
	private OntologyEntityFrame getFrame(OWLEntity entity, OWLOntology ont) {
		// TODO: Depending on what type of OWLEntity we get in, construct a suitable frame and return it
		
		String entityLabel = getLabel(entity, ont);
		String candidateComment = getAnnotationValue(entity, ont, rdfsComment, "en");
		
		if (entity instanceof OWLClass) {
			return new ClassFrame(entityLabel,candidateComment, entity.getIRI());
		}
		else if (entity instanceof OWLDataProperty) {
			// Create frame
			OWLDataProperty dataProperty = (OWLDataProperty)entity;
			DataPropertyFrame frame = new DataPropertyFrame(entityLabel,candidateComment, dataProperty.getIRI());
			
			// Get domains
			Set<OWLClassExpression> domainExpressions = dataProperty.getDomains(ont);
			List<LabelOrIri> domains = new ArrayList<LabelOrIri>();
    		for (OWLClassExpression oce: domainExpressions) {
    			if (oce instanceof OWLClass) {
    				IRI domainIri = ((OWLClass)oce).getIRI();
    				domains.add(new LabelOrIri(domainIri));
    			}
    		}
			frame.setDomains(domains.toArray(new LabelOrIri[domains.size()]));
    		
    		// Get ranges
    		Set<OWLDataRange> rangeExpressions = dataProperty.getRanges(ont);
    		List<IRI> ranges = new ArrayList<IRI>();
    		for (OWLDataRange dr: rangeExpressions) {
    			if (dr instanceof OWLDatatype) {
    				IRI rangeIri = ((OWLDatatype) dr).getIRI();
    				ranges.add(rangeIri);
    			}
    		}
    		frame.setRanges(ranges.toArray(new IRI[ranges.size()]));
    		
    		// Set attributes
			frame.setFunctional(dataProperty.isFunctional(ont));
    		
    		// Return constructed data property frame
			return frame;
		}
		else if (entity instanceof OWLObjectProperty) {
			// Create frame
			OWLObjectProperty objectProperty = (OWLObjectProperty)entity;
			ObjectPropertyFrame frame = new ObjectPropertyFrame(entityLabel,candidateComment, objectProperty.getIRI());
			
			// Get domains
			Set<OWLClassExpression> domainExpressions = objectProperty.getDomains(ont);
			List<LabelOrIri> domains = new ArrayList<LabelOrIri>();
    		for (OWLClassExpression oce: domainExpressions) {
    			if (oce instanceof OWLClass) {
    				IRI domainIri = ((OWLClass)oce).getIRI();
    				domains.add(new LabelOrIri(domainIri));
    			}
    		}
			frame.setDomains(domains.toArray(new LabelOrIri[domains.size()]));
    		
    		// Get ranges
			Set<OWLClassExpression> rangeExpressions = objectProperty.getRanges(ont);
			List<LabelOrIri> ranges = new ArrayList<LabelOrIri>();
    		for (OWLClassExpression oce: rangeExpressions) {
    			if (oce instanceof OWLClass) {
    				IRI rangeIri = ((OWLClass)oce).getIRI();
    				ranges.add(new LabelOrIri(rangeIri));
    			}
    		}
			frame.setRanges(ranges.toArray(new LabelOrIri[ranges.size()]));
    		
			// Set attributes
			frame.setSymmetric(objectProperty.isSymmetric(ont));
			frame.setFunctional(objectProperty.isFunctional(ont));
			frame.setTransitive(objectProperty.isTransitive(ont));
			
			// Return constructed object property frame
    		return frame;
		}
		
		// We should never get to here.
		return null;
		
		/*
    	
    	// Get obj property specifics
    	if (entity instanceof OWLObjectProperty) {
    		OWLObjectProperty entityAsProperty = (OWLObjectProperty)entity;
    		Set<OWLClassExpression> domainExpressions = entityAsProperty.getDomains(ont);
    		for (OWLClassExpression oce: domainExpressions) {
    			if (oce instanceof OWLClass) {
    				String domainLabel = getLabel(((OWLClass)oce), ont);
    				clsMetadata.put("rdfsDomain", domainLabel);
    				break;
    			}
    		}
    		Set<OWLClassExpression> rangeExpressions = entityAsProperty.getRanges(ont);
    		for (OWLClassExpression oce: rangeExpressions) {
    			if (oce instanceof OWLClass) {
    				String rangeLabel = getLabel(((OWLClass)oce), ont);
    				clsMetadata.put("rdfsRange", rangeLabel);
    				break;
    			}
    		}
    		if (entityAsProperty.isFunctional(ont)) {
    			clsMetadata.put("owlFunctionalProperty","true");
    		}
    		if (entityAsProperty.isTransitive(ont)) {
    			clsMetadata.put("owlTransitiveProperty","true");
    		}
    		if (entityAsProperty.isSymmetric(ont)) {
    			clsMetadata.put("owlSymmetricProperty","true");
    		}
    	}
    	
    	}*/
	}
	
	/**
	 * Recursively convert OWLClass nodes provided from reasoner to a Tree of ClassFrames 
	 * suitable for sending over the wire to client.
	 * @param node
	 * @param tree
	 * @param reasoner
	 * @param ont
	 */
	private void addClassToTree(Node<OWLClass> node, XdTreeNode<ClassFrame> tree, OWLReasoner reasoner, OWLOntology ont) {
		if (!node.isBottomNode()) {
			for (OWLClass cls: node.getEntities()) {
				ClassFrame cf = (ClassFrame)getFrame(cls, ont);
				XdTreeNode<ClassFrame> emNode = tree.addChild(cf);
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
	private void addObjectPropertyToTree(OWLObjectProperty property, XdTreeNode<ObjectPropertyFrame> tree, 
			OWLReasoner reasoner, OWLOntology ont) {
		ObjectPropertyFrame opf = (ObjectPropertyFrame)getFrame(property, ont);
		XdTreeNode<ObjectPropertyFrame> emNode = tree.addChild(opf);
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
	private void addDataPropertyToTree(OWLDataProperty property, XdTreeNode<DataPropertyFrame> tree, 
			OWLReasoner reasoner, OWLOntology ont) {
		DataPropertyFrame dpf = (DataPropertyFrame)getFrame(property, ont);
		XdTreeNode<DataPropertyFrame> emNode = tree.addChild(dpf);
		for (Node<OWLDataProperty> childNode: reasoner.getSubDataProperties(property, true)) {
        	if (!childNode.isBottomNode()) {
        		for (OWLDataProperty childProperty: childNode.getEntities()) {
        			addDataPropertyToTree(childProperty,emNode,reasoner, ont);
        		}
        	}
		}
	}
	
	
	/**
	 * Gets label (prefixed by ODP name). If no RDFS label exists for ODP or for concept exist,
	 * uses local IRI fragments.
	 * @param entity
	 * @return
	 */
	private String getLabel(OWLEntity entity, OWLOntology ont) {
		String odpPrefix = getOdpPrefix(ont);
		String entityLabel = getAnnotationValue(entity, ont, rdfsLabel, "en");
		if (entityLabel == null) {
			entityLabel = entity.getIRI().getFragment();
		}
		return String.format("%s: %s",odpPrefix,entityLabel);
	}
	
	/**
	 * Attempts to get a reasonable prefix to show the user for this particular ODP. 
	 * Searches, in order, for rdfs:label annotations in English, then in any language,
	 * then ontology IRI local fragment, and as last resort, document IRI local fragment. 
	 * @param odp
	 * @return
	 */
	private String getOdpPrefix(OWLOntology odp) {
		String odpPrefix = null;
		for (OWLAnnotation annotation : odp.getAnnotations()) {
			if (annotation.getProperty() == rdfsLabel && annotation.getValue() instanceof OWLLiteral) {
				OWLLiteral val = (OWLLiteral) annotation.getValue();
				if (val.hasLang("en")) {
					odpPrefix = val.getLiteral();
					break;
                }
				if (!val.hasLang()) {
					odpPrefix = val.getLiteral();
                }
			}
		}
		if (odpPrefix == null) {
			odpPrefix = odp.getOntologyID().getOntologyIRI().getFragment();
		}
		if (odpPrefix == null) {
			odpPrefix = odp.getOWLOntologyManager().getOntologyDocumentIRI(odp).getFragment();
		}
		return odpPrefix;
	}
	
	/**
	 * Returns the string value of a given annotation property on a given entity, in a given ontology,
	 * as defined in a given language. If no such matching value is found, returns a value with no language tag.
	 * If no such value is found, returns null.
	 * @param entity
	 * @param ont
	 * @param annotationProperty
	 * @param lang
	 * @return
	 */
	private String getAnnotationValue(OWLEntity entity,OWLOntology ont,OWLAnnotationProperty annotationProperty,String lang) {
		String candidateAnswer = null;
		for (OWLAnnotation annotation : entity.getAnnotations(ont, annotationProperty)) {
            if (annotation.getValue() instanceof OWLLiteral) {
            	OWLLiteral val = (OWLLiteral) annotation.getValue();
                if (val.hasLang(lang)) {
                	return val.getLiteral();
                }
                if (!val.hasLang()) {
                	candidateAnswer = val.getLiteral();
                }
            }
        }
		return candidateAnswer;
	}
}
