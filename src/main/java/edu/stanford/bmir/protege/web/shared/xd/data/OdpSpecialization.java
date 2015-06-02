package edu.stanford.bmir.protege.web.shared.xd.data;

import java.io.Serializable;

import org.semanticweb.owlapi.model.IRI;

import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;

public class OdpSpecialization implements Serializable {
	private static final long serialVersionUID = 3079727766627945835L;
	
	/**
	 * GWT-RPC-required empty constructor.
	 */
	@SuppressWarnings("unused")
	private OdpSpecialization() {
	}
	
	public OdpSpecialization(IRI odpIri,
			OdpSpecializationStrategy specializationStrategy,
			Alignment[] alignments, XdTreeNode<ClassFrame>[] classFrameTrees,
			XdTreeNode<ObjectPropertyFrame>[] objectPropertyFrameTrees,
			XdTreeNode<DataPropertyFrame>[] dataPropertyFrameTrees) {
		this.odpIri = odpIri;
		this.specializationStrategy = specializationStrategy;
		this.alignments = alignments;
		this.classFrameTrees = classFrameTrees;
		this.objectPropertyFrameTrees = objectPropertyFrameTrees;
		this.dataPropertyFrameTrees = dataPropertyFrameTrees;
	}
	
	// Private fields
	private IRI odpIri;
	private OdpSpecializationStrategy specializationStrategy;
	private Alignment[] alignments;
	private XdTreeNode<ClassFrame>[] classFrameTrees;
	private XdTreeNode<ObjectPropertyFrame>[] objectPropertyFrameTrees;
	private XdTreeNode<DataPropertyFrame>[] dataPropertyFrameTrees;
	
	// Access methods
	public IRI getOdpIri() {
		return this.odpIri;
	}
	public OdpSpecializationStrategy getSpecializationStrategy() {
		return this.specializationStrategy;
	}
	public Alignment[] getAlignments() {
		return this.alignments;
	}
	public XdTreeNode<ClassFrame>[] getClassFrameTrees() {
		return classFrameTrees;
	}
	public XdTreeNode<ObjectPropertyFrame>[] getObjectPropertyFrameTrees() {
		return objectPropertyFrameTrees;
	}
	public XdTreeNode<DataPropertyFrame>[] getDataPropertyFrameTrees() {
		return dataPropertyFrameTrees;
	}
}
