package edu.stanford.bmir.protege.web.client.xd.specialization;

import org.semanticweb.owlapi.model.OWLEntity;

import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.RowLayout;

public abstract class DetailsWindow extends Window {
	
	private XdSpecializationWizard parentWizard;

	public DetailsWindow(XdSpecializationWizard parentWizard) {
		this.parentWizard = parentWizard;
		this.setLayout(new RowLayout());
		this.setWidth(200);
		this.setHeight(320);
		this.add(new Label("In this window we add or edit classes/object properties/datatype properties!"));
		this.initialize();
		
		Panel submitClosePanel = new Panel();
		submitClosePanel.setHeight(30);
		submitClosePanel.setLayout(new HorizontalLayout(0));
		Button submitButton = new Button("Submit");
		submitButton.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(Button button, EventObject e) {
				persistAndClose();
			}
		});
		submitClosePanel.add(submitButton);
		Button closeButton = new Button("Cancel");
		closeButton.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(Button button, EventObject e) {
				hide();
			}
		});
		submitClosePanel.add(closeButton);
		this.add(submitClosePanel);
	}
	
	// Resets this window to empty state
	public abstract void reset();
	
	// Draw the custom window contents
	public abstract void initialize();
	
	// E.g. persist to parent specialization wizard, not to actual
	// ontology!
	public abstract void persistAndClose();
}
