package edu.stanford.bmir.protege.web.client.xd.instantiation.old;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

import com.gwtext.client.core.EventObject; 
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.CardLayout;

import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.xd.DesignPatternDetailsPortlet;
import edu.stanford.bmir.protege.web.client.xd.instantiation.old.panels.AlignmentsPanel;
import edu.stanford.bmir.protege.web.client.xd.instantiation.old.panels.EntitySpecializationPanel;
import edu.stanford.bmir.protege.web.client.xd.instantiation.old.panels.PreviewPanel;
import edu.stanford.bmir.protege.web.client.xd.instantiation.old.panels.PropertyRestrictionPanel;
import edu.stanford.bmir.protege.web.client.xd.instantiation.old.panels.StrategySelectionPanel;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetOdpContentsAction;
import edu.stanford.bmir.protege.web.shared.xd.actions.PersistInstantiationAction;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpInstantiation;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpSpecializationStrategy;
import edu.stanford.bmir.protege.web.shared.xd.data.CodpInstantiationMethod;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpContentsResult;
import edu.stanford.bmir.protege.web.shared.xd.results.PersistInstantiationResult;

public class DesignPatternSpecializationWizard extends com.gwtext.client.widgets.Window {
	
	private ToolbarButton backButton;
	private ToolbarButton nextButton;
	private ToolbarButton finishButton;
	private Toolbar navigationBar;
	private ButtonListenerAdapter backButtonListener;
	private ButtonListenerAdapter forwardButtonListener;
	private CardLayout cardLayout;
	
	// Reference to project ID required for service calls
	private ProjectId projectId;
	
	// Individual screens of the specialization wizard
	private StrategySelectionPanel strategySelectionPanel;
	private EntitySpecializationPanel entitySpecializationPanel;
	private PropertyRestrictionPanel propertyRestrictionPanel;
	private AlignmentsPanel alignmentsPanel;
	private PreviewPanel previewPanel;
	
	// IRI of ODP being specialized
	private IRI odpIRI;
	
	private FrameTreeNode<OntologyEntityFrame> allClasses;
	private FrameTreeNode<OntologyEntityFrame> allObjectProperties;
	private FrameTreeNode<OntologyEntityFrame> allDataProperties;
	private Set<Alignment> alignments;
	
	private OdpSpecializationStrategy specializationStrategy;
	
	@SuppressWarnings("deprecation")
	public DesignPatternSpecializationWizard(DesignPatternDetailsPortlet parent) {
		this.setCloseAction(HIDE);
		
		this.projectId = parent.getProjectId();
		
		this.setTitle("ODP Specialisation Wizard");
		this.setWidth(640);
		this.setHeight(480);
		this.setResizable(false);
		 
		cardLayout = new CardLayout();
		this.setLayout(cardLayout);
		this.setActiveItem(0);
        
        // This is the toolbar at the bottom of the wizard that handles back/forward navigation
        // through the wizard interface
        navigationBar = new Toolbar();  
        forwardButtonListener = makeForwardButtonListener();
        backButtonListener = makeBackButtonListener();
        
        backButton = new ToolbarButton("Back", backButtonListener);  
        backButton.setId("move-prev");  
        navigationBar.addButton(backButton);  
        navigationBar.addFill();  
  
        nextButton = new ToolbarButton("Next", forwardButtonListener);  
        nextButton.setId("move-next");  
        navigationBar.addButton(nextButton);
        
        finishButton = new ToolbarButton("Finish", new ButtonListenerAdapter(){
        	public void onClick(Button button, EventObject e) {
        		saveAndClose();
        	}
        });
        finishButton.setId("move-finish");  
        navigationBar.addButton(finishButton);
        finishButton.setVisible(false);
        this.setBottomToolbar(navigationBar);  

        this.strategySelectionPanel = new StrategySelectionPanel();
        this.entitySpecializationPanel = new EntitySpecializationPanel(this);
        this.propertyRestrictionPanel = new PropertyRestrictionPanel(this);
        this.alignmentsPanel = new AlignmentsPanel(this);
        this.previewPanel = new PreviewPanel(this);
        
        // These are the individual cards/screens of the wizard interface.
        // The order in which they are added is important, as is the ID that 
        // they are assigned in their respective constructor.
        this.add(strategySelectionPanel);  
        this.add(entitySpecializationPanel);
        this.add(propertyRestrictionPanel); 
        this.add(alignmentsPanel);
        this.add(previewPanel);
        
        this.specializationStrategy = strategySelectionPanel.getSelectedStrategy();
	}
	
