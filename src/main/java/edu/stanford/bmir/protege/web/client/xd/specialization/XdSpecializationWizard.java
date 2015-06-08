package edu.stanford.bmir.protege.web.client.xd.specialization;

import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

import com.google.gwt.user.client.Window;
import com.gwtext.client.core.EventObject; 
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.CardLayout;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.xd.XdPatternDetailsPortlet;
import edu.stanford.bmir.protege.web.client.xd.specialization.panels.AlignmentsPanel;
import edu.stanford.bmir.protege.web.client.xd.specialization.panels.EntitySpecializationPanel;
import edu.stanford.bmir.protege.web.client.xd.specialization.panels.PreviewPanel;
import edu.stanford.bmir.protege.web.client.xd.specialization.panels.PropertyRestrictionPanel;
import edu.stanford.bmir.protege.web.client.xd.specialization.panels.StrategySelectionPanel;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetOdpContentsAction;
import edu.stanford.bmir.protege.web.shared.xd.actions.PersistSpecializationAction;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpSpecialization;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpSpecializationStrategy;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpContentsResult;
import edu.stanford.bmir.protege.web.shared.xd.results.PersistSpecializationResult;

public class XdSpecializationWizard extends com.gwtext.client.widgets.Window {
	
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
	
	private Set<FrameTreeNode<OntologyEntityFrame>> allClasses;
	private Set<FrameTreeNode<OntologyEntityFrame>> allObjectProperties;
	private Set<FrameTreeNode<OntologyEntityFrame>> allDataProperties;
	private Set<Alignment> alignments;
	
	private OdpSpecializationStrategy specializationStrategy;
	
	@SuppressWarnings("deprecation")
	public XdSpecializationWizard(XdPatternDetailsPortlet parent) {
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
        this.alignmentsPanel = new AlignmentsPanel();
        this.previewPanel = new PreviewPanel();
        
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
	
	// DEBUG code can be removed
	private String printFrameTree(FrameTreeNode<OntologyEntityFrame> classes, String prefix) {
		String ret = prefix + " " + classes.getData().getLabel();
		for (FrameTreeNode<OntologyEntityFrame> childFrameTreeNode: classes.getChildren()) {
			ret += ("\n" +  printFrameTree(childFrameTreeNode, prefix + "|"));
		}
		return ret;
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
                	
                	//This is test code to ensure that the way specialziedClasses is generated makes sense.
                	// Remove once done testing.
                	/*String alignmentsString = "";
                	for (Alignment a: alignments) {
                		alignmentsString += (a.toString() + "\n");
                	}
                	Window.alert(alignmentsString);*/
                	/*String classTree = "";
                	for (FrameTreeNode<OntologyEntityFrame> tree: allClasses) {
                		classTree += printFrameTree(tree,"|") + "\n";
                	}
                	Window.alert(classTree);*/
                	/*
                	String dataPropertyTree = "";
                	for (FrameTreeNode<OntologyEntityFrame> tree: specializedDataProperties) {
                		dataPropertyTree += printFrameTree(tree,"|") + "\n";
                	}
                	Window.alert(dataPropertyTree);
                	
                	String objectPropertyTree = "";
                	for (FrameTreeNode<OntologyEntityFrame> tree: specializedObjectProperties) {
                		objectPropertyTree += printFrameTree(tree,"|") + "\n";
                	}
                	Window.alert(objectPropertyTree);*/
                	
                	cardLayout.setActiveItem(2);
                	break;
                	
                case "card-2":
                	
                	// Moving from property restriction panel to alignment panel 
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
	
	// Returns a filtered subset of the input set of class or property trees, containing
	// only those that have been specialized in this wizard (e.g., those that do not have
	// any minted IRI).
	public Set<FrameTreeNode<OntologyEntityFrame>> getSpecializedEntityTrees(Set<FrameTreeNode<OntologyEntityFrame>> inputFrameTrees) {
		// TODO: Implement this
		return null;
		
	}

	/**
	 * This method stores the customized ODP instantiation into the ontology on the server side
	 * and then closes down the wizard. 
	 */
	private void saveAndClose() {	
		
		// TODO: Fetch alignments from user selections
		Alignment[] selectedAlignments = new Alignment[0];
		
		// Generate ODP Specialization object and action to pass to dispach service
		Set<FrameTreeNode<OntologyEntityFrame>> specializedClasses = getSpecializedEntityTrees(this.allClasses);
		Set<FrameTreeNode<OntologyEntityFrame>> specializedObjectProperties = getSpecializedEntityTrees(this.allObjectProperties);
		Set<FrameTreeNode<OntologyEntityFrame>> specializedDataProperties = getSpecializedEntityTrees(this.allDataProperties);
		OdpSpecialization odpSpec = new OdpSpecialization(this.projectId, this.odpIRI, this.specializationStrategy, 
				selectedAlignments, specializedClasses, specializedObjectProperties, 
				specializedDataProperties);
		PersistSpecializationAction psa = new PersistSpecializationAction(odpSpec);
		
		DispatchServiceManager.get().execute(psa, new DispatchServiceCallback<PersistSpecializationResult>() {
        	@Override
            public void handleSuccess(PersistSpecializationResult result) {
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
		
		// Get ODP implementation from server
        DispatchServiceManager.get().execute(new GetOdpContentsAction(uri), new DispatchServiceCallback<GetOdpContentsResult>() {
        	@Override
            public void handleSuccess(GetOdpContentsResult result) {
        		
        		FrameTreeNode<ClassFrame> odpClasses = result.getClasses();
        		FrameTreeNode<ObjectPropertyFrame> odpObjectProperties = result.getObjectProperties();
        		FrameTreeNode<DataPropertyFrame> odpDataProperties = result.getDataProperties();
        		
        		entitySpecializationPanel.populateEntityTree(odpClasses, odpObjectProperties, odpDataProperties);
            }
        });
	}
	
	private void clearAllFields() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * Recursive method to walk the a tree panel and return leaf nodes.
	 * @param parentNode - starting node for each recursion.
	 * @return lowest level TreeNodes, e.g. with no children.
	 */
	/*
	private Set<TreeNode> getChildLeafNodes(TreeNode parentNode) {
		HashSet<TreeNode> leaves = new HashSet<TreeNode>();
		if (parentNode.getChildNodes().length == 0) {
			leaves.add(parentNode);
		}
		else {
			for (final Node childNode: parentNode.getChildNodes()) {
				leaves.addAll(getChildLeafNodes((TreeNode)childNode));
			}
		}
		return leaves;
	}*/ 
	
	
	/*
	 * Recursive method to walk the a tree panel and return all nodes.
	 * @param parentNode - starting node for each recursion.
	 * @return all nodes in tree
	 */
	/*
	private Set<TreeNode> getChildNodes(TreeNode parentNode) {
		HashSet<TreeNode> leaves = new HashSet<TreeNode>();
		leaves.add(parentNode);
		for (final Node childNode: parentNode.getChildNodes()) {
			leaves.addAll(getChildNodes((TreeNode)childNode));
		}
		return leaves;
	}*/

	public OdpSpecializationStrategy getSpecializationStrategy() {
		return specializationStrategy;
	}

	public Set<FrameTreeNode<OntologyEntityFrame>> getAllClasses() {
		return allClasses;
	}

	public Set<FrameTreeNode<OntologyEntityFrame>> getAllObjectProperties() {
		return allObjectProperties;
	}

	public Set<FrameTreeNode<OntologyEntityFrame>> getAllDataProperties() {
		return allDataProperties;
	} 
}
