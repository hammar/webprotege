package edu.stanford.bmir.protege.web.client.xd.instantiation;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

import com.google.common.base.Optional;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.xd.DesignPatternDetailsPortlet;
import edu.stanford.bmir.protege.web.client.xd.instantiation.old.restriction.Restriction;
import edu.stanford.bmir.protege.web.client.xd.instantiation.panels.AlignmentsPanel;
import edu.stanford.bmir.protege.web.client.xd.instantiation.panels.EntityCloningPanel;
import edu.stanford.bmir.protege.web.client.xd.instantiation.panels.EntitySpecializationPanel;
import edu.stanford.bmir.protege.web.client.xd.instantiation.panels.InstantiationMethodSelectionPanel;
import edu.stanford.bmir.protege.web.client.xd.instantiation.panels.PreviewPanel;
import edu.stanford.bmir.protege.web.client.xd.instantiation.panels.RestrictionsPanel;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetOdpContentsAction;
import edu.stanford.bmir.protege.web.shared.xd.actions.PersistInstantiationAction;
import edu.stanford.bmir.protege.web.shared.xd.data.CodpInstantiationMethod;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.CodpInstantiation;
import edu.stanford.bmir.protege.web.shared.xd.data.CodpSpecializationStrategy;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpContentsResult;
import edu.stanford.bmir.protege.web.shared.xd.results.PersistInstantiationResult;
import edu.stanford.bmir.protege.web.shared.xd.util.TreeMethods;

public class DesignPatternInstantiationWizard extends PopupPanel {

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
	private Date alignmentsModificationTimestamp = new Date();
	private Date restrictionsModificationTimestamp = new Date();
	private Date instantiationModificationTimestamp = new Date();
	private Date instantiationMethodSelectionTimestamp = new Date();	
	private Spinner spinner = new Spinner();

	private IRI odpIri;
	private FrameTreeNode<OntologyEntityFrame> classTree;
	private FrameTreeNode<OntologyEntityFrame> objectPropertyTree;
	private FrameTreeNode<OntologyEntityFrame> dataPropertyTree;

	private Set<Restriction> restrictions = new HashSet<Restriction>();
	private Set<Alignment> alignments = new HashSet<Alignment>();
	private ProjectId projectId;

	private enum ActiveWizardScreen {
        METHOD_SELECTION, ENTITY_CLONING, ENTITY_SPECIALIZATION, RESTRICTIONS, ALIGNMENTS, PREVIEW;
    }
	
