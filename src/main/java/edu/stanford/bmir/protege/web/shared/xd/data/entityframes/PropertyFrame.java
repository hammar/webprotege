package edu.stanford.bmir.protege.web.shared.xd.data.entityframes;

import org.semanticweb.owlapi.model.IRI;

import edu.stanford.bmir.protege.web.shared.xd.data.LabelOrIri;

public abstract class PropertyFrame extends OntologyEntityFrame {

	private static final long serialVersionUID = -108525613727114368L;
	
	// Private fields
	private boolean functional;
	private LabelOrIri[] domains;

	/**
	 * GWT-RPC constructor
	 */
	protected PropertyFrame() {
	}
	
	// No-OWL-entity constructor
	public PropertyFrame(String label, String comment, IRI iri) {
		super(label, comment, iri);
	}
	
	// Access methods
	public boolean getFunctional() {
		return functional;
	}

	public void setFunctional(boolean functional) {
		this.functional = functional;
	}

	public void setDomains(LabelOrIri[] domains) {
		this.domains = domains;
	}

	public LabelOrIri[] getDomains() {
		return this.domains;
	}
}
