package edu.stanford.bmir.protege.web.client.xd.instantiation.panels;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.stanford.bmir.protege.web.client.xd.instantiation.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.shared.xd.data.CodpInstantiationMethod;

public class InstantiationMethodSelectionPanel extends FlowPanel implements InstantiationWizardPanel {

	private DesignPatternInstantiationWizard parentWizard;
	private RadioButton templateMethodButton;
	private RadioButton importMethodButton;

	public InstantiationMethodSelectionPanel(DesignPatternInstantiationWizard parent) {
		super();
		
		this.parentWizard = parent;
        this.setTitle("Instantiation Method Selection");
        this.addStyleName("xdpInstantiationMethodSelectionPanel");
        
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
        HTML instruction = new HTML("<strong>Select the appropriate Content Ontology Design Pattern instantiation method "
          		+ "from the choices below. For a discussion on their respective attributes and effects, see "
          		+ "<a href=\"http://goo.gl/dv8pA3\" target=\"_blank\">http://goo.gl/dv8pA3</a></strong>");
        vp.add(instruction);
        vp.add(templateMethodButton);
        String templateText ="In this method the CODP building block is treated as a template that is instantiated into the target ontology module by way of copying and renaming "
        		+ "its constituent classes and properties. Advantages of this method include that CODP-level generic concepts that may be off-putting to less experienced modellers "
        		+ "are not included in the final ontology, but only the CODP structure is kept. Disadvantages include that future alignment to other ontologies using the same CODPs "
        		+ "may be complicated, as the IRIs of COPD-level concepts are not kept."; 
        vp.add(new Label(templateText));
        vp.add(importMethodButton);
        String importText = "In this method the original CODP is imported into the target ontology module, and instantiation is performed via specialization of CODP classes and "
        		+ "properties using subsumption axioms. Advantages of this method include increased traceability and ease of alignment with other CODPs, as IRIs of CODP-level concepts "
        		+ "are maintained.";
        vp.add(new Label(importText));
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
	
	@Override
	public void renderPanel() {
		this.templateMethodButton.setValue(true);
	}
}
