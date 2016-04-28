package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.widgets.Panel;

public class InstantiationMethodSelectionPanel extends Panel {

	private RadioButton templateMethodButton;
	private RadioButton importMethodButton;

	public InstantiationMethodSelectionPanel() {
		super();
		
		this.setBorder(false);  
        this.setId("card-0");
        this.setTitle("Instantiation Method Selection");
        
        this.templateMethodButton = new RadioButton("methodGroup","Template-Based Instantiation");
        this.importMethodButton = new RadioButton("methodGroup", "Import-Based Instantiation");
        
        VerticalPanel vp = new VerticalPanel();
        vp.setSpacing(10);
        vp.add(new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque finibus imperdiet orci, a convallis tortor. Nunc luctus turpis nisl, ut posuere ante blandit a. Cras quis ex at elit hendrerit vehicula ut a purus. Nam vestibulum dapibus sapien in pulvinar. Cras pharetra congue suscipit. Etiam dictum erat at placerat blandit."));
        vp.add(templateMethodButton);
        vp.add(new Label("Integer lacus ligula, ultricies ut odio et, vehicula tempor orci. Cras sem lorem, convallis eu felis ac, volutpat fringilla purus. Nulla turpis mauris, pulvinar pretium dignissim at, rhoncus eu leo. In neque tortor, volutpat at tempor vitae, tincidunt ut neque. Donec et consectetur orci."));
        vp.add(importMethodButton);
        vp.add(new Label("Quisque felis risus, rutrum et blandit malesuada, viverra et risus. Nam luctus, neque ac ultrices ultricies, erat odio pellentesque augue, in porttitor arcu nulla et magna."));
        this.add(vp);
	}
}
