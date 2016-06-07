package edu.stanford.bmir.protege.web.client.xd.specialization.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class EntityTreeNode extends Composite {

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
		panel.add(new Label(inputFrame.getLabel()));
		
		initWidget(panel);
		setStyleName("xdp-EntityTreeNode");
	}
}
