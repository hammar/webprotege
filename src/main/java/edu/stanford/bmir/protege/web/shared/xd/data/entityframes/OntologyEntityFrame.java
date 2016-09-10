package edu.stanford.bmir.protege.web.shared.xd.data.entityframes;

import java.io.Serializable;

import org.semanticweb.owlapi.model.IRI;

import com.google.common.base.Optional;

public interface OntologyEntityFrame extends Serializable {
	public String getLabel();
	public void setLabel(String label);
	
	public String getCurrentLabel();
	
	public Optional<String> getClonedLabel();
	public void setClonedLabel(String clonedLabel);

	public Optional<String> getComment();
	public void setComment(String comment);
	
	public Optional<IRI> getIri();
	public void setIri(IRI iri);
	public void removeIRI();
}
