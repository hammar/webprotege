package edu.stanford.bmir.protege.web.client.xd.specialization;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

import edu.stanford.bmir.protege.web.client.xd.DesignPatternDetailsPortlet;
import edu.stanford.bmir.protege.web.client.xd.specialization.panels.AlignmentsPanel;
import edu.stanford.bmir.protege.web.client.xd.specialization.panels.EntityCloningPanel;
import edu.stanford.bmir.protege.web.client.xd.specialization.panels.EntitySpecializationPanel;
import edu.stanford.bmir.protege.web.client.xd.specialization.panels.InstantiationMethodSelectionPanel;
import edu.stanford.bmir.protege.web.client.xd.specialization.panels.PreviewPanel;
import edu.stanford.bmir.protege.web.client.xd.specialization.panels.RestrictionsPanel;
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
	private EntityCloningPanel entityCloningPanel;
	private EntitySpecializationPanel entitySpecializationPanel;
	private RestrictionsPanel restrictionsPanel;
	private AlignmentsPanel alignmentsPanel;
	private PreviewPanel previewPanel;
	private CodpInstantiationMethod instantiationMethod;
	private ActiveWizardScreen activeWizardScreen;

	private enum ActiveWizardScreen {
        METHOD_SELECTION, ENTITY_CLONING, ENTITY_SPECIALIZATION, RESTRICTIONS, ALIGNMENTS, PREVIEW;
    }
	
	public DesignPatternInstantiationWizard(DesignPatternDetailsPortlet parent) {
		super(false, true);
		this.addStyleName("xdInstantiationWizard");
		
		DockLayoutPanel wizardFramePanel = new DockLayoutPanel(Unit.EM);
		// Size of popup widget (i.e., framing panel) governs size of popup, so is set here.
		wizardFramePanel.setHeight("480px");
		wizardFramePanel.setWidth("640px");
		
		// Header holding wizard title and close button
		DockLayoutPanel wizardHeaderPanel = new DockLayoutPanel(Unit.EM);
		wizardHeaderPanel.addStyleName("xdWizardHeader");
		Button closeWizardButton = new Button("X");
		closeWizardButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				hide();
			}
		});
		wizardHeaderPanel.addEast(closeWizardButton, 2);
		wizardHeaderPanel.add(new Label("CODP Instantiation Wizard"));
		wizardFramePanel.addNorth(wizardHeaderPanel, 2);
		
		// Default values
		this.instantiationMethod = CodpInstantiationMethod.TEMPLATE_BASED;
		
		// Outer tab panel containing ODP visualization and instantiation wizard
		TabLayoutPanel tabPanel = new TabLayoutPanel(3, Unit.EM);
		
		// Add tabs
		this.wizardPanel = makeWizardPanel();
		this.visualizationPanel = makeVisualizationPanel();
		tabPanel.add(wizardPanel, "CODP Instantiation");
		tabPanel.add(visualizationPanel, "CODP Visualisation");
		tabPanel.selectTab(wizardPanel);
		
		// Add tab panel to main body of outer frame panel
		wizardFramePanel.add(tabPanel);
		
		// Set popup widget to be the tab panel
		this.setWidget(wizardFramePanel);
	}

	private DockLayoutPanel makeWizardPanel() {
		DockLayoutPanel wp = new DockLayoutPanel(Unit.EM);
		
		// Create the navigation bar of the wizard
		DockLayoutPanel navBar = new DockLayoutPanel(Unit.EM);
		navBar.addStyleName("xdWizardNavBar");
		this.wizardBackButton = new Button("Back");
		this.wizardBackButton.addClickHandler(makeBackButtonClickHandler());
		this.wizardNextButton = new Button("Next");
		this.wizardNextButton.addClickHandler(makeNextButtonClickHandler());
		this.wizardFinishButton = new Button("Finish");
		this.wizardFinishButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				saveAndClose();
			}
		});
		navBar.addWest(this.wizardBackButton, 5);
		navBar.addEast(this.wizardNextButton, 5);
		navBar.addEast(this.wizardFinishButton, 5);
		// Set initial button state
		this.wizardBackButton.setEnabled(false);
		this.wizardFinishButton.setVisible(false);
		wp.addSouth(navBar, 3);
        
		// This is the main content field of the wizard interface
		FlowPanel wpContentPanel = new FlowPanel(); 
		wpContentPanel.addStyleName("xdWizardContentPanel");
		
        // These are the individual cards/screens of the wizard interface.
        this.instantiationMethodSelectionPanel = new InstantiationMethodSelectionPanel(this);
        this.entityCloningPanel = new EntityCloningPanel(this);
        this.entitySpecializationPanel = new EntitySpecializationPanel(this);
        this.restrictionsPanel = new RestrictionsPanel(this);
        this.alignmentsPanel = new AlignmentsPanel(this);
        this.previewPanel = new PreviewPanel(this);
        wpContentPanel.add(this.instantiationMethodSelectionPanel);
        wpContentPanel.add(this.entityCloningPanel);
        wpContentPanel.add(this.entitySpecializationPanel);
        wpContentPanel.add(this.restrictionsPanel);
        wpContentPanel.add(this.alignmentsPanel);
        wpContentPanel.add(this.previewPanel);
        
        // Set initial wizard state
        this.entityCloningPanel.setVisible(false);
        this.entitySpecializationPanel.setVisible(false);
        this.restrictionsPanel.setVisible(false);
        this.alignmentsPanel.setVisible(false);
        this.previewPanel.setVisible(false);
        this.activeWizardScreen = ActiveWizardScreen.METHOD_SELECTION;
        
        wp.add(wpContentPanel);
		return wp;
	}
	
	private Panel makeVisualizationPanel() {
		FlowPanel p = new FlowPanel();
		p.add(new Label("Visualisation goes here"));
		return p;
	}
	
	protected void saveAndClose() {
		// TODO Auto-generated method stub
	}
	
	private ClickHandler makeBackButtonClickHandler() {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				switch (activeWizardScreen) {
				case METHOD_SELECTION:
					// This should not be able to occur
					break;

				case ENTITY_CLONING:
					// MOVING ENTITY_CLONING -> METHOD_SELECTION
					// Set outgoing panel visibility state
					entityCloningPanel.setVisible(false);
					// Set button state
					wizardBackButton.setEnabled(false);
					// Show incoming panel and set statekeeping enum
					instantiationMethodSelectionPanel.setVisible(true);
					activeWizardScreen = ActiveWizardScreen.METHOD_SELECTION;
					break;
					
				case ENTITY_SPECIALIZATION:
					// MOVING ENTITY_SPECIALIZATION -> METHOD_SELECTION
					// Set outgoing panel visibility state
					entitySpecializationPanel.setVisible(false);
					// Set button state
					wizardBackButton.setEnabled(false);
					// Show incoming panel and set statekeeping enum
					instantiationMethodSelectionPanel.setVisible(true);
					activeWizardScreen = ActiveWizardScreen.METHOD_SELECTION;
					break;
					
				case RESTRICTIONS:
					// MOVING RESTRICTIONS -> ENTITY_CLONING or ENTITY_SPECIALIZATION
					// Set outgoing panel visibility state
					restrictionsPanel.setVisible(false);					
					// Figure out which method is being used and which panel should be displayed
					switch (instantiationMethod) {
					case TEMPLATE_BASED:
						// Show incoming panel and set statekeeping enum
						entityCloningPanel.setVisible(true);
						activeWizardScreen = ActiveWizardScreen.ENTITY_CLONING;
						break;
					case IMPORT_BASED:
						// Show incoming panel and set statekeeping enum
						entitySpecializationPanel.setVisible(true);
						activeWizardScreen = ActiveWizardScreen.ENTITY_SPECIALIZATION;
						break;
					}
					break;
					
				case ALIGNMENTS:
					//Moving ALIGNMENTS->RESTRICTIONS
					// Set outgoing panel visibility state
					alignmentsPanel.setVisible(false);
					// Show incoming panel and set statekeeping enum
					restrictionsPanel.setVisible(true);
					activeWizardScreen = ActiveWizardScreen.RESTRICTIONS;
					break;
				
				case PREVIEW:
					// Moving PREVIEW->ALIGNMENTS
					// Set outgoing panel visibility state
					previewPanel.setVisible(false);
					// Set button state
					wizardFinishButton.setVisible(false);
					wizardNextButton.setVisible(true);
					// Show incoming panel and set statekeeping enum
					alignmentsPanel.setVisible(true);
					activeWizardScreen = ActiveWizardScreen.ALIGNMENTS;
					break;

				default:
					break;
				}
			}
		};
	}
	
	private ClickHandler makeNextButtonClickHandler() {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				switch (activeWizardScreen) {
				case METHOD_SELECTION:
					// Moving METHOD_SELECTION -> ENTITY_CLONING or ENTITY_SPECIALIZATION
					// Set outgoing panel visibility state
					instantiationMethodSelectionPanel.setVisible(false);
					// Set button state
					wizardBackButton.setEnabled(true);					
					// Figure out which method is being used and which panel should be displayed
					switch (instantiationMethod) {
					case TEMPLATE_BASED:
						// Show panel and set statekeeping enum
						entityCloningPanel.setVisible(true);
						activeWizardScreen = ActiveWizardScreen.ENTITY_CLONING;
						break;
					case IMPORT_BASED:
						// Show incoming panel and set statekeeping enum
						entitySpecializationPanel.setVisible(true);
						activeWizardScreen = ActiveWizardScreen.ENTITY_SPECIALIZATION;
						break;
					}
					break;
					
				case ENTITY_CLONING:
					// MOVING ENTITY_CLONING -> RESTRICTIONS
					// Set outgoing panel visibility state
					entityCloningPanel.setVisible(false);
					// Show incoming panel and set statekeeping enum
					restrictionsPanel.setVisible(true);
					activeWizardScreen = ActiveWizardScreen.RESTRICTIONS;
					break;
					
				case ENTITY_SPECIALIZATION:
					// MOVING ENTITY_SPECIALIZATION -> RESTRICTIONS
					// Set outgoing panel visibility state
					entitySpecializationPanel.setVisible(false);
					// Show incoming panel and set statekeeping enum
					restrictionsPanel.setVisible(true);
					activeWizardScreen = ActiveWizardScreen.RESTRICTIONS;
					break;
				
				case RESTRICTIONS:
					// MOVING RESTRICTIONS -> ALIGNMENTS
					// Set outgoing panel visibility state
					restrictionsPanel.setVisible(false);
					// Show incoming panel and set statekeeping enum
					alignmentsPanel.setVisible(true);
					activeWizardScreen = ActiveWizardScreen.ALIGNMENTS;
					break;
					
				case ALIGNMENTS:
					// Moving ALIGNMENTS->PREVIEW
					// Set outgoing panel visibility state
					alignmentsPanel.setVisible(false);
					// Set button state
					wizardNextButton.setVisible(false);
					wizardFinishButton.setVisible(true);
					// Show incoming panel and set statekeeping enum
					previewPanel.setVisible(true);
					activeWizardScreen = ActiveWizardScreen.PREVIEW;
					break;
					
				case PREVIEW:
					// This should not be able to occur
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
		//Window.alert("Asked to load: " + uri);
		// TODO Auto-generated method stub
	}

	public void setInstantiationMethod(CodpInstantiationMethod instantiationMethod) {
		this.instantiationMethod = instantiationMethod;
	}
}
