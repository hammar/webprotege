package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import edu.stanford.bmir.protege.web.client.xd.specialization.DesignPatternInstantiationWizard;

public class EntityCloningPanel extends FlowPanel {
	
	public EntityCloningPanel(DesignPatternInstantiationWizard parentWizard) {
		super();
		this.parentWizard = parentWizard;
		
		this.setTitle("Entity Cloning");
		this.add(new Label("Entity cloning interface to be implemented."));
	}

	private DesignPatternInstantiationWizard parentWizard;

}
