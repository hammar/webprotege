package edu.stanford.bmir.protege.web.client.xd.specialization;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.CardLayout;
import com.gwtext.client.widgets.layout.FitLayout;

import edu.stanford.bmir.protege.web.client.xd.DesignPatternDetailsPortlet;
import edu.stanford.bmir.protege.web.client.xd.specialization.panels.InstantiationMethodSelectionPanel;
import edu.stanford.bmir.protege.web.client.xd.specialization.panels.PreviewPanel;
import edu.stanford.bmir.protege.web.shared.xd.data.CodpInstantiationMethod;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpSpecialization;

public class DesignPatternInstantiationWizard extends com.gwtext.client.widgets.Window {

	private CardLayout wizardCardLayout;
	private ToolbarButton wizardBackButton;
	private ToolbarButton wizardNextButton;
	private ToolbarButton wizardFinishButton;
	private Panel wizardPanel;
	private Panel visualizationPanel;
	private InstantiationMethodSelectionPanel instantiationMethodSelectionPanel;
	private PreviewPanel previewPanel;
	private CodpInstantiationMethod instantiationMethod;

	public DesignPatternInstantiationWizard(DesignPatternDetailsPortlet parent) {
		this.setCloseAction(HIDE);
		
		this.setTitle("CODP Instantiation Wizard");
		this.setWidth(640);
		this.setHeight(480);
		this.setResizable(false);
		this.setLayout(new FitLayout()); 
		
		// Default values
		this.instantiationMethod = CodpInstantiationMethod.TEMPLATE_BASED;
		
		TabPanel tabPanel = new TabPanel();  
        tabPanel.setTabPosition(Position.TOP);  
        tabPanel.setResizeTabs(false);    
        tabPanel.setTabWidth(135);  
        tabPanel.setActiveTab(0); 
		
		this.wizardPanel = makeWizardPanel();
		this.visualizationPanel = makeVisualizationPanel();
		
		tabPanel.add(wizardPanel);
		tabPanel.add(visualizationPanel);
		this.add(tabPanel);
	}

	private Panel makeWizardPanel() {
		Panel wp = new Panel();
		
		this.wizardCardLayout = new CardLayout();
		wp.setLayout(wizardCardLayout);
		wp.setTitle("CODP Instantiation");
		wp.setActiveItem(0);
		
		Toolbar navigationBar = new Toolbar();  
        
        this.wizardBackButton = new ToolbarButton("Back", makeBackButtonListener());  
        this.wizardBackButton.setId("move-prev");  
        navigationBar.addButton(wizardBackButton);  
        navigationBar.addFill();  
  
        this.wizardNextButton = new ToolbarButton("Next", makeForwardButtonListener());  
        this.wizardNextButton.setId("move-next");  
        navigationBar.addButton(wizardNextButton);
        
        this.wizardFinishButton = new ToolbarButton("Finish", new ButtonListenerAdapter(){
        	public void onClick(Button button, EventObject e) {
        		saveAndClose();
        	}
        });
        wizardFinishButton.setId("move-finish");  
        navigationBar.addButton(wizardFinishButton);
        wizardFinishButton.setVisible(false);
        wp.setBottomToolbar(navigationBar);
        
        // These are the individual cards/screens of the wizard interface.
        // The order in which they are added is important, as is the ID that 
        // they are assigned in their respective constructor.
        this.instantiationMethodSelectionPanel = new InstantiationMethodSelectionPanel(this);
        this.previewPanel = new PreviewPanel(this);
        
        wp.add(instantiationMethodSelectionPanel);
        wp.add(previewPanel);
        
		return wp;
	}
	
	private Panel makeVisualizationPanel() {
		Panel p = new Panel();
		p.setTitle("CODP Visualisation");
		p.add(new Label("Visualisation goes here"));
		return p;
	}
	
	protected void saveAndClose() {
		// TODO Auto-generated method stub
	}

	private ButtonListenerAdapter makeBackButtonListener() {
		return new ButtonListenerAdapter() {  
            public void onClick(Button button, EventObject e) {      
                String panelID = wizardCardLayout.getActiveItem().getId();  
  
                switch (panelID) {
                case "card-1":
                	wizardCardLayout.setActiveItem(0);
                	break;
                	
                case "card-2":
                	wizardCardLayout.setActiveItem(1);
                	break;
                	
                case "card-3":
                	wizardCardLayout.setActiveItem(2);
                	break;
                	
                case "card-4":
                	wizardCardLayout.setActiveItem(3);
                	wizardFinishButton.setVisible(false);
                    wizardNextButton.setVisible(true);
                	break;
                } 
            }
        };
	}

	private ButtonListenerAdapter makeForwardButtonListener() {
		return new ButtonListenerAdapter() {  
            public void onClick(Button button, EventObject e) {  
                String panelID = wizardCardLayout.getActiveItem().getId();  
  
                switch (panelID) {
                case "card-0":
                	wizardCardLayout.setActiveItem(1);
                	break;
                	
                case "card-1":
                	wizardCardLayout.setActiveItem(2);
                	break;
                	
                case "card-2":
                	wizardCardLayout.setActiveItem(3);
                	break;
                	
                case "card-3": 
                	wizardCardLayout.setActiveItem(4);
                	wizardFinishButton.setVisible(true);
                    wizardNextButton.setVisible(false);
                	break;
                }
            }
        };
	}

	public OdpSpecialization getSpecialization() {
		// TODO Auto-generated method stub
		return null;
	}

	public void loadOdp(String uri) {
		Window.alert("Asked to load: " + uri);
		// TODO Auto-generated method stub
	}

	public void setInstantiationMethod(CodpInstantiationMethod instantiationMethod) {
		this.instantiationMethod = instantiationMethod;
	}
}
