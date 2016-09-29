package edu.stanford.bmir.protege.web.server.xd.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;

import com.google.common.base.Optional;

import edu.stanford.bmir.protege.web.shared.xd.data.LabelOrIri;
import edu.stanford.bmir.protege.web.shared.xd.data.PropertyRestriction;
import edu.stanford.bmir.protege.web.shared.xd.data.PropertyRestriction.ValueConstraint;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.AbstractOntologyEntityFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;

public class OntologyOperations {

	/**
	 * Attempts to get a reasonable prefix to show the user for this particular ontology. 
	 * Searches, in order, for rdfs:label annotations in English, then in any language,
	 * then ontology IRI local fragment, and as last resort, document IRI local fragment. 
	 * @param ontology
	 * @return
	 */
	public static String getOntologyPrefix(OWLOntology ontology) {
		String ontologyPrefix = null;
		for (OWLAnnotation annotation : ontology.getAnnotations()) {
			if (annotation.getProperty() == AnnotationOperations.rdfsLabel && annotation.getValue() instanceof OWLLiteral) {
				OWLLiteral val = (OWLLiteral) annotation.getValue();
				if (val.hasLang("en")) {
					ontologyPrefix = val.getLiteral();
					break;
                }
				if (!val.hasLang()) {
					ontologyPrefix = val.getLiteral();
                }
			}
		}
		if (ontologyPrefix == null) {
			ontologyPrefix = ontology.getOntologyID().getOntologyIRI().getFragment();
		}
		if (ontologyPrefix == null) {
			ontologyPrefix = ontology.getOWLOntologyManager().getOntologyDocumentIRI(ontology).getFragment();
		}
		return ontologyPrefix;
	}
	
	// TODO: DOCUMENT THIS
	private static Optional<PropertyRestriction> restrictionFromOwlExpression(OWLClassExpression expression, OWLOntology ont, Map<IRI,ClassFrame> iriToClassFrameMap,  Map<IRI,ObjectPropertyFrame> iriToObjectPropertyFrameMap) {
		if (expression instanceof OWLQuantifiedObjectRestriction) {
			OWLQuantifiedObjectRestriction quantRestriction = (OWLQuantifiedObjectRestriction)expression;
			OWLObjectPropertyExpression propertyExpression = quantRestriction.getProperty();
			OWLClassExpression targetClassExpression = quantRestriction.getFiller();
			if (!propertyExpression.isAnonymous() && !targetClassExpression.isAnonymous()) {
				OWLObjectProperty objectProperty = propertyExpression.asOWLObjectProperty();
				ObjectPropertyFrame propertyFrame;
				if (iriToObjectPropertyFrameMap.containsKey(objectProperty.getIRI())) {
					propertyFrame = iriToObjectPropertyFrameMap.get(objectProperty.getIRI());
				}
				else {
					propertyFrame = (ObjectPropertyFrame)getFrame(objectProperty, ont);
				}
				OWLClass targetClass = targetClassExpression.asOWLClass();
				ClassFrame targetClassFrame;
				if (iriToClassFrameMap.containsKey(targetClass.getIRI())) {
					targetClassFrame = iriToClassFrameMap.get(targetClass.getIRI());
				}
				else {
					targetClassFrame = (ClassFrame)getFrame(targetClass, ont);
				}
				PropertyRestriction restriction;
				if (quantRestriction instanceof OWLObjectAllValuesFrom) {
					restriction = new PropertyRestriction(propertyFrame, targetClassFrame, ValueConstraint.ONLY);
				}
				else {
					restriction = new PropertyRestriction(propertyFrame, targetClassFrame, ValueConstraint.SOME);
				}
				return Optional.of(restriction);
			}
		}
		return Optional.absent();
	}
	
	// TODO: DOCUMENT THIS
	public static Set<PropertyRestriction> addRestrictionsToFrame(ClassFrame classFrame, OWLOntology ont, Map<IRI,ClassFrame> iriToClassFrameMap, Map<IRI,ObjectPropertyFrame> iriToObjectPropertyFrameMap) {
		Set<PropertyRestriction> restrictions = new HashSet<PropertyRestriction>();
		if (classFrame.getIri().isPresent()) {
			IRI classIri = classFrame.getIri().get();
			Set<OWLEntity> entities = ont.getEntitiesInSignature(classIri);
			for (OWLEntity entity: entities) {
				if (entity instanceof OWLClass) {
					OWLClass owlClass = entity.asOWLClass();
					Set<OWLClassExpression> equivalentClassExpressions = owlClass.getEquivalentClasses(ont);
					for (OWLClassExpression expression: equivalentClassExpressions) {
						Optional<PropertyRestriction> restriction = restrictionFromOwlExpression(expression, ont, iriToClassFrameMap, iriToObjectPropertyFrameMap);
						if (restriction.isPresent()) {
							classFrame.getEquivalentToRestrictions().add(restriction.get());
						}
					}
					Set<OWLClassExpression> superClassExpressions = owlClass.getSuperClasses(ont);
					for (OWLClassExpression expression: superClassExpressions) {
						Optional<PropertyRestriction> restriction = restrictionFromOwlExpression(expression, ont, iriToClassFrameMap, iriToObjectPropertyFrameMap);
						if (restriction.isPresent()) {
							classFrame.getSubClassOfRestrictions().add(restriction.get());
						}
					}
				}
			}
		}
		return restrictions;
	}
	
