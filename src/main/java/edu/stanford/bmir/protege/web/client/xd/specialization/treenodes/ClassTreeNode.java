package edu.stanford.bmir.protege.web.client.xd.specialization.treenodes;

import org.semanticweb.owlapi.model.IRI;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;

public class ClassTreeNode extends OntologyEntityTreeNode {

	private ClassFrame classFrame;
	
	public ClassTreeNode(String text) {
		this(text, null);
	}
	
	public ClassTreeNode(String text, IRI iri) {
		super(text,"owlClassTreeNode", iri);
	}

	public ClassFrame getClassFrame() {
		return classFrame;
	}

	public void setClassFrame(ClassFrame classFrame) {
		this.classFrame = classFrame;
	}
}
