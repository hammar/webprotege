package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import edu.stanford.bmir.protege.web.client.xd.specialization.DesignPatternInstantiationWizard;

public class AlignmentsPanel extends DockLayoutPanel {
	
	private DesignPatternInstantiationWizard parentWizard;
	
	public AlignmentsPanel(DesignPatternInstantiationWizard parent) {
		super(Unit.EM);
		
		this.parentWizard = parent;
		
        this.setTitle("CODP Specialisation Alignment");
        
        FlowPanel instructionPanel = new FlowPanel();
        Label instruction = new Label("From the suggested entity alignments listed below, constructed based on your specialised "
        		+ "entities and the existing ontology entities, please select the ones that hold within your model. Please note "
        		+ "that for multiple simultanous selection to work you must check the boxes individually, not just click on "
        		+ "the rows in question. ");
        instructionPanel.add(instruction);
        this.addNorth(instructionPanel, 4);
        
        this.add(new Label("Alignment interface to be implemented."));
	}
}
