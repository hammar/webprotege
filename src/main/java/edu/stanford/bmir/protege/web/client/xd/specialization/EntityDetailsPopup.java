package edu.stanford.bmir.protege.web.client.xd.specialization;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class EntityDetailsPopup extends PopupPanel {
	
	public EntityDetailsPopup(DesignPatternInstantiationWizard parentWizard) {
		super(false, true);
		
		// TODO: Build this.
		FlowPanel panel = new FlowPanel();
		panel.add(new Label("Hello world"));
		
		this.setWidget(panel);
	}
}
