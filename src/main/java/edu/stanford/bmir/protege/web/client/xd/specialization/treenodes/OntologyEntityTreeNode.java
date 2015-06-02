package edu.stanford.bmir.protege.web.client.xd.specialization.treenodes;

import org.semanticweb.owlapi.model.IRI;

import com.gwtext.client.widgets.tree.TreeNode;

public abstract class OntologyEntityTreeNode extends TreeNode {

	private IRI iri;
	
	protected OntologyEntityTreeNode(String text, String iconCls, IRI iri) {
		super(text, iconCls);
		this.iri = iri;
	}
	
	public IRI getIri() {
		return iri;
	}

	public void setIri(IRI iri) {
		this.iri = iri;
	}
	
	public boolean hasIri() {
		return (this.iri!=null);
	}
}
