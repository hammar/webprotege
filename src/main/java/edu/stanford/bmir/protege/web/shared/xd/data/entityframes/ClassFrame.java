package edu.stanford.bmir.protege.web.shared.xd.data.entityframes;

import org.semanticweb.owlapi.model.IRI;

public class ClassFrame extends OntologyEntityFrame {
	
	private static final long serialVersionUID = -6646680694888948769L;

	/**
	 * GWT-RPC constructor
	 */
	@SuppressWarnings("unused")
	private ClassFrame() {
	}
	
	public ClassFrame(String label) {
		super(label);
	}
	
	public ClassFrame(String label, String comment) {
		super(label, comment);
	}
	
	public ClassFrame(String label, String comment, IRI iri) {
		super(label, comment, iri);
	}

}
