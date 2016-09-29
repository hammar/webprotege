package edu.stanford.bmir.protege.web.client.xd.visualization.vowl;

import com.google.gwt.user.client.ui.Widget;

import edu.stanford.bmir.protege.web.shared.project.ProjectId;

public class VOWLDetailsViewPresenter {
	
	private VOWLDetailsView view;
    public boolean loaded = false;
    
    public VOWLDetailsViewPresenter(ProjectId projectId, VOWLDetailsView view) {
        this.view = view;
    }

    public Widget getWidget() {
        return view.getWidget();
    }
}
