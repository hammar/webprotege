package edu.stanford.bmir.protege.web.client.xd.specialization;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

import edu.stanford.bmir.protege.web.client.xd.DesignPatternDetailsPortlet;
import edu.stanford.bmir.protege.web.client.xd.specialization.panels.InstantiationMethodSelectionPanel;
import edu.stanford.bmir.protege.web.client.xd.specialization.panels.PreviewPanel;
import edu.stanford.bmir.protege.web.shared.xd.data.CodpInstantiationMethod;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpSpecialization;

public class DesignPatternInstantiationWizard extends PopupPanel {

	//private CardLayout wizardCardLayout;
	private Button wizardBackButton;
	private Button wizardNextButton;
	private Button wizardFinishButton;
	private Panel wizardPanel;
	private Panel visualizationPanel;
	private InstantiationMethodSelectionPanel instantiationMethodSelectionPanel;
	private PreviewPanel previewPanel;
	private CodpInstantiationMethod instantiationMethod;
	private ActiveWizardScreen activeWizardScreen;

	private enum ActiveWizardScreen {
        METHOD_SELECTION, ALIGNMENTS, PREVIEW;
    }
	
	public DesignPatternInstantiationWizard(DesignPatternDetailsPortlet parent) {
		super(false, true);
		
		// TODO: figure out how to display title in UI
		this.setTitle("CODP Instantiation Wizard");
		this.addStyleName("xdInstantiationWizard");
		
		// Default values
		this.instantiationMethod = CodpInstantiationMethod.TEMPLATE_BASED;
		
		// Outer tab panel containing ODP visualization and instantiation wizard
		TabLayoutPanel tabPanel = new TabLayoutPanel(3, Unit.EM);
		
		// Size of popup widget (i.e., tab panel) governs size of popup, so is set here.
		tabPanel.setHeight("480px");
		tabPanel.setWidth("640px");
		
		// Add tabs
		this.wizardPanel = makeWizardPanel();
		this.visualizationPanel = makeVisualizationPanel();
		tabPanel.add(wizardPanel, "CODP Instantiation");
		tabPanel.add(visualizationPanel, "CODP Visualisation");
		tabPanel.selectTab(wizardPanel);
		
		// Set popup widget to be the tab panel
		this.setWidget(tabPanel);
	}

	private DockLayoutPanel makeWizardPanel() {
		DockLayoutPanel wp = new DockLayoutPanel(Unit.EM);
		
		// Create the navigation bar of the wizard
		DockLayoutPanel navBar = new DockLayoutPanel(Unit.EM);
		navBar.addStyleName("xdWizardNavBar");
		this.wizardBackButton = new Button("Back");
		this.wizardNextButton = new Button("Next");
		this.wizardFinishButton = new Button("Finish");
		// TODO: implement button listeners that moves user between screens
		navBar.addWest(this.wizardBackButton, 5);
		navBar.addEast(this.wizardNextButton, 5);
		navBar.addEast(this.wizardFinishButton, 5);
		// Hide finish button (will be shown when user goes to last screen of wizard)
		this.wizardFinishButton.setVisible(false);
		wp.addSouth(navBar, 3);
		
		/*
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
        */
        
		// This is the main content field of the wizard interface
		FlowPanel wpContentPanel = new FlowPanel(); 
		wpContentPanel.addStyleName("xdWizardContentPanel");
		
        // These are the individual cards/screens of the wizard interface.
        this.instantiationMethodSelectionPanel = new InstantiationMethodSelectionPanel(this);
        this.previewPanel = new PreviewPanel(this);
        wpContentPanel.add(this.instantiationMethodSelectionPanel);
        //wpContentPanel.add(this.previewPanel);
        
        // Hide all but the first subpanel of the wizard
        this.previewPanel.setVisible(false);
        // Set flag of which screen is active to default first value
        this.activeWizardScreen = ActiveWizardScreen.METHOD_SELECTION;
        
        wp.add(wpContentPanel);
		return wp;
	}
	
	private Panel makeVisualizationPanel() {
		FlowPanel p = new FlowPanel();
		//p.setTitle("CODP Visualisation");
		p.add(new Label("Visualisation goes here"));
		return p;
	}
	
	protected void saveAndClose() {
		// TODO Auto-generated method stub
	}
	
	/*
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
	*/
	
	public OdpSpecialization getSpecialization() {
		// TODO Auto-generated method stub
		return null;
	}

	public void loadOdp(String uri) {
		//Window.alert("Asked to load: " + uri);
		// TODO Auto-generated method stub
	}

	public void setInstantiationMethod(CodpInstantiationMethod instantiationMethod) {
		this.instantiationMethod = instantiationMethod;
	}
}
