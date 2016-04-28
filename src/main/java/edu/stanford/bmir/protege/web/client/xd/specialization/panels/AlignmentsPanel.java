package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.layout.RowLayout;
import com.gwtext.client.widgets.layout.RowLayoutData;

import edu.stanford.bmir.protege.web.client.xd.specialization.DesignPatternInstantiationWizard;

public class AlignmentsPanel extends Panel {
	
	private DesignPatternInstantiationWizard parentWizard;
	
	public AlignmentsPanel(DesignPatternInstantiationWizard parent) {
		super();
		
		this.parentWizard = parent;
		
        this.setBorder(false);  
        this.setId("card-3");
        this.setTitle("CODP Specialisation Alignment");
        this.setLayout(new RowLayout());
        
        Panel instructionPanel = new Panel();
        instructionPanel.setPaddings(5);
        Label instruction = new Label("From the suggested entity alignments listed below, constructed based on your specialised "
        		+ "entities and the existing ontology entities, please select the ones that hold within your model. Please note "
        		+ "that for multiple simultanous selection to work you must check the boxes individually, not just click on "
        		+ "the rows in question. ");
        instructionPanel.add(instruction);
        this.add(instructionPanel, new RowLayoutData(53));
        
        
        this.add(new Label("Alignment interface to be implemented."));
	}
}
