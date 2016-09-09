package edu.stanford.bmir.protege.web.shared.xd.data;

import java.io.Serializable;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class OdpInstantiation implements Serializable {
	private static final long serialVersionUID = 3079727766627945835L;
	
	/**
	 * GWT-RPC-required empty constructor.
	 */
	@SuppressWarnings("unused")
	private OdpInstantiation() {
	}
	
	public OdpInstantiation(ProjectId projectId,
			IRI odpIri,
			FrameTreeNode<OntologyEntityFrame> classFrameTree,
			FrameTreeNode<OntologyEntityFrame> objectPropertyFrameTree,
			FrameTreeNode<OntologyEntityFrame> dataPropertyFrameTree,
			Set<Alignment> alignments, 
			CodpInstantiationMethod instantiationMethod,
			OdpSpecializationStrategy specializationStrategy) {
		this.projectId = projectId;
		this.odpIri = odpIri;
		this.classFrameTree = classFrameTree;
		this.objectPropertyFrameTree = objectPropertyFrameTree;
		this.dataPropertyFrameTree = dataPropertyFrameTree;
		this.alignments = alignments;
		this.instantiationMethod = instantiationMethod;
		this.specializationStrategy = specializationStrategy;
	}
	
	// Private fields
	private ProjectId projectId;
	private IRI odpIri;
	private FrameTreeNode<OntologyEntityFrame> classFrameTree;
	private FrameTreeNode<OntologyEntityFrame> objectPropertyFrameTree;
	private FrameTreeNode<OntologyEntityFrame> dataPropertyFrameTree;
	private Set<Alignment> alignments;
	private CodpInstantiationMethod instantiationMethod;
	private OdpSpecializationStrategy specializationStrategy;
	
	// Access methods
	public ProjectId getProjectId() {
		return this.projectId;
	}
	public IRI getOdpIri() {
		return this.odpIri;
	}
	public FrameTreeNode<OntologyEntityFrame> getClassFrameTree() {
		return classFrameTree;
	}
	public FrameTreeNode<OntologyEntityFrame> getObjectPropertyFrameTree() {
		return objectPropertyFrameTree;
	}
	public FrameTreeNode<OntologyEntityFrame> getDataPropertyFrameTree() {
		return dataPropertyFrameTree;
	}
	public Set<Alignment> getAlignments() {
		return this.alignments;
	}
	public CodpInstantiationMethod getInstantiationMethod() {
		return this.instantiationMethod;
	}
	public OdpSpecializationStrategy getSpecializationStrategy() {
		return this.specializationStrategy;
	}
}
