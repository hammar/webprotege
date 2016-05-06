package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;

import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.xd.specialization.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetSpecializationPreviewAction;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpSpecialization;
import edu.stanford.bmir.protege.web.shared.xd.results.GetSpecializationPreviewResult;

public class PreviewPanel extends FlowPanel {
	
	private TextArea instantationAxiomsPreview;
	private final DesignPatternInstantiationWizard parentWizard;
	
	public PreviewPanel(DesignPatternInstantiationWizard parent) {
		super();
		
		this.parentWizard = parent;
        this.setTitle("ODP Specialisation Preview");
        
        // Axioms list
        DockLayoutPanel instantiationAxiomsPanel = new DockLayoutPanel(Unit.EM);
        //instantiationAxiomsPanel.setLayout(new RowLayout());

        
        // Toolbar with preview generation button
        HorizontalPanel navBar = new HorizontalPanel();
        //Toolbar previewButtonToolbar = new Toolbar();
        final Button buildPreviewButton = new Button("Generate preview");
        buildPreviewButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent arg0) {
				// TODO: implement some sort of spinner here, that does not depend on PopupPanel
				// Get specialization object from parent wizard and send to server for preview generation
            	OdpSpecialization spec = parentWizard.getSpecialization();
                DispatchServiceManager.get().execute(new GetSpecializationPreviewAction(spec), 
                		new DispatchServiceCallback<GetSpecializationPreviewResult>() {
                			@Override
                			public void handleSuccess(GetSpecializationPreviewResult result) {
                				instantationAxiomsPreview.setText(result.getSpecializationPreview());
                			}
                		});
			}
        	
        });

        navBar.add(buildPreviewButton);
        instantiationAxiomsPanel.addNorth(navBar, 3);
        
        instantationAxiomsPreview = new TextArea();
        instantationAxiomsPreview.setEnabled(false);
        instantiationAxiomsPanel.add(instantationAxiomsPreview);
        
        this.add(instantiationAxiomsPanel);
	}
}
