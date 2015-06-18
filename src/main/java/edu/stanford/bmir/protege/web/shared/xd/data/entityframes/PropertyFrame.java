package edu.stanford.bmir.protege.web.shared.xd.data.entityframes;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

import edu.stanford.bmir.protege.web.shared.xd.data.LabelOrIri;

public abstract class PropertyFrame extends AbstractOntologyEntityFrame {

	private static final long serialVersionUID = -108525613727114368L;
	
	// Private fields
	private boolean functional;
	private Set<LabelOrIri> domains;

	/**
	 * GWT-RPC constructor
	 */
	protected PropertyFrame() {
	}
	
	public PropertyFrame(String label) {
		super(label);
		this.domains = new HashSet<LabelOrIri>();
	}
	
	// No-OWL-entity constructor
	public PropertyFrame(String label, String comment, IRI iri) {
		super(label, comment, iri);
		this.domains = new HashSet<LabelOrIri>();
	}
	
	// Access methods
	public boolean getFunctional() {
		return functional;
	}

	public void setFunctional(boolean functional) {
		this.functional = functional;
	}

	public Set<LabelOrIri> getDomains() {
		return this.domains;
	}
}
