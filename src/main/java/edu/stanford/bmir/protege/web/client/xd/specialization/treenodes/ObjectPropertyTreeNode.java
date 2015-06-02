package edu.stanford.bmir.protege.web.client.xd.specialization.treenodes;

import org.semanticweb.owlapi.model.IRI;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;

public class ObjectPropertyTreeNode extends OntologyEntityTreeNode {

	private ObjectPropertyFrame objectPropertyFrame;
	
	public ObjectPropertyTreeNode(String text) {
		this(text, null);
	}
	
	public ObjectPropertyTreeNode(String text, IRI iri) {
		super(text,"owlObjectPropertyTreeNode", iri);
	}

	public ObjectPropertyFrame getObjectPropertyFrame() {
		return objectPropertyFrame;
	}

	public void setObjectPropertyFrame(ObjectPropertyFrame objectPropertyFrame) {
		this.objectPropertyFrame = objectPropertyFrame;
	}
}