	// Controls what happens when back button is pressed
	private ButtonListenerAdapter makeBackButtonListener() {
		return new ButtonListenerAdapter() {  
            public void onClick(Button button, EventObject e) {      
                String panelID = cardLayout.getActiveItem().getId();  
  
                switch (panelID) {
                case "card-1":
                	// Moving from entity specialization panel to strategy selection panel
                	entitySpecializationPanel.resetSpecializations();
                	cardLayout.setActiveItem(0);
                	break;
                	
                case "card-2":
                	
                	// Moving from property restriction panel to entity specialization panel
                	propertyRestrictionPanel.resetRestrictions();
                	cardLayout.setActiveItem(1);
                	break;
                	
                case "card-3":
                	
                	// Moving from alignment panel to property restriction panel
                	alignmentsPanel.resetAlignments();
                	cardLayout.setActiveItem(2);
                	break;
                	
                case "card-4":
                	
                	// Moving from preview panel to alignment panel
                	cardLayout.setActiveItem(3);
                	finishButton.setVisible(false);
                    nextButton.setVisible(true);
                	break;
                } 
            }
        };  
	}
	
	// Controls what happens when forward button is pressed
	private ButtonListenerAdapter makeForwardButtonListener() {
		return new ButtonListenerAdapter() {  
            public void onClick(Button button, EventObject e) {  
                String panelID = cardLayout.getActiveItem().getId();  
  
                switch (panelID) {
                case "card-0":
                	// Moving from strategy selection panel to entity specialization panel
                	specializationStrategy = strategySelectionPanel.getSelectedStrategy();
                	cardLayout.setActiveItem(1);
                	break;
                	
                case "card-1":
                	// Moving from entity specialization panel to property restriction panel
                	allClasses = entitySpecializationPanel.getAllClasses();
                	allObjectProperties = entitySpecializationPanel.getAllObjectProperties();
                	allDataProperties = entitySpecializationPanel.getAllDataProperties();
                	alignments = entitySpecializationPanel.getAlignments();
                	propertyRestrictionPanel.loadEntities();
                	cardLayout.setActiveItem(2);
                	break;
                	
                case "card-2":
                	
                	// Moving from property restriction panel to alignment panel
                	propertyRestrictionPanel.persistRestrictions();
                	alignmentsPanel.loadCandidateAlignments();
                	cardLayout.setActiveItem(3);
                	break;
                	
                case "card-3":
                	
                	// Moving from alignment panel to preview panel 
                	cardLayout.setActiveItem(4);
                	finishButton.setVisible(true);
                    nextButton.setVisible(false);
                	break;
                }
            }
        };  
	}
	
	/**
	 *  Returns a filtered subset of the input set of class or property trees, containing
	 *  only those that have been specialized in this wizard (e.g., those that do not have
	 *  any minted IRI), flattened to a set of root nodes.
	 * @param inputFrameTreeRoot - root node to search through
	 * @return
	 */
	private Set<FrameTreeNode<OntologyEntityFrame>> getSpecializedEntityTrees(FrameTreeNode<OntologyEntityFrame> inputFrameTreeRoot) {
		Set<FrameTreeNode<OntologyEntityFrame>> specializedEntityRoots = new HashSet<FrameTreeNode<OntologyEntityFrame>>();
		// This node has an IRI, e.g., is a pre-existing concept in the ontology, e.g., recurse deeper into child nodes.
		if (inputFrameTreeRoot.getData().getIri().isPresent()) {
			for (FrameTreeNode<OntologyEntityFrame> childNode: inputFrameTreeRoot.getChildren()) {
				specializedEntityRoots.addAll(getSpecializedEntityTrees(childNode));
			}
		}
		// This node has no IRI so it is a specialized subtree. Add it to the set to be returned.
		else {
			specializedEntityRoots.add(inputFrameTreeRoot);
		}
		return specializedEntityRoots;
	}
	
