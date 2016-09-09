package edu.stanford.bmir.protege.web.client.xd.instantiation.panels;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import edu.stanford.bmir.protege.web.client.xd.instantiation.DesignPatternInstantiationWizard;

public class RestrictionsPanel extends FlowPanel implements InstantiationWizardPanel {
	
	private DesignPatternInstantiationWizard parentWizard;

	public RestrictionsPanel(DesignPatternInstantiationWizard parentWizard) {
		super();
		this.parentWizard = parentWizard;
		
		this.setTitle("Restrictions");
		this.add(new Label("Restrictions interface to be implemented."));
	}

	@Override
	public void renderPanel() {
		// TODO Auto-generated method stub
	}
}
