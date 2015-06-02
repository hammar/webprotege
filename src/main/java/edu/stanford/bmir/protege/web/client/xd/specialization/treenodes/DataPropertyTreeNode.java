package edu.stanford.bmir.protege.web.client.xd.specialization.treenodes;

import org.semanticweb.owlapi.model.IRI;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;

public class DataPropertyTreeNode extends OntologyEntityTreeNode {

	private DataPropertyFrame dataPropertyFrame;
	
	public DataPropertyTreeNode(String text) {
		this(text, null);
	}
	
	public DataPropertyTreeNode(String text, IRI iri) {
		super(text,"owlDataPropertyTreeNode", iri);
	}

	public DataPropertyFrame getDataPropertyFrame() {
		return dataPropertyFrame;
	}

	public void setDataPropertyFrame(DataPropertyFrame dataPropertyFrame) {
		this.dataPropertyFrame = dataPropertyFrame;
	}
}
