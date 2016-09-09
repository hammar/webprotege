package edu.stanford.bmir.protege.web.client.xd.instantiation.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class EntityTreeNode extends Composite {

	private Label labelWidget;
	
	public EntityTreeNode(OntologyEntityFrame inputFrame) {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(3);
	
		String imageUrl = "";
		if (inputFrame instanceof ClassFrame) {
			imageUrl = "/images/xd/owlClassIcon.png";
		}
		else if (inputFrame instanceof ObjectPropertyFrame) {
			imageUrl = "/images/xd/owlObjectPropertyIcon.png";
		}
		else if (inputFrame instanceof DataPropertyFrame) {
			imageUrl = "/images/xd/owlDataPropertyIcon.png";
		}
		Image image = new Image(imageUrl);
		image.setWidth("20px");
		panel.add(image);
		labelWidget = new Label(inputFrame.getLabel());
		panel.add(labelWidget);
		
		initWidget(panel);
		setStyleName("xdp-EntityTreeNode");
	}
	
	public void setLabelText(String newLabel) {
		this.labelWidget.setText(newLabel);
	}
}
