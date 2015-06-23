package edu.stanford.bmir.protege.web.shared.xd.actions;

import edu.stanford.bmir.protege.web.shared.HasProjectId;
import edu.stanford.bmir.protege.web.shared.dispatch.Action;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;
import edu.stanford.bmir.protege.web.shared.xd.results.GetSpecializationAlignmentSuggestionsResult;

public class GetSpecializationAlignmentSuggestionsAction implements Action<GetSpecializationAlignmentSuggestionsResult>, HasProjectId {
	
	private FrameTreeNode<OntologyEntityFrame> classes;
	private FrameTreeNode<OntologyEntityFrame> dataProperties;
	private FrameTreeNode<OntologyEntityFrame> objectProperties;
	private ProjectId projectId;
	
	/**
	 * GWT-RPC constructor
	 */
	@SuppressWarnings("unused")
	private GetSpecializationAlignmentSuggestionsAction() {
		
	}
	
	public GetSpecializationAlignmentSuggestionsAction(ProjectId projectId,
			FrameTreeNode<OntologyEntityFrame> classes, 
			FrameTreeNode<OntologyEntityFrame> dataProperties, 
			FrameTreeNode<OntologyEntityFrame> objectProperties) {
		this.projectId = projectId;
		this.classes = classes;
		this.dataProperties = dataProperties;
		this.objectProperties = objectProperties;
	}

	public FrameTreeNode<OntologyEntityFrame> getClasses() {
		return classes;
	}

	public FrameTreeNode<OntologyEntityFrame> getDataProperties() {
		return dataProperties;
	}

	public FrameTreeNode<OntologyEntityFrame> getObjectProperties() {
		return objectProperties;
	}

	@Override
	public ProjectId getProjectId() {
		return projectId;
	}
}
