package edu.stanford.bmir.protege.web.client.xd.instantiation.panels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.xd.instantiation.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetInstantiationPreviewAction;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpInstantiation;
import edu.stanford.bmir.protege.web.shared.xd.results.GetInstantiationPreviewResult;

public class PreviewPanel extends VerticalPanel implements InstantiationWizardPanel {
	
	private TextArea instantationAxiomsPreview;
	private final DesignPatternInstantiationWizard parentWizard;
	
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
				// TODO: implement some sort of spinner here, that does not depend on PopupPanel
				// Get specialization object from parent wizard and send to server for preview generation
            	OdpInstantiation spec = parentWizard.getInstantiation();
                DispatchServiceManager.get().execute(new GetInstantiationPreviewAction(spec), 
                		new DispatchServiceCallback<GetInstantiationPreviewResult>() {
                			@Override
                			public void handleSuccess(GetInstantiationPreviewResult result) {
                				instantationAxiomsPreview.setText(result.getInstantiationPreview());
                				// TODO: kill spinner UI here
                			}
                		});
			}
        });
        navBar.add(buildPreviewButton);
        
        this.add(navBar);
        
        VerticalPanel innerVp = new VerticalPanel();
		innerVp.setSpacing(10);
        instantationAxiomsPreview = new TextArea();
        instantationAxiomsPreview.setEnabled(false);
        innerVp.add(instantationAxiomsPreview);
        
        this.add(innerVp);
	}

	@Override
	public void renderPanel() {
		// TODO Auto-generated method stub
	}
}
