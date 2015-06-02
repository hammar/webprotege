package edu.stanford.bmir.protege.web.shared.xd.data.entityframes;

import org.semanticweb.owlapi.model.IRI;

public class DataPropertyFrame extends PropertyFrame {

	private static final long serialVersionUID = -108525613727114368L;
	
	// Private fields
	private IRI[] ranges;

	/**
	 * GWT-RPC constructor
	 */
	@SuppressWarnings("unused")
	private DataPropertyFrame() {
	}
	
	// No-OWL-entity constructor
	public DataPropertyFrame(String label, String comment) {
		this(label, comment, null);
	}
	
	// OWL-entity constructor
	public DataPropertyFrame(String label, String comment, IRI iri) {
		super(label, comment, iri);
	}
	
	public void setRanges(IRI[] ranges) {
		this.ranges = ranges;
	}
	
	public IRI[] getRanges() {
		return this.ranges;
	}
}