	public Set<FrameTreeNode<OntologyEntityFrame>> getSpecializedClasses() {
		return getSpecializedEntityTrees(this.allClasses);
	}
	
	public Set<FrameTreeNode<OntologyEntityFrame>> getSpecializedObjectProperties() {
		return getSpecializedEntityTrees(this.allObjectProperties);
	}
	
	public Set<FrameTreeNode<OntologyEntityFrame>> getSpecializedDataProperties() {
		return getSpecializedEntityTrees(this.allDataProperties);
	}

	/**
	 * This method stores the customized ODP instantiation into the ontology on the server side
	 * and then closes down the wizard. 
	 */
	private void saveAndClose() {	
		OdpInstantiation odpSpec = this.getSpecialization();
		PersistInstantiationAction psa = new PersistInstantiationAction(odpSpec);
		
		// Put up progress window
		MessageBox.show(new MessageBoxConfig() {  
            {  
                setMsg("Saving ODP Specialisation, please wait...");  
                setProgressText("Saving ODP Specialisation...");  
                setWidth(300);  
                setWait(true);  
                setWaitConfig(new WaitConfig() {  
                    {  
                        setInterval(200);  
                    }  
                });    
            }  
        }); 
		
		DispatchServiceManager.get().execute(psa, new DispatchServiceCallback<PersistInstantiationResult>() {
        	@Override
            public void handleSuccess(PersistInstantiationResult result) {
        		// Hide progress window once done
        		MessageBox.hide();
        		closeAndResetSpecializationWizard();
            }
        });
	}
	
	/**
	 * Clear all used fields and close the Specialization Wizard.
	 */
	private void closeAndResetSpecializationWizard() {
		this.clearAllFields();
		this.hide();
	}
	

	// This is where we clear out old data, load new data required 
	// to run the wizard, prepare fields, etc etc
	public void loadOdp(String uri) {
		
		// Empty out everything
		this.clearAllFields();
		
		// Set IRI that we use
		this.odpIRI = IRI.create(uri);
		
		// Throw up progress window
		MessageBox.show(new MessageBoxConfig() {  
            {  
                setMsg("Loading ODP, please wait...");  
                setProgressText("Loading ODP...");  
                setWidth(300);  
                setWait(true);  
                setWaitConfig(new WaitConfig() {  
                    {  
                        setInterval(200);  
                    }  
                });    
            }  
        }); 
		
		// Get ODP implementation from server
        DispatchServiceManager.get().execute(new GetOdpContentsAction(uri), new DispatchServiceCallback<GetOdpContentsResult>() {
        	@Override
            public void handleSuccess(GetOdpContentsResult result) {
        		
        		FrameTreeNode<OntologyEntityFrame> odpClasses = result.getClasses();
        		FrameTreeNode<OntologyEntityFrame> odpObjectProperties = result.getObjectProperties();
        		FrameTreeNode<OntologyEntityFrame> odpDataProperties = result.getDataProperties();
        		
        		entitySpecializationPanel.populateEntityTree(odpClasses, odpObjectProperties, odpDataProperties);
        		
        		// Hide progress window once done
        		MessageBox.hide();
            }
        });
	}
	
	private void clearAllFields() {
		// TODO Auto-generated method stub
		
	}

	public OdpSpecializationStrategy getSpecializationStrategy() {
		return specializationStrategy;
	}

	public FrameTreeNode<OntologyEntityFrame> getAllClasses() {
		return allClasses;
	}

	public FrameTreeNode<OntologyEntityFrame> getAllObjectProperties() {
		return allObjectProperties;
	}

	public FrameTreeNode<OntologyEntityFrame> getAllDataProperties() {
		return allDataProperties;
	}
	
	public OdpInstantiation getSpecialization() {
		Set<Alignment> selectedAlignments = alignmentsPanel.getSelectedAlignments();
		selectedAlignments.addAll(alignments);
		
		return new OdpInstantiation(projectId, odpIRI, this.allClasses, this.allObjectProperties, this.allDataProperties, selectedAlignments, CodpInstantiationMethod.IMPORT_BASED, specializationStrategy);
	}

	public ProjectId getProjectId() {
		return projectId;
	}
}
