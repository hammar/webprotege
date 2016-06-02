package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import edu.stanford.bmir.protege.web.client.xd.specialization.DesignPatternInstantiationWizard;

public class RestrictionsPanel extends FlowPanel {
	
	private DesignPatternInstantiationWizard parentWizard;

	public RestrictionsPanel(DesignPatternInstantiationWizard parentWizard) {
		super();
		this.parentWizard = parentWizard;
		
		this.setTitle("Restrictions");
		this.add(new Label("Restrictions interface to be implemented."));
	}

}
