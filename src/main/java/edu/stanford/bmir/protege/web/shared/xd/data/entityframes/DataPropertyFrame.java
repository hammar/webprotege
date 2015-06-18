package edu.stanford.bmir.protege.web.shared.xd.data.entityframes;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

public class DataPropertyFrame extends PropertyFrame {

	private static final long serialVersionUID = -108525613727114368L;
	
	// Private fields
	private Set<IRI> ranges;

	/**
	 * GWT-RPC constructor
	 */
	@SuppressWarnings("unused")
	private DataPropertyFrame() {
	}
	
	public DataPropertyFrame(String label) {
		super(label);
		this.ranges = new HashSet<IRI>();
	}
	
	// OWL-entity constructor
	public DataPropertyFrame(String label, String comment, IRI iri) {
		super(label, comment, iri);
		this.ranges = new HashSet<IRI>();
	}
	
	public Set<IRI> getRanges() {
		return this.ranges;
	}
}
