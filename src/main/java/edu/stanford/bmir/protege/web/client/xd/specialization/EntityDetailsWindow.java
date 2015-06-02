package edu.stanford.bmir.protege.web.client.xd.specialization;

import java.util.Iterator;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.RowLayout;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class EntityDetailsWindow extends Window implements HasWidgets {

	/**
	 * This is only to hide a bug in the GWT-EXT Window class which otherwise causes compilation
	 * warnings for every single subclass..
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Widget> iterator() {
		return super.iterator();
	}

	private OntologyEntityFrame frame;
	private TextField labelField;
	private TextField commentField;

	public EntityDetailsWindow() {
		this.setLayout(new RowLayout());
		this.setWidth(200);
		this.setHeight(320);
		this.add(new Label("In this window we add or edit classes/object properties/datatype properties!"));
		
		FormPanel formPanel = new FormPanel();
		labelField = new TextField("Class name", "className");
		labelField.setAllowBlank(false);
		formPanel.add(labelField);  
		commentField = new TextField("Comment", "classComment");
		commentField.setAllowBlank(true);
		formPanel.add(commentField);
		this.add(formPanel);
		
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
	public void reset() {
		this.frame = null;
		this.labelField.setValue("");
		this.commentField.setValue("");
	}
	
	// I.e., persist to parent specialization wizard, not to actual ontology!
	public void persistAndClose() {
		frame.setLabel(labelField.getValueAsString());
		frame.setComment(commentField.getValueAsString());
		this.close();
	}
	
	// Load the frame details
	public void loadFrame(OntologyEntityFrame frame) {
		this.reset();
		this.frame = frame;
		labelField.setValue(frame.getLabel());
		if (frame.getComment() != null) {
			commentField.setValue(frame.getComment());
		}
	}
}