	@SuppressWarnings("deprecation")
	public DesignPatternInstantiationWizard(DesignPatternDetailsPortlet parent) {
		super(false, true);
		
		this.projectId = parent.getProjectId();
		
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
		this.classTree = new FrameTreeNode<OntologyEntityFrame>(new ClassFrame("nil"));
		this.objectPropertyTree = new FrameTreeNode<OntologyEntityFrame>(new ObjectPropertyFrame("nil"));
		this.dataPropertyTree = new FrameTreeNode<OntologyEntityFrame>(new DataPropertyFrame("nil"));
		
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
	
	public void showSpinner(String text) {
		this.spinner.setText(text);
		this.spinner.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
				public void setPosition(int offsetWidth, int offsetHeight) {
	                int left = (Window.getClientWidth() - offsetWidth) / 2;
	                int top = (Window.getClientHeight() - offsetHeight) / 2;
	                spinner.setPopupPosition(left, top);
				}
			});
	}
	
	public void hideSpinner() {
		this.spinner.hide();
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
		this.wizardFinishButton.setEnabled(false);
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
        this.instantiationMethodSelectionPanel.setVisible(true);
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
		PersistInstantiationAction pia = new PersistInstantiationAction(this.getInstantiation());
		this.showSpinner("Persisting CODP Instantiation...");
		
		DispatchServiceManager.get().execute(pia, new DispatchServiceCallback<PersistInstantiationResult>() {
        	@Override
            public void handleSuccess(PersistInstantiationResult result) {
        		// Kill spinner and hide self
        		hideSpinner();
        		hide();
            }
        });
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
					// Moving ENTITY_CLONING -> METHOD_SELECTION
					// Set outgoing panel visibility state
					entityCloningPanel.setVisible(false);
					// Set button state
					wizardBackButton.setEnabled(false);
					// Show incoming panel and set statekeeping enum
					instantiationMethodSelectionPanel.setVisible(true);
					activeWizardScreen = ActiveWizardScreen.METHOD_SELECTION;
					break;
					
				case ENTITY_SPECIALIZATION:
					// Moving ENTITY_SPECIALIZATION -> METHOD_SELECTION
					// Set outgoing panel visibility state
					entitySpecializationPanel.setVisible(false);
					// Set button state
					wizardBackButton.setEnabled(false);
					// Show incoming panel and set statekeeping enum
					instantiationMethodSelectionPanel.setVisible(true);
					activeWizardScreen = ActiveWizardScreen.METHOD_SELECTION;
					break;
					
				case RESTRICTIONS:
					// Moving RESTRICTIONS -> ENTITY_SPECIALIZATION
					// Set outgoing panel visibility state
					restrictionsPanel.setVisible(false);					
					// Show incoming panel and set statekeeping enum
					entitySpecializationPanel.setVisible(true);
					activeWizardScreen = ActiveWizardScreen.ENTITY_SPECIALIZATION;
					break;
					
				case ALIGNMENTS:
					// Moving ALIGNMENTS-> ENTITY_CLONING or RESTRICTIONS
					// Set outgoing panel visibility state
					alignmentsPanel.setVisible(false);
					// Figure out which method is being used and which panel should be displayed
					switch (instantiationMethod) {
					case TEMPLATE_BASED:
						// Show incoming panel and set statekeeping enum
						entityCloningPanel.setVisible(true);
						activeWizardScreen = ActiveWizardScreen.ENTITY_CLONING;
						break;
					case IMPORT_BASED:
						// Show incoming panel and set statekeeping enum
						restrictionsPanel.setVisible(true);
						activeWizardScreen = ActiveWizardScreen.RESTRICTIONS;
						break;
					}
					break;
				
				case PREVIEW:
					// Moving PREVIEW->ALIGNMENTS
					// Set outgoing panel visibility state
					previewPanel.setVisible(false);
					// Set button state
					wizardFinishButton.setEnabled(false);
					wizardNextButton.setEnabled(true);
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
						if (!isInstantiationUpToDateWithMethod()) {
							resetInstantiation();
							entityCloningPanel.renderPanel();
						}
						
						// Show panel and set statekeeping enum
						entityCloningPanel.setVisible(true);
						activeWizardScreen = ActiveWizardScreen.ENTITY_CLONING;
						break;
					case IMPORT_BASED:
						if (!isInstantiationUpToDateWithMethod()) {
							resetInstantiation();
							entitySpecializationPanel.renderPanel();
						}
						
						// Show incoming panel and set statekeeping enum
						entitySpecializationPanel.setVisible(true);
						activeWizardScreen = ActiveWizardScreen.ENTITY_SPECIALIZATION;
						break;
					}
					break;
					
				case ENTITY_CLONING:
					// MOVING ENTITY_CLONING -> ALIGNMENTS
					// Set outgoing panel visibility state
					entityCloningPanel.setVisible(false);
					if (!areAlignmentsUpToDate()) {
						alignments.clear();
						alignmentsPanel.renderPanel();
					}
					// Show incoming panel and set statekeeping enum
					alignmentsPanel.setVisible(true);
					activeWizardScreen = ActiveWizardScreen.ALIGNMENTS;
					break;
					
				case ENTITY_SPECIALIZATION:
					// MOVING ENTITY_SPECIALIZATION -> RESTRICTIONS
					// Set outgoing panel visibility state
					entitySpecializationPanel.setVisible(false);
					
					if (!areRestrictionsUpToDate()) {
						restrictions.clear();
						restrictionsPanel.renderPanel();
					}
					
					// Show incoming panel and set statekeeping enum
					restrictionsPanel.setVisible(true);
					activeWizardScreen = ActiveWizardScreen.RESTRICTIONS;
					break;
				
				case RESTRICTIONS:
					// MOVING RESTRICTIONS -> ALIGNMENTS
					// Set outgoing panel visibility state
					restrictionsPanel.setVisible(false);
					if (!areAlignmentsUpToDate()) {
						alignments.clear();
						alignmentsPanel.renderPanel();
					}
					// Show incoming panel and set statekeeping enum
					alignmentsPanel.setVisible(true);
					activeWizardScreen = ActiveWizardScreen.ALIGNMENTS;
					break;
					
				case ALIGNMENTS:
					// Moving ALIGNMENTS->PREVIEW
					// Set outgoing panel visibility state
					alignmentsPanel.setVisible(false);
					// Set button state
					wizardNextButton.setEnabled(false);
					wizardFinishButton.setEnabled(true);
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

	private boolean areRestrictionsUpToDate() {
		if (this.restrictionsModificationTimestamp.after(this.instantiationModificationTimestamp)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public ProjectId getProjectId() {
		return projectId;
	}
	
	public void addSpecializedFrame(OntologyEntityFrame parentFrame, OntologyEntityFrame newFrame) {
		FrameTreeNode<OntologyEntityFrame> treeRoot;
		if (parentFrame instanceof ClassFrame) {
			treeRoot = this.classTree;
		}
		else if (parentFrame instanceof ObjectPropertyFrame) {
			treeRoot = this.objectPropertyTree;
		}
		else {
			treeRoot = this.dataPropertyTree;
		}
		Optional<FrameTreeNode<OntologyEntityFrame>> parentTreeOpt = TreeMethods.getFrameTreeForFrame(treeRoot, parentFrame);
		if (parentTreeOpt.isPresent()) {
			FrameTreeNode<OntologyEntityFrame> parentTree = parentTreeOpt.get();
			parentTree.addChild(newFrame);
			this.updateInstantiationModificationTimestamp();
		}
	}
	
	public void removeSpecializedFrame(OntologyEntityFrame frameToRemove) {
		FrameTreeNode<OntologyEntityFrame> treeRoot;
		if (frameToRemove instanceof ClassFrame) {
			treeRoot = this.classTree;
		}
		else if (frameToRemove instanceof ObjectPropertyFrame) {
			treeRoot = this.objectPropertyTree;
		}
		else {
			treeRoot = this.dataPropertyTree;
		}
		Optional<FrameTreeNode<OntologyEntityFrame>> frameTreeOpt = TreeMethods.getFrameTreeForFrame(treeRoot, frameToRemove);
		if (frameTreeOpt.isPresent()) {
			FrameTreeNode<OntologyEntityFrame> treeToRemove = frameTreeOpt.get();
			FrameTreeNode<OntologyEntityFrame> parentTree = treeToRemove.getParent();
			parentTree.getChildren().remove(treeToRemove);
		}
		this.updateInstantiationModificationTimestamp();
	}
	
	public void addAlignment(Alignment a) {
		this.alignments.add(a);
		this.updateAlignmentModificationTimestamp();
	}
	
	public void removeAlignment(Alignment a) {
		this.alignments.remove(a);
		this.updateAlignmentModificationTimestamp();
	}
	
	public CodpInstantiation getInstantiation() {
		// TODO: Update CodpInstantiation class to also carry restrictions
		// TODO: Possibly remove strategy from CodpInstantiation class
		return new CodpInstantiation(this.projectId, this.odpIri, this.classTree, this.objectPropertyTree, this.dataPropertyTree, this.alignments, this.instantiationMethod, CodpSpecializationStrategy.PROPERTY_ORIENTED);
	}
	
	public void updateInstantiationMethodSelectionTimestamp() {
		this.instantiationMethodSelectionTimestamp = new Date();
	}
	
	public void updateInstantiationModificationTimestamp() {
		this.instantiationModificationTimestamp = new Date();
	}
	
	private void updateAlignmentModificationTimestamp() {
		this.alignmentsModificationTimestamp = new Date();
	}
	
	private void updateRestrictionsModificationTimestamp() {
		this.restrictionsModificationTimestamp = new Date();
	}
	
	private Boolean areAlignmentsUpToDate() {
		if (this.alignmentsModificationTimestamp.after(this.instantiationModificationTimestamp)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private Boolean isInstantiationUpToDateWithMethod() {
		if (this.instantiationModificationTimestamp.after(this.instantiationMethodSelectionTimestamp)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private void resetFrameTreeNode(FrameTreeNode<OntologyEntityFrame> inputTree) {
		for (FrameTreeNode<OntologyEntityFrame> childTree: inputTree.getChildren()) {
			resetFrameTreeNode(childTree);
		}
		for (FrameTreeNode<OntologyEntityFrame> childTree: inputTree.getChildren()) {
			if (!childTree.getData().getIri().isPresent()) {
				inputTree.getChildren().remove(childTree);
			}
		}
		inputTree.getData().removeClonedLabel();
	}
	
	private void resetInstantiation() {
		resetFrameTreeNode(this.classTree);
		resetFrameTreeNode(this.objectPropertyTree);
		resetFrameTreeNode(this.dataPropertyTree);
	}

	public void loadOdp(String uri) {
		
		// Set default visibility of child panels and reset statekeeping enum
		// This is prior to spinner UI activating as it is instantaneous, and we
		// don't want to show old data to user
		this.instantiationMethodSelectionPanel.setVisible(true);
        this.entityCloningPanel.setVisible(false);
        this.entitySpecializationPanel.setVisible(false);
        this.restrictionsPanel.setVisible(false);
        this.alignmentsPanel.setVisible(false);
        this.previewPanel.setVisible(false);
        this.activeWizardScreen = ActiveWizardScreen.METHOD_SELECTION;
        
        // Set default state of buttons
        this.wizardBackButton.setEnabled(false);
        this.wizardNextButton.setEnabled(true);
		this.wizardFinishButton.setEnabled(false);
        
		// Reset initial timestamps that keep track of alignments being up to date or not 
		this.updateAlignmentModificationTimestamp();
		this.updateRestrictionsModificationTimestamp();
        this.updateInstantiationModificationTimestamp();
        this.updateInstantiationMethodSelectionTimestamp();
		
		// Initiate spinner UI
        this.showSpinner("Loading CODP...");
		
		// Clear wizard-level data structures
        this.instantiationMethod = CodpInstantiationMethod.TEMPLATE_BASED;
		this.classTree = new FrameTreeNode<OntologyEntityFrame>(new ClassFrame("nil"));
		this.objectPropertyTree = new FrameTreeNode<OntologyEntityFrame>(new ObjectPropertyFrame("nil"));
		this.dataPropertyTree = new FrameTreeNode<OntologyEntityFrame>(new DataPropertyFrame("nil"));
		this.alignments.clear();
		
		// Re-populate wizard-level data structures w/ new data from server
        DispatchServiceManager.get().execute(new GetOdpContentsAction(uri), new DispatchServiceCallback<GetOdpContentsResult>() {
        	@Override
            public void handleSuccess(GetOdpContentsResult result) {
        		
        		odpIri = result.getOdpIri();
        		classTree = result.getClasses();
        		objectPropertyTree = result.getObjectProperties();
        		dataPropertyTree = result.getDataProperties();
        		
        		// Render initial wizard and preview panel.
        		instantiationMethodSelectionPanel.renderPanel();
        		previewPanel.renderPanel();
        		
        		// Kill the spinner UI
        		hideSpinner();
            }
        });
	}

	public CodpInstantiationMethod getInstantiationMethod() {
		return this.instantiationMethod;
	}
	
	public void setInstantiationMethod(CodpInstantiationMethod instantiationMethod) {
		this.instantiationMethod = instantiationMethod;
		this.updateInstantiationMethodSelectionTimestamp();
	}
	
	public void addChildOntologyEntityFrame(OntologyEntityFrame parentFrame, OntologyEntityFrame childFrame) {
		
	}
	
	public FrameTreeNode<OntologyEntityFrame> getClassTree() {
		return classTree;
	}

	public FrameTreeNode<OntologyEntityFrame> getObjectPropertyTree() {
		return objectPropertyTree;
	}

	public FrameTreeNode<OntologyEntityFrame> getDataPropertyTree() {
		return dataPropertyTree;
	}

	public void addRestriction(Restriction restriction) {
		this.restrictions.add(restriction);
		this.updateRestrictionsModificationTimestamp();
	}

	public void removeRestriction(Restriction restriction) {
		this.restrictions.remove(restriction);
		this.updateRestrictionsModificationTimestamp();
	}
}
