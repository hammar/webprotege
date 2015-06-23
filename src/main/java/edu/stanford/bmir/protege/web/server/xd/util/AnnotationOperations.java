package edu.stanford.bmir.protege.web.server.xd.util;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import com.google.common.base.Optional;

import edu.stanford.bmir.protege.web.shared.DataFactory;

public class AnnotationOperations {

	public static OWLAnnotationProperty rdfsLabel = DataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
	public static OWLAnnotationProperty rdfsComment = DataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());
	
	/**
	 * Gets entity label, optionally prefiex by ontology name. If no RDFS label exists for ontology 
	 * or for concept exist, uses local IRI fragments.
	 * @param entity - Entity to look for.
	 * @param prefixed - If true, prefixes label by ontology name and a colon.
	 * @return
	 */
	public static String getLabel(OWLEntity entity, OWLOntology ont, Boolean prefixed) {
		String ontologyPrefix = OntologyOperations.getOntologyPrefix(ont);
		Optional<String> entityLabel = getAnnotationValue(entity, ont, rdfsLabel, "en");
		if (entityLabel.isPresent()) {
			if (prefixed) {
				return String.format("%s: %s",ontologyPrefix,entityLabel.get());
			}
			else {
				return entityLabel.get();
			}
		}
		else {
			return entity.getIRI().getFragment();
		}
	}
	
	public static Optional<String> getComment(OWLEntity entity, OWLOntology ont) {
		return getAnnotationValue(entity, ont, rdfsComment, "en");
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
	private static Optional<String> getAnnotationValue(OWLEntity entity, OWLOntology ont, OWLAnnotationProperty annotationProperty, String lang) {
		String candidateAnswer = null;
		for (OWLAnnotation annotation : entity.getAnnotations(ont, annotationProperty)) {
            if (annotation.getValue() instanceof OWLLiteral) {
            	OWLLiteral val = (OWLLiteral) annotation.getValue();
                if (val.hasLang(lang)) {
                	return Optional.of(val.getLiteral());
                }
                if (!val.hasLang()) {
                	candidateAnswer = val.getLiteral();
                }
            }
        }
		return Optional.fromNullable(candidateAnswer);
	}
}