	/**
	 * Generate an Ontology Entity Frame from an OWL Entity. Only works with classes, data properties, 
	 * and object properties (otherwise returns null). Frame label will be prefixed with ontology label or fragment
	 * if prefixLabel is true.
	 * @param cls
	 * @param ont
	 * @return
	 */
	public static AbstractOntologyEntityFrame getFrame(OWLEntity entity, OWLOntology ont, Boolean prefixLabel) {
		// Depending on what type of OWLEntity we get in, construct a suitable frame and return it
		
		Set<OWLOntology> ontologyClosure = ont.getOWLOntologyManager().getOntologies();
		
		String entityLabel = AnnotationOperations.getLabel(entity, ont, prefixLabel);
		Optional<String> entityComment = AnnotationOperations.getComment(entity, ont);
		
		if (entity instanceof OWLClass) {
			ClassFrame cf = new ClassFrame(entityLabel);
			cf.setIri(entity.getIRI());
			if (entityComment.isPresent()) {
				cf.setComment(entityComment.get());
			}
			return cf;
		}
		else if (entity instanceof OWLDataProperty) {
			// Create frame
			OWLDataProperty dataProperty = (OWLDataProperty)entity;
			DataPropertyFrame frame = new DataPropertyFrame(entityLabel);
			frame.setIri(dataProperty.getIRI());
			if (entityComment.isPresent()) {
				frame.setComment(entityComment.get());
			}
			
			
			// Get domains
			Set<OWLClassExpression> domainExpressions = dataProperty.getDomains(ontologyClosure);
			Set<LabelOrIri> domains = frame.getDomains();
    		for (OWLClassExpression oce: domainExpressions) {
    			if (oce instanceof OWLClass) {
    				IRI domainIri = ((OWLClass)oce).getIRI();
    				domains.add(new LabelOrIri(domainIri));
    			}
    		}
    		
    		// Get ranges
    		Set<OWLDataRange> rangeExpressions = dataProperty.getRanges(ontologyClosure);
    		Set<IRI> ranges = frame.getRanges();
    		for (OWLDataRange dr: rangeExpressions) {
    			if (dr instanceof OWLDatatype) {
    				IRI rangeIri = ((OWLDatatype) dr).getIRI();
    				ranges.add(rangeIri);
    			}
    		}
    		
    		// Set attributes
			frame.setFunctional(dataProperty.isFunctional(ont));
    		
    		// Return constructed data property frame
			return frame;
		}
		else if (entity instanceof OWLObjectProperty) {
			// Create frame
			OWLObjectProperty objectProperty = (OWLObjectProperty)entity;
			ObjectPropertyFrame frame = new ObjectPropertyFrame(entityLabel);
			frame.setIri(objectProperty.getIRI());
			if (entityComment.isPresent()) {
				frame.setComment(entityComment.get());
			}
			
			// Get domains
			Set<OWLClassExpression> domainExpressions = objectProperty.getDomains(ontologyClosure);
			Set<LabelOrIri> domains = frame.getDomains();
    		for (OWLClassExpression oce: domainExpressions) {
    			if (oce instanceof OWLClass) {
    				IRI domainIri = ((OWLClass)oce).getIRI();
    				domains.add(new LabelOrIri(domainIri));
    			}
    		}
    		
    		// Get ranges
			Set<OWLClassExpression> rangeExpressions = objectProperty.getRanges(ontologyClosure);
			Set<LabelOrIri> ranges = frame.getRanges();
    		for (OWLClassExpression oce: rangeExpressions) {
    			if (oce instanceof OWLClass) {
    				IRI rangeIri = ((OWLClass)oce).getIRI();
    				ranges.add(new LabelOrIri(rangeIri));
    			}
    		}
    		
			// Set attributes
			frame.setSymmetric(objectProperty.isSymmetric(ontologyClosure));
			frame.setFunctional(objectProperty.isFunctional(ontologyClosure));
			frame.setTransitive(objectProperty.isTransitive(ontologyClosure));
			
			// Return constructed object property frame
    		return frame;
		}
		
		// We should never get to here.
		return null;
	}
	
	/**
	 * Generate an Ontology Entity Frame from an OWL Entity. Only works with classes, data properties, 
	 * and object properties (otherwise returns null). Frame label will not be prefixed with ontology label or fragment.
	 * @param cls
	 * @param ont
	 * @return
	 */
	public static AbstractOntologyEntityFrame getFrame(OWLEntity entity, OWLOntology ont) {
		return getFrame(entity, ont, false);
	}
}
