package edu.stanford.bmir.protege.web.shared.xd.data.entityframes;

import java.io.Serializable;

import org.semanticweb.owlapi.model.IRI;

import com.google.common.base.Optional;

// Parent class of class frames, property frames, etc.
public class OntologyEntityFrame implements Serializable {
	
	private static final long serialVersionUID = 3210642949486447600L;
	
	private String label;
	private Optional<String> comment;
	private Optional<IRI> iri;
	
	// GWT-RPC empty constructor
	protected OntologyEntityFrame() {
	}
	
	// Co-comment no-OWL-entity constructor
	public OntologyEntityFrame(String label) {
		this.label = label;
		this.comment = Optional.absent();
		this.iri = Optional.absent();
	}
	
	// -entity constructor
	public OntologyEntityFrame(String label, String comment) {
		this(label);
		this.setComment(comment);
	}
	
	public OntologyEntityFrame(String label, String comment, IRI iri) {
		this(label, comment);
		this.setIri(iri);
	}

	// Access methods
	public String getLabel() {
		return label;
	}

	public Optional<String> getComment() {
		return comment;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public void setComment(String comment) {
		this.comment = Optional.of(comment);
	}
	
	public Optional<IRI> getIri() {
		return iri;
	}	
	
	public void setIri(IRI iri) {
		this.iri = Optional.of(iri);
	}

	public Boolean hasIri() {
		return (this.iri!=null);
	}
}
