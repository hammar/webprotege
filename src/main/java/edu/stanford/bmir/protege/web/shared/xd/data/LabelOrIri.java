package edu.stanford.bmir.protege.web.shared.xd.data;

import java.io.Serializable;

import org.semanticweb.owlapi.model.IRI;

// Represents an object that client code does not necessarily know whether it is
// persisted and has received an IRI yet or not. If no IRI exists, then client
// code will have to check (either against the ontology project manager, or against
// some in memory list of recently created entities) what IRI corresponds to the
// string label held by this class.
public class LabelOrIri implements Serializable {

	private static final long serialVersionUID = -5591044130131535746L;

	// Member fields
	private String label;
	private IRI iri;
	
	/**
	 * GWT-RPC constructor
	 */
	@SuppressWarnings("unused")
	private LabelOrIri() {
	}
	
	// String constructor
	public LabelOrIri(String label) {
		this.label = label;
	}
	
	// IRI constructor
	public LabelOrIri(IRI iri) {
		this.iri = iri;
	}
	
	// What type is this?
	public Boolean isLabel() {
		if (label != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	// If not label, then it must be IRI..
	public Boolean isIri() {
		return !isLabel();
	}
	
	// Access method
	public Object getValue() {
		if (isLabel()) {
			return this.label;
		}
		else {
			return this.iri;
		}
	}
}