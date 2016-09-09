package edu.stanford.bmir.protege.web.client.xd.instantiation.widgets;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import edu.stanford.bmir.protege.web.client.xd.instantiation.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class EntityCloningWidget extends HorizontalPanel {

	private final DesignPatternInstantiationWizard parentWizard;
	private final OntologyEntityFrame entityFrame;
	private final String prefix;
	private TextBox targetTextBox;
	
	public EntityCloningWidget(DesignPatternInstantiationWizard parentWizard, OntologyEntityFrame entityFrame, String prefix) {
		super();
		this.parentWizard = parentWizard;
		this.entityFrame = entityFrame;
		this.prefix = prefix;
		this.targetTextBox = new TextBox();
		this.targetTextBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				EntityCloningWidget.this.entityFrame.setClonedLabel(event.getValue());
				EntityCloningWidget.this.parentWizard.updateInstantiationModificationTimestamp();
			}
		});
		Label prefixLabel = new Label(this.prefix);
		prefixLabel.addStyleName("prefixLabel");
		this.add(prefixLabel);
		
		Label originalLabelLabel = new Label(this.entityFrame.getLabel());
		originalLabelLabel.addStyleName("originalLabelLabel");
		this.add(originalLabelLabel);
		
		Label arrowLabel = new Label("==>");
		arrowLabel.addStyleName("arrowLabel");
		this.add(arrowLabel);
		
		this.targetTextBox.addStyleName("targetLabelTextBox");
		this.add(this.targetTextBox);
		
		// Set initial label that the user may replace if they wish.
		String initialLabel = this.entityFrame.getLabel();
		this.targetTextBox.setText(initialLabel);
		this.entityFrame.setClonedLabel(initialLabel);
	}
	
	public EntityCloningWidget(DesignPatternInstantiationWizard parentWizard, OntologyEntityFrame entityFrame) {
		this(parentWizard, entityFrame, "");
	}
}
