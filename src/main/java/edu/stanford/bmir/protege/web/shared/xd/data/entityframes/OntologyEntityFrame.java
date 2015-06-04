package edu.stanford.bmir.protege.web.shared.xd.data.entityframes;

import java.io.Serializable;

import org.semanticweb.owlapi.model.IRI;

import com.google.common.base.Optional;

public interface OntologyEntityFrame extends Serializable {
	public String getLabel();
	public Optional<String> getComment();
	public Optional<IRI> getIri();
}
