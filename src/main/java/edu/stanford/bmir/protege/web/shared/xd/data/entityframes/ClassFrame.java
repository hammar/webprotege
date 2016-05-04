package edu.stanford.bmir.protege.web.shared.xd.data.entityframes;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

import edu.stanford.bmir.protege.web.shared.xd.data.PropertyRestriction;

public class ClassFrame extends AbstractOntologyEntityFrame {
	
	private static final long serialVersionUID = -6646680694888948769L;
	
	private Set<PropertyRestriction> equivalentToRestrictions;
	private Set<PropertyRestriction> subClassOfRestrictions;
	
	/**
	 * GWT-RPC constructor
	 */
	@SuppressWarnings("unused")
	private ClassFrame() {
	}
	
	public ClassFrame(String label) {
		super(label);
		this.equivalentToRestrictions = new HashSet<PropertyRestriction>();
		this.subClassOfRestrictions = new HashSet<PropertyRestriction>();
	}
	
	public ClassFrame(String label, String comment) {
		super(label, comment);
		this.equivalentToRestrictions = new HashSet<PropertyRestriction>();
		this.subClassOfRestrictions = new HashSet<PropertyRestriction>();
	}
	
	public ClassFrame(String label, String comment, IRI iri) {
		super(label, comment, iri);
		this.equivalentToRestrictions = new HashSet<PropertyRestriction>();
		this.subClassOfRestrictions = new HashSet<PropertyRestriction>();
	}
	
	public Set<PropertyRestriction> getEquivalentToRestrictions() {
		return equivalentToRestrictions;
	}

	public Set<PropertyRestriction> getSubClassOfRestrictions() {
		return subClassOfRestrictions;
	}
}
