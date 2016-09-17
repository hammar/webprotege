package edu.stanford.bmir.protege.web.shared.xd.data.entityframes;

import org.semanticweb.owlapi.model.IRI;

import com.google.common.base.Optional;

// Parent class of class frames, property frames, etc.
public class AbstractOntologyEntityFrame implements OntologyEntityFrame {
	
	private static final long serialVersionUID = 3210642949486447600L;
	
	private String label;
	private Optional<String> clonedLabel;
	private Optional<String> comment;
	private Optional<IRI> iri;
	
	// GWT-RPC empty constructor
	protected AbstractOntologyEntityFrame() {
	}
	
	// No-comment no-OWL-entity constructor
	public AbstractOntologyEntityFrame(String label) {
		this.label = label;
		this.clonedLabel = Optional.absent();
		this.comment = Optional.absent();
		this.iri = Optional.absent();
	}
	
	// No-entity constructor
	public AbstractOntologyEntityFrame(String label, String comment) {
		this(label);
		this.setComment(comment);
	}
	
	public AbstractOntologyEntityFrame(String label, String comment, IRI iri) {
		this(label, comment);
		this.setIri(iri);
	}

	// Access methods
	public String getLabel() {
		return this.label;
	}
	
	public String getCurrentLabel() {
		if (this.clonedLabel.isPresent()) {
			if (this.clonedLabel.get().trim().length()>0) {
				return this.clonedLabel.get();
			}
		}
		return this.label;
	}
	
	public Optional<String> getClonedLabel() {
		return this.clonedLabel;
	}

	public Optional<String> getComment() {
		return comment;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public void setClonedLabel(String clonedLabel) {
		this.clonedLabel = Optional.of(clonedLabel);
	}
	
	public void removeClonedLabel() {
		this.clonedLabel = Optional.absent();
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
	public void removeIRI() {
		this.iri = Optional.absent();
	}

	@Override
	public String toString() {
		return this.label;
	}
	
}
