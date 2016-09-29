package edu.stanford.bmir.protege.web.client.xd.instantiation;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Spinner extends PopupPanel {
	
	private Label spinnerLabel = new Label();
	private Image spinnerImage = new Image("/images/xd/xdpAjaxLoader.gif");

	public Spinner() {
		super(false,true);
		this.addStyleName("xdpInstantiationWizardSpinner");
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setSpacing(10);
		vPanel.setWidth("150px");
		vPanel.setHeight("80px");
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vPanel.add(spinnerImage);
		vPanel.add(spinnerLabel);
		this.add(vPanel);
	}
	
	public void setText(String text) {
		this.spinnerLabel.setText(text);
	}
}
