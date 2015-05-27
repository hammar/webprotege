package edu.stanford.bmir.protege.web.server.xd;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
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
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import edu.stanford.bmir.protege.web.server.dispatch.ActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.NullValidator;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetOdpContentsAction;
import edu.stanford.bmir.protege.web.shared.xd.data.EntityMetadata;
import edu.stanford.bmir.protege.web.shared.xd.data.XdTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpContentsResult;

public class GetOdpContentsHandler implements ActionHandler<GetOdpContentsAction,GetOdpContentsResult> {

	private OWLAnnotationProperty rdfsLabel;
	private OWLAnnotationProperty rdfsComment;
	
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
			// Load ODP ontology
			// TODO: ACTUALLY LOAD ODP FROM XDPSERVICE CALL
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			IRI iri = IRI.create("http://www.dcs.bbk.ac.uk/~michael/sw/slides/pizza.owl");
	        OWLOntology odp = manager.loadOntologyFromOntologyDocument(iri);
			
			// Create often used properties
			OWLDataFactory df = manager.getOWLDataFactory();
			rdfsLabel = df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
	        rdfsComment = df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());
			
	        // Set up reasoner (required for traversing class/property hierarchies)
	        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
	        OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(odp);
	        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY, InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.DATA_PROPERTY_HIERARCHY);
	        
	        XdTreeNode<EntityMetadata> classes = getClassesMetadata(odp, reasoner);
	        XdTreeNode<EntityMetadata> objectProperties = getObjectPropertiesMetadata(odp, reasoner);
	        XdTreeNode<EntityMetadata> datatypeProperties = getDatatypePropertiesMetadata(odp, reasoner);
	        
	        return new GetOdpContentsResult(classes,objectProperties,datatypeProperties);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private XdTreeNode<EntityMetadata> getObjectPropertiesMetadata(OWLOntology ont, OWLReasoner reasoner) {
		
		//TODO: Figure out why some properties aren't included (e.g., hasIngredient in pizza ontology)
		
        // Extract metadata from top node (i.e., OWL:TopObjectProperty)
		Node<OWLObjectPropertyExpression> topNode = reasoner.getTopObjectPropertyNode();
        OWLObjectPropertyExpression topPropertyExpression = topNode.getRepresentativeElement();
        OWLObjectProperty topObjectProperty = (OWLObjectProperty)topPropertyExpression;
        
        EntityMetadata em = getMetadata(topObjectProperty, ont);
        
        // Create the tree with only OWL:TopObjectProperty in it
        XdTreeNode<EntityMetadata> tree = new XdTreeNode<EntityMetadata>(em);
        
        // Recursively add child properties (but not property expressions, and not bottoms)
        for (Node<OWLObjectPropertyExpression> childNode: reasoner.getSubObjectProperties(topObjectProperty, true)) {
        	if (!childNode.isBottomNode()) {
	        	OWLObjectPropertyExpression childPropertyExpression = childNode.getRepresentativeElement();
	        	if (childPropertyExpression instanceof OWLObjectProperty) {
	        		OWLObjectProperty childProperty = (OWLObjectProperty)childPropertyExpression;
	        		addObjectPropertyToTree(childProperty,tree,reasoner, ont);
	        	}
        	}
        }
        return tree;
	}


	
	private XdTreeNode<EntityMetadata> getDatatypePropertiesMetadata(OWLOntology ont, OWLReasoner reasoner) {
		
		//TODO: Figure out why some properties aren't included (e.g., hasIngredient in pizza ontology)
		
        // Extract metadata from top node (i.e., OWL:TopDataProperty)
		Node<OWLDataProperty> topNode = reasoner.getTopDataPropertyNode();
        OWLDataProperty topProperty = topNode.getRepresentativeElement();
        
        EntityMetadata em = getMetadata(topProperty, ont);
        
        // Create the tree with only OWL:TopDataProperty in it
        XdTreeNode<EntityMetadata> tree = new XdTreeNode<EntityMetadata>(em);
        
        // Recursively add child properties (but not property expressions, and not bottoms)
        for (Node<OWLDataProperty> childNode: reasoner.getSubDataProperties(topProperty, true)) {
        	if (!childNode.isBottomNode()) {
	        	OWLDataProperty childProperty = childNode.getRepresentativeElement();
	        	addDataPropertyToTree(childProperty,tree,reasoner, ont);
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
	private XdTreeNode<EntityMetadata> getClassesMetadata(OWLOntology ont, OWLReasoner reasoner) {
        // Extract metadata from top node (i.e., OWL:Thing)
		Node<OWLClass> topNode = reasoner.getTopClassNode();
        OWLClass owlThing = topNode.getRepresentativeElement();
        EntityMetadata em = getMetadata(owlThing, ont);
        
        // Create the tree with only OWL:Thing in it
        XdTreeNode<EntityMetadata> tree = new XdTreeNode<EntityMetadata>(em);
        
        // Recursively add child classes
        for (Node<OWLClass> childNode: reasoner.getSubClasses(topNode.getRepresentativeElement(), true)) {
        	addClassToTree(childNode,tree,reasoner, ont);
        }
        return tree;
	}
	
	/**
	 * Generate EntityMetadata from an OWL Entity
	 * @param cls
	 * @param ont
	 * @return
	 */
	private EntityMetadata getMetadata(OWLEntity entity, OWLOntology ont) {
		String classLabel = getLabel(entity, ont);
		Map<String,String> clsMetadata = new HashMap<String,String>();
		
		// Get RDFS:Label
		String labelFromRdfs = getAnnotationValue(entity,ont,rdfsLabel,"en");
    	if (labelFromRdfs != null) {
    		clsMetadata.put("rdfsLabel", labelFromRdfs);
    	}
    	
    	// Get comment
    	String candidateComment = getAnnotationValue(entity,ont,rdfsComment,"en");
    	if (candidateComment != null) {
    		clsMetadata.put("rdfsComment", candidateComment);
    	}
    	
    	// Get obj property specifics
    	if (entity instanceof OWLObjectProperty) {
    		OWLObjectProperty entityAsProperty = (OWLObjectProperty)entity;
    		Set<OWLClassExpression> domainExpressions = entityAsProperty.getDomains(ont);
    		for (OWLClassExpression oce: domainExpressions) {
    			if (oce instanceof OWLClass) {
    				// TODO: handle multiple domains
    				String domainLabel = getLabel(((OWLClass)oce), ont);
    				clsMetadata.put("rdfsDomain", domainLabel);
    				break;
    			}
    		}
    		Set<OWLClassExpression> rangeExpressions = entityAsProperty.getRanges(ont);
    		for (OWLClassExpression oce: rangeExpressions) {
    			if (oce instanceof OWLClass) {
    				// TODO: handle multiple ranges
    				String rangeLabel = getLabel(((OWLClass)oce), ont);
    				clsMetadata.put("rdfsRange", rangeLabel);
    				break;
    			}
    		}
    		// TODO: clean up the below, sending true as a string is ugly..
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
    	
    	// Get data property specifics
    	if (entity instanceof OWLDataProperty) {
    		OWLDataProperty entityAsProperty = (OWLDataProperty)entity;
    		Set<OWLClassExpression> domainExpressions = entityAsProperty.getDomains(ont);
    		for (OWLClassExpression oce: domainExpressions) {
    			if (oce instanceof OWLClass) {
    				// TODO: handle multiple domains
    				String domainLabel = getLabel(((OWLClass)oce), ont);
    				clsMetadata.put("rdfsDomain", domainLabel);
    				break;
    			}
    		}
    		Set<OWLDataRange> rangeExpressions = entityAsProperty.getRanges(ont);
    		for (OWLDataRange dr: rangeExpressions) {
    			if (dr instanceof OWLDatatype) {
    				// TODO: handle multiple ranges
    				String range = ((OWLDatatype) dr).getIRI().toString();
    				clsMetadata.put("rdfsRange", range);
    				break;
    			}
    		}
    		// TODO: clean up the below, sending true as a string is ugly..
    		if (entityAsProperty.isFunctional(ont)) {
    			clsMetadata.put("owlFunctionalProperty","true");
    		}
    	}

		return new EntityMetadata(classLabel, clsMetadata);
	}
	
	/**
	 * Recursively convert OWLClass nodes provided from reasoner to a Tree of EntityMetadata frames 
	 * suitable for sending over the wire to client.
	 * @param node
	 * @param tree
	 * @param reasoner
	 * @param ont
	 */
	private void addClassToTree(Node<OWLClass> node, XdTreeNode<EntityMetadata> tree, OWLReasoner reasoner, OWLOntology ont) {
		if (!node.isBottomNode()) {
			OWLClass cls = node.getRepresentativeElement();
			EntityMetadata em = getMetadata(cls, ont);
			XdTreeNode<EntityMetadata> emNode = tree.addChild(em);
			for (Node<OWLClass> childNode: reasoner.getSubClasses(node.getRepresentativeElement(), true)) {
				addClassToTree(childNode, emNode, reasoner, ont);
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
	private void addObjectPropertyToTree(OWLObjectProperty property, XdTreeNode<EntityMetadata> tree, 
			OWLReasoner reasoner, OWLOntology ont) {
		
		EntityMetadata em = getMetadata(property, ont);
		XdTreeNode<EntityMetadata> emNode = tree.addChild(em);
		for (Node<OWLObjectPropertyExpression> childNode: reasoner.getSubObjectProperties(property, true)) {
        	if (!childNode.isBottomNode()) {
	        	OWLObjectPropertyExpression childPropertyExpression = childNode.getRepresentativeElement();
	        	if (childPropertyExpression instanceof OWLObjectProperty) {
	        		OWLObjectProperty childProperty = (OWLObjectProperty)childPropertyExpression;
	        		addObjectPropertyToTree(childProperty,emNode,reasoner, ont);
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
	private void addDataPropertyToTree(OWLDataProperty property, XdTreeNode<EntityMetadata> tree, 
			OWLReasoner reasoner, OWLOntology ont) {
		EntityMetadata em = getMetadata(property, ont);
		XdTreeNode<EntityMetadata> emNode = tree.addChild(em);
		for (Node<OWLDataProperty> childNode: reasoner.getSubDataProperties(property, true)) {
        	if (!childNode.isBottomNode()) {
	        	OWLDataProperty childProperty = childNode.getRepresentativeElement();
        		addDataPropertyToTree(childProperty,emNode,reasoner, ont);
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
