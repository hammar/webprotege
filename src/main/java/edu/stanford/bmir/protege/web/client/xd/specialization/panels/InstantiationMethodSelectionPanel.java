package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.stanford.bmir.protege.web.client.xd.specialization.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.shared.xd.data.CodpInstantiationMethod;

public class InstantiationMethodSelectionPanel extends FlowPanel {

	private DesignPatternInstantiationWizard parentWizard;
	private RadioButton templateMethodButton;
	private RadioButton importMethodButton;

	public InstantiationMethodSelectionPanel(DesignPatternInstantiationWizard parent) {
		super();
		
		this.parentWizard = parent;
        this.setTitle("Instantiation Method Selection");
        
        this.templateMethodButton = new RadioButton("methodGroup","Template-Based Instantiation");
        this.importMethodButton = new RadioButton("methodGroup", "Import-Based Instantiation");
        this.templateMethodButton.setValue(true);
        
        // Add handlers that trigger update of parent instantation method field when user
        // changes selection from the radion buttons.
        ValueChangeHandler<Boolean> methodSelectionButtonsHandler = new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> e) {
            	updateParentWizardInstantiationMethodField();
        }};
        this.templateMethodButton.addValueChangeHandler(methodSelectionButtonsHandler);
        this.importMethodButton.addValueChangeHandler(methodSelectionButtonsHandler);
        
        // Render the instructions and buttons.
        VerticalPanel vp = new VerticalPanel();
        vp.setSpacing(10);
        vp.add(new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque finibus imperdiet orci, a convallis tortor. Nunc luctus turpis nisl, ut posuere ante blandit a. Cras quis ex at elit hendrerit vehicula ut a purus. Nam vestibulum dapibus sapien in pulvinar. Cras pharetra congue suscipit. Etiam dictum erat at placerat blandit."));
        vp.add(templateMethodButton);
        vp.add(new Label("Integer lacus ligula, ultricies ut odio et, vehicula tempor orci. Cras sem lorem, convallis eu felis ac, volutpat fringilla purus. Nulla turpis mauris, pulvinar pretium dignissim at, rhoncus eu leo. In neque tortor, volutpat at tempor vitae, tincidunt ut neque. Donec et consectetur orci."));
        vp.add(importMethodButton);
        vp.add(new Label("Quisque felis risus, rutrum et blandit malesuada, viverra et risus. Nam luctus, neque ac ultrices ultricies, erat odio pellentesque augue, in porttitor arcu nulla et magna."));
        this.add(vp);
	}
	
	private void updateParentWizardInstantiationMethodField() {
		if (this.templateMethodButton.getValue()) {
        	parentWizard.setInstantiationMethod(CodpInstantiationMethod.TEMPLATE_BASED);
		}
        else {
        	parentWizard.setInstantiationMethod(CodpInstantiationMethod.IMPORT_BASED);
        }
	}
}
