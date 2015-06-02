package edu.stanford.bmir.protege.web.shared.xd.data.entityframes;

import java.io.Serializable;

import org.semanticweb.owlapi.model.IRI;

// Parent class of class frames, property frames, etc.
public abstract class OntologyEntityFrame implements Serializable {
	
	private static final long serialVersionUID = 3210642949486447600L;
	
	private String label;
	private String comment;
	private IRI iri;
	
	// GWT-RPC empty constructor
	protected OntologyEntityFrame() {
	}
	
	// Co-comment no-OWL-entity constructor
	public OntologyEntityFrame(String label) {
		this(label, null);
	}
	
	// -entity constructor
	public OntologyEntityFrame(String label, String comment) {
		this(label,comment,null);
	}
	
	public OntologyEntityFrame(String label, String comment, IRI iri) {
		this.label = label;
		this.comment = comment;
		this.iri = iri;
	}

	// Access methods
	public String getLabel() {
		return label;
	}

	public String getComment() {
		return comment;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public IRI getIri() {
		return iri;
	}	
	
	public void setIri(IRI iri) {
		this.iri = iri;
	}

	public Boolean hasIri() {
		return (this.iri!=null);
	}
}
