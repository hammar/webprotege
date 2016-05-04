package edu.stanford.bmir.protege.web.client.ui.visualization.composites;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ExtendedMenuItem extends MenuItem implements ClickHandler {

	private TextBox textBox;
	private CheckBox checkBox = new CheckBox();
	private VerticalPanel panel = new VerticalPanel();
	
	public ExtendedMenuItem(String html, boolean htmlON, ScheduledCommand cmd) {
		super(html, htmlON, cmd);
		textBox = new TextBox();
		panel.add(textBox);
		textBox.setTitle(html);
		textBox.setValue("200");
	}


	/*public ExtendedMenuItem(String html, boolean htmlON, ScheduledCommand cmd, boolean checked) {
	    super(html, htmlON, cmd);
	 // Place the check above the text box using a vertical panel.
	    panel = new VerticalPanel();
	    textBox = new TextBox();
	 		checkBox.setText(html);
	 		checkBox.setChecked(checked);
	 		checkBox.addClickHandler(this);

	 		// All composites must call initWidget() in their constructors.
	 		//initWidget(panel);

	 		// Give the overall composite a style name.
	 		setStyleName("example-OptionalCheckBox");
	  }*/


	public ExtendedMenuItem(ScheduledCommand cmd, TextBox textBox) {
		super(textBox.getTitle(), true, cmd);
		this.textBox = textBox;
	}


	public TextBox getTextBox() {
		return textBox;
	}

	public void onClick(ClickEvent event) {

		//new MyPopup().show();

		Object sender = event.getSource();

		/*
		if (sender == checkBox) {
			// When the check box is clicked, update the text box's enabled state.
			textBox.setEnabled(checkBox.isChecked());
		}
		 */

		if (sender instanceof CheckBox) {
			// When the check box is clicked, update the text box's enabled state.
			if(checkBox.getValue())
				Window.alert("Checked");
			else
				Window.alert("Checked");
		}
	}

	/**
	 * Sets the caption associated with the check box.
	 * 
	 * @param caption the check box's caption
	 */
	public void setCaption(String caption) {
		// Note how we use the use composition of the contained widgets to provide
		// only the methods that we want to.
		checkBox.setText(caption);
	}

	/**
	 * Gets the caption associated with the check box.
	 * 
	 * @return the check box's caption
	 */
	public String getCaption() {
		return checkBox.getText();
	}

	public VerticalPanel getPanel() {
		return panel;
	}	
}
