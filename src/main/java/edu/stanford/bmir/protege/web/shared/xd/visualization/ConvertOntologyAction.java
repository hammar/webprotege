package edu.stanford.bmir.protege.web.shared.xd.visualization;

import edu.stanford.bmir.protege.web.shared.dispatch.Action;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

public class ConvertOntologyAction implements Action<ConvertOntologyResult> {

	private ProjectId projectId;
	
	ConvertOntologyAction() {

	}
	
	public ConvertOntologyAction(ProjectId projectId) {
		this.projectId = projectId;
	}
	
	public ProjectId getProjectID() {
		return projectId;
	}
	
}
