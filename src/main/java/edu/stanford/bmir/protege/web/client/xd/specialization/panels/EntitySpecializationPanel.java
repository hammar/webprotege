package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import edu.stanford.bmir.protege.web.client.xd.specialization.DesignPatternInstantiationWizard;

public class EntitySpecializationPanel extends FlowPanel implements InstantiationWizardPanel {
	
	public EntitySpecializationPanel(DesignPatternInstantiationWizard parentWizard) {
		super();
		this.parentWizard = parentWizard;
		
		this.setTitle("Entity Specialization");
		this.add(new Label("Entity specialization interface to be implemented."));
	}

	private DesignPatternInstantiationWizard parentWizard;

	@Override
	public void renderPanel() {
		// TODO Auto-generated method stub
	}
}
