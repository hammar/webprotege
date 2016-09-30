package edu.stanford.bmir.protege.web.client.xd.instantiation.panels;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.xd.instantiation.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetInstantiationPreviewAction;
import edu.stanford.bmir.protege.web.shared.xd.data.CodpInstantiation;
import edu.stanford.bmir.protege.web.shared.xd.results.GetInstantiationPreviewResult;

public class PreviewPanel extends VerticalPanel implements InstantiationWizardPanel {
	
	private TabLayoutPanel tabPanel;
	private TextArea instantationAxiomsPreview;
	private final DesignPatternInstantiationWizard parentWizard;
	private VerticalPanel axiomPreviewPanel;
	private VisualisationPanel vowlPreviewPanel;
	
	public PreviewPanel(DesignPatternInstantiationWizard parent) {
		super();
		this.parentWizard = parent;
		this.addStyleName("xdpPreviewPanel");
        this.setTitle("ODP Specialisation Preview");

        // Toolbar with preview generation button
        HorizontalPanel navBar = new HorizontalPanel();
        navBar.addStyleName("xdpPreviewPanelNavBar");
        final Button buildPreviewButton = new Button("Generate preview");
        buildPreviewButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent arg0) {
				// Initiate spinner
				parentWizard.showSpinner("Loading preview...");
				// Get specialization object from parent wizard and send to server for preview generation
            	CodpInstantiation spec = parentWizard.getInstantiation();
                DispatchServiceManager.get().execute(new GetInstantiationPreviewAction(spec), 
                		new DispatchServiceCallback<GetInstantiationPreviewResult>() {
                			@Override
                			public void handleSuccess(GetInstantiationPreviewResult result) {
                				instantationAxiomsPreview.setText(result.getInstantiationAxioms());
                				vowlPreviewPanel.renderPanel(result.getInstantiationAsJson());
                				// Kill the spinner
                				parentWizard.hideSpinner();
                			}
                		});
			}
        });
        navBar.add(buildPreviewButton);    
        this.add(navBar);
        
        tabPanel = new TabLayoutPanel(3, Unit.EM);
        tabPanel.addStyleName("xdpPreviewPanelTabPanel");
        tabPanel.setHeight("400px");
        
        axiomPreviewPanel = new VerticalPanel();
        axiomPreviewPanel.setSpacing(10);
        instantationAxiomsPreview = new TextArea();
        instantationAxiomsPreview.setEnabled(false);
        axiomPreviewPanel.add(instantationAxiomsPreview);
        tabPanel.add(axiomPreviewPanel, "Axiom Preview");
        
        vowlPreviewPanel = new VisualisationPanel(parentWizard);
        tabPanel.add(vowlPreviewPanel, "VOWL Preview");
        
        tabPanel.selectTab(axiomPreviewPanel);
        this.add(tabPanel);
	}

	@Override
	public void renderPanel() {
		this.tabPanel.selectTab(this.axiomPreviewPanel);
		this.instantationAxiomsPreview.setText("");
	}
}
