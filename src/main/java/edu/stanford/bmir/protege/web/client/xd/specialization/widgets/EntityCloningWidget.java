package edu.stanford.bmir.protege.web.client.xd.specialization.widgets;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import edu.stanford.bmir.protege.web.client.xd.specialization.DesignPatternInstantiationWizard;

public class EntityCloningWidget extends HorizontalPanel {

	private final DesignPatternInstantiationWizard parentWizard;
	private final String originalLabel;
	private TextBox targetTextBox;
	
	public EntityCloningWidget(DesignPatternInstantiationWizard parentWizard, String originalLabel) {
		super();
		this.parentWizard = parentWizard;
		this.originalLabel = originalLabel;
		this.targetTextBox = new TextBox();
		this.targetTextBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				EntityCloningWidget.this.parentWizard.setClonedEntityLabel(EntityCloningWidget.this.originalLabel, event.getValue());
			}
		});
		this.add(new Label(this.originalLabel));
		this.add(new Label("==>"));
		this.add(this.targetTextBox);
	}
}
