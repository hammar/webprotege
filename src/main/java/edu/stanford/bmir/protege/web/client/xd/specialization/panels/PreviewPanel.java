package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import com.google.gwt.user.client.ui.TextArea;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.RowLayout;

import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.xd.specialization.XdSpecializationWizard;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetSpecializationPreviewAction;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpSpecialization;
import edu.stanford.bmir.protege.web.shared.xd.results.GetSpecializationPreviewResult;

public class PreviewPanel extends Panel {
	
	private TextArea instantationAxiomsPreview;
	private final XdSpecializationWizard parentWizard;
	
	public PreviewPanel(XdSpecializationWizard parent) {
		super();
		
		this.parentWizard = parent;
		
        this.setLayout(new FitLayout());
        this.setBorder(false);  
        this.setId("card-4");
        this.setTitle("ODP Specialisation Preview");
        
        // Axioms list
        Panel instantiationAxiomsPanel = new Panel();
        instantiationAxiomsPanel.setLayout(new RowLayout());
        instantationAxiomsPreview = new TextArea();
        instantationAxiomsPreview.setEnabled(false);
        instantiationAxiomsPanel.add(instantationAxiomsPreview);
        
        // Toolbar with preview generation button 
        Toolbar previewButtonToolbar = new Toolbar();
        final ToolbarButton buildPreviewButton = new ToolbarButton("Generate preview");
        buildPreviewButton.addListener(new ButtonListenerAdapter() {
            public void onClick(Button button, EventObject e) {
            	
            	MessageBox.show(new MessageBoxConfig() {  
                    {  
                        setMsg("Generating preview, please wait...");  
                        setProgressText("Generating preview...");  
                        setWidth(300);  
                        setWait(true);  
                        setWaitConfig(new WaitConfig() {  
                            {  
                                setInterval(200);  
                            }  
                        });  
                        setAnimEl(buildPreviewButton.getId());  
                    }  
                }); 

            	// Get specialization object from parent wizard and send to server for preview generation
            	OdpSpecialization spec = parentWizard.getSpecialization();
                DispatchServiceManager.get().execute(new GetSpecializationPreviewAction(spec), 
                		new DispatchServiceCallback<GetSpecializationPreviewResult>() {
                	@Override
                    public void handleSuccess(GetSpecializationPreviewResult result) {
                		MessageBox.hide();
                		instantationAxiomsPreview.setText(result.getSpecializationPreview());
                    }
                });
            }
        });
        previewButtonToolbar.addButton(buildPreviewButton);
        instantiationAxiomsPanel.setTopToolbar(previewButtonToolbar);
        
        this.add(instantiationAxiomsPanel);
	}
	
	public void setInstantiationPreviewAxioms(String previewAxioms) {
		this.instantationAxiomsPreview.setValue(previewAxioms);
	}
}
