package edu.stanford.bmir.protege.web.shared.xd.data;

import java.io.Serializable;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class OdpSpecialization implements Serializable {
	private static final long serialVersionUID = 3079727766627945835L;
	
	/**
	 * GWT-RPC-required empty constructor.
	 */
	@SuppressWarnings("unused")
	private OdpSpecialization() {
	}
	
	public OdpSpecialization(ProjectId projectId,
			IRI odpIri,
			OdpSpecializationStrategy specializationStrategy,
			Set<Alignment> alignments, 
			Set<FrameTreeNode<OntologyEntityFrame>> classFrameTrees,
			Set<FrameTreeNode<OntologyEntityFrame>> objectPropertyFrameTrees,
			Set<FrameTreeNode<OntologyEntityFrame>> dataPropertyFrameTrees) {
		this.projectId = projectId;
		this.odpIri = odpIri;
		this.specializationStrategy = specializationStrategy;
		this.alignments = alignments;
		this.classFrameTrees = classFrameTrees;
		this.objectPropertyFrameTrees = objectPropertyFrameTrees;
		this.dataPropertyFrameTrees = dataPropertyFrameTrees;
	}
	
	// Private fields
	private ProjectId projectId;
	private IRI odpIri;
	private OdpSpecializationStrategy specializationStrategy;
	private Set<Alignment> alignments;
	private Set<FrameTreeNode<OntologyEntityFrame>> classFrameTrees;
	private Set<FrameTreeNode<OntologyEntityFrame>> objectPropertyFrameTrees;
	private Set<FrameTreeNode<OntologyEntityFrame>> dataPropertyFrameTrees;
	
	// Access methods
	public ProjectId getProjectId() {
		return this.projectId;
	}
	public IRI getOdpIri() {
		return this.odpIri;
	}
	public OdpSpecializationStrategy getSpecializationStrategy() {
		return this.specializationStrategy;
	}
	public Set<Alignment> getAlignments() {
		return this.alignments;
	}
	public Set<FrameTreeNode<OntologyEntityFrame>> getClassFrameTrees() {
		return classFrameTrees;
	}
	public Set<FrameTreeNode<OntologyEntityFrame>> getObjectPropertyFrameTrees() {
		return objectPropertyFrameTrees;
	}
	public Set<FrameTreeNode<OntologyEntityFrame>> getDataPropertyFrameTrees() {
		return dataPropertyFrameTrees;
	}
}
