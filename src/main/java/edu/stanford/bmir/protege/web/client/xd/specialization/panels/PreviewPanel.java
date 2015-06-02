package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.layout.RowLayout;

public class PreviewPanel extends Panel {
	
	private Panel visualizationPanel;
	private TextArea instantationAxiomsPreview;
	
	public PreviewPanel() {
		super();
		
        this.setLayout(new RowLayout());
        this.setBorder(false);  
        this.setId("card-4");
        this.setTitle("Specialisation Overview");
        
        // Visualization stuff
        visualizationPanel = new Panel();
        visualizationPanel.setTitle("Visualization");
        HTML circle = new HTML("<div style=\"font-size: 12px; line-height:50px; width: 100px; height: 50px; margin: 50px 20px; border-radius:50%; text-align:center; background: orange;\">My Class Name</div>");
        visualizationPanel.add(circle);
        this.add(visualizationPanel);
        
        // Axioms list
        Panel instantiationAxiomsPanel = new Panel();
        instantiationAxiomsPanel.setTitle("ODP Instantiation Axioms");
        instantiationAxiomsPanel.setLayout(new RowLayout());
        instantationAxiomsPreview = new TextArea();
        instantationAxiomsPreview.setEnabled(false);
        // TODO: add a button? make this call the remote preview service
        instantationAxiomsPreview.setText("OY");
        instantiationAxiomsPanel.add(instantationAxiomsPreview);
        this.add(instantiationAxiomsPanel);
	}
	
	public void setInstantiationPreviewAxioms(String previewAxioms) {
		this.instantationAxiomsPreview.setValue(previewAxioms);
	}
}
