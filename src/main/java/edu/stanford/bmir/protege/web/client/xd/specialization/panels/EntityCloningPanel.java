package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.stanford.bmir.protege.web.client.xd.specialization.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.client.xd.specialization.widgets.EntityCloningWidget;

public class EntityCloningPanel extends VerticalPanel {
	
	VerticalPanel classHolderPanel;
	VerticalPanel opHolderPanel;
	VerticalPanel dpHolderPanel;
	
	public EntityCloningPanel(DesignPatternInstantiationWizard parentWizard) {
		super();
		this.parentWizard = parentWizard;
		
		this.classHolderPanel = new VerticalPanel();
		this.opHolderPanel = new VerticalPanel();
		this.dpHolderPanel = new VerticalPanel();
		
		this.setTitle("Entity Cloning");
		this.add(new Label("Classes"));
		this.add(this.classHolderPanel);
		this.add(new Label("Object Properties"));
		this.add(this.opHolderPanel);
		this.add(new Label("Datatype Properties"));
		this.add(this.dpHolderPanel);
	}

	public void render() {
		this.classHolderPanel.add(new EntityCloningWidget(this.parentWizard, "Testlabel 1"));
		this.classHolderPanel.add(new EntityCloningWidget(this.parentWizard, "Testlabel 2"));
		this.classHolderPanel.add(new EntityCloningWidget(this.parentWizard, "Testlabel 3"));
	}
	
	private DesignPatternInstantiationWizard parentWizard;

}
