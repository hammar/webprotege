package edu.stanford.bmir.protege.web.shared.xd.data.entityframes;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

import edu.stanford.bmir.protege.web.shared.xd.data.LabelOrIri;

public class ObjectPropertyFrame extends PropertyFrame implements Serializable {

	private static final long serialVersionUID = -108525613727114368L;
	
	// Private fields
	private Set<LabelOrIri> ranges;
	private boolean symmetric;
	private boolean transitive;

	/**
	 * GWT-RPC-required constructor
	 */
	@SuppressWarnings("unused")
	private ObjectPropertyFrame() {
	}
	
	public ObjectPropertyFrame(String label) {
		super(label);
		this.symmetric = false;
		this.transitive = false;
		this.ranges = new HashSet<LabelOrIri>();
	}
	
	// No-OWL-entity constructor
	/*public ObjectPropertyFrame(String label, String comment) {
		this(label, comment, null);
	}*/
	
	// OWL-entity constructor
	public ObjectPropertyFrame(String label, String comment, IRI iri) {
		super(label, comment, iri);
		this.symmetric = false;
		this.transitive = false;
		this.ranges = new HashSet<LabelOrIri>();
	}
	
	public void setRanges(Set<LabelOrIri> ranges) {
		this.ranges = ranges;
	}
	
	public Set<LabelOrIri> getRanges() {
		return this.ranges;
	}

	public boolean getSymmetric() {
		return symmetric;
	}

	public void setSymmetric(boolean symmetric) {
		this.symmetric = symmetric;
	}

	public boolean getTransitive() {
		return transitive;
	}

	public void setTransitive(boolean transitive) {
		this.transitive = transitive;
	}
}
