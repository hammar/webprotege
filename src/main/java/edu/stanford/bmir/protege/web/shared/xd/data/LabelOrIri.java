package edu.stanford.bmir.protege.web.shared.xd.data;

import java.io.Serializable;

import org.semanticweb.owlapi.model.IRI;

import com.google.common.base.Optional;

// Represents an object that client code does not necessarily know whether it is
// persisted and has received an IRI yet or not. If no IRI exists, then client
// code will have to check (either against the ontology project manager, or against
// some in memory list of recently created entities) what IRI corresponds to the
// string label held by this class.
public class LabelOrIri implements Serializable {

	private static final long serialVersionUID = -5591044130131535746L;

	// Member fields
	private Optional<String> label;
	private Optional<IRI> iri;
	
	/**
	 * GWT-RPC constructor
	 */
	@SuppressWarnings("unused")
	private LabelOrIri() {
	}
	
	// String constructor
	public LabelOrIri(String label) {
		this.label = Optional.of(label);
		this.iri = Optional.absent();
	}
	
	// IRI constructor
	public LabelOrIri(IRI iri) {
		this.iri = Optional.of(iri);
		this.label = Optional.absent();
	}

	// Access methods
	public Optional<String> getLabel() {
		return label;
	}

	public Optional<IRI> getIri() {
		return iri;
	}

	@Override
	public String toString() {
		if (iri.isPresent()) {
			return iri.get().toString();
		}
		else {
			return label.get().toString();
		}
	}
}