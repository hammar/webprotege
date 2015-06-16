package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.RowLayout;
import com.gwtext.client.widgets.layout.RowLayoutData;
import com.gwtext.client.widgets.tree.DefaultSelectionModel;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

import edu.stanford.bmir.protege.web.client.xd.specialization.EntityDetailsWindow;
import edu.stanford.bmir.protege.web.client.xd.specialization.XdSpecializationWizard;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpSpecializationStrategy;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.SubClassAlignment;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.SubDataPropertyAlignment;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.SubObjectPropertyAlignment;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class EntitySpecializationPanel extends Panel {

	private TreePanel entityTreePanel;
	private EntityDetailsWindow edWindow;
	private TreeNode rootNode;
	
	private ToolbarButton specializeButton;
	private ToolbarButton modifyButton;
	private ToolbarButton deleteButton;
	
	private FrameTreeNode<OntologyEntityFrame> allClasses;
	private FrameTreeNode<OntologyEntityFrame> allObjectProperties;
	private FrameTreeNode<OntologyEntityFrame> allDataProperties;
	
	private XdSpecializationWizard parentWizard;
	
	public EntitySpecializationPanel(XdSpecializationWizard parent) {
		super();
		
		this.parentWizard = parent;
		this.edWindow = new EntityDetailsWindow();
		
		this.setLayout(new RowLayout());  
        this.setId("card-1");
        this.setTitle("Entity Specialization");
        
        Panel instructionPanel = new Panel();
        instructionPanel.setPaddings(5);
        Label instruction = new Label("Please select the classes, object properties, and datatype properties from "
        		+ "the ontology design pattern that you wish to specialize for your modeling need from the list "
        		+ "below. Note that if using the class-oriented strategy you will be prevented from specialising "
        		+ "properties.");
        instructionPanel.add(instruction);
        this.add(instructionPanel, new RowLayoutData(50));
        
        entityTreePanel = new TreePanel();
        entityTreePanel.setTitle("ODP Entities");
        entityTreePanel.addListener(new TreePanelListenerAdapter() {
        	public void onClick(TreeNode node, EventObject e) {
        		if (node.getAttributeAsObject("iri") != null) {
        			modifyButton.setDisabled(true);
        			deleteButton.setDisabled(true);
        		}
        		else {
        			modifyButton.setDisabled(false);
        			deleteButton.setDisabled(false);
        		}
        		if (parentWizard.getSpecializationStrategy() == OdpSpecializationStrategy.CLASS_ORIENTED) {
        			if (node.getAttribute("type").equalsIgnoreCase("owlClassTreeNode")) {
        				specializeButton.setDisabled(false);
        			}
        			else {
        				specializeButton.setDisabled(true);
        			}
        		}
        		else {
        			specializeButton.setDisabled(false);
        		}
        	}
        });
        entityTreePanel.setRootVisible(false);
        entityTreePanel.setAutoScroll(true);
        rootNode = new TreeNode();
        entityTreePanel.setRootNode(rootNode);
        this.add(entityTreePanel);

        Toolbar specializationControlsToolbar = new Toolbar();
        specializeButton = new ToolbarButton("Specialise");
        specializeButton.addListener(new ButtonListenerAdapter() {  
            public void onClick(Button button, EventObject e) {
            	DefaultSelectionModel dsm = (DefaultSelectionModel)entityTreePanel.getSelectionModel();
            	edWindow.newFrameAndShow(dsm.getSelectedNode());
            }
        });
        modifyButton = new ToolbarButton("Modify");
        modifyButton.addListener(new ButtonListenerAdapter() {  
            public void onClick(Button button, EventObject e) {
            	DefaultSelectionModel dsm = (DefaultSelectionModel)entityTreePanel.getSelectionModel();
            	TreeNode selectedNode = dsm.getSelectedNode();
            	if (selectedNode.getAttributeAsObject("frame")!=null) {
            		edWindow.loadFrameAndShow(selectedNode);
            	}
            }
        });
        deleteButton = new ToolbarButton("Delete");
        deleteButton.addListener(new ButtonListenerAdapter() {  
            public void onClick(Button button, EventObject e) {
            	MessageBox.confirm("Confirm Deletion", "Are you sure you want to delete the selected entity?", new MessageBox.ConfirmCallback() {
            		public void execute(String btnID) {
            			if (btnID.equalsIgnoreCase("yes")) {
            				DefaultSelectionModel dsm = (DefaultSelectionModel)entityTreePanel.getSelectionModel();
            				TreeNode selectedNode = dsm.getSelectedNode();
            				selectedNode.getParentNode().removeChild(selectedNode);
            			}
            		}
            	});  
            }
        });
        specializationControlsToolbar.addButton(specializeButton);
        specializationControlsToolbar.addButton(modifyButton);
        specializationControlsToolbar.addButton(deleteButton);
        entityTreePanel.setTopToolbar(specializationControlsToolbar);
	}
	
	/**
	 * Clear out the entity tree, leaving only the root node.
	 */
	private void clearEntityTree() {
		for (Node childNode: rootNode.getChildNodes()) {
			TreeNode treeNode = (TreeNode)childNode;
			this.deleteTree(treeNode);
		}
	}
	
	/**
	 * Recursive method for removing a subtree.
	 * @param root - the root of the 
	 */
	private void deleteTree(TreeNode root) {
		// Recurse into child trees
		for (Node childNode: root.getChildNodes()) {
			TreeNode childTreeNode = (TreeNode)childNode;
			this.deleteTree(childTreeNode);
		}
		// Remove self
		root.getParentNode().removeChild(root);
	}
	
	/**
	 * Recursive method for removing all subtrees that do not have
	 * IRIs defined, e.g. that are specializations that have not
	 * yet been persisted to the ontology and had IRIs minted.
	 * @param root
	 */
	private void deleteSpecializedSubTrees(TreeNode root) {
		// Recurse into child trees
		for (Node childNode: root.getChildNodes()) {
			TreeNode childTreeNode = (TreeNode)childNode;
			this.deleteSpecializedSubTrees(childTreeNode);
		}
		// Remove self if self does not have IRI defined
		if (root.getAttribute("iri") == null) {
			root.getParentNode().removeChild(root);
		}
	}
	
	/**
	 * Populate the entity tree with content.
	 * @param classes
	 * @param objectProperties
	 * @param dataProperties
	 */
	public void populateEntityTree(FrameTreeNode<OntologyEntityFrame> classes, 
			FrameTreeNode<OntologyEntityFrame> objectProperties,
			FrameTreeNode<OntologyEntityFrame> dataProperties) {
		
		// Load private fields for ease of access
		this.allClasses = classes;
		this.allObjectProperties = objectProperties;
		this.allDataProperties = dataProperties;
		
		// Begin by clearing out existing entity tree
		this.clearEntityTree();
		
		// Render classes - ignore top node OWL:Thing which we don't want to display
		for (FrameTreeNode<OntologyEntityFrame> subClass: classes.getChildren()) {
			recursivelyAddClassNode(subClass, rootNode);
		}
		
		// Render object properties - ignore top level OWL:TopObjectProperty that we don't want to display
		for (FrameTreeNode<OntologyEntityFrame> subObjectProperty: objectProperties.getChildren()) {
			recursivelyAddObjectPropertyNode(subObjectProperty, rootNode);
		}
		
		// Render data properties - ignore top level OWL:TopDataProperty that we don't want to display
		for (FrameTreeNode<OntologyEntityFrame> subDataProperty: dataProperties.getChildren()) {
			recursivelyAddDataPropertyNode(subDataProperty, rootNode);
		}
		
		entityTreePanel.expandAll();
	}
	
	/**
	 * Recursively creates and adds ClassTreeNodes to the entityTreePanel.
	 * @param subClass
	 * @param rootNode
	 */
	private void recursivelyAddClassNode(FrameTreeNode<OntologyEntityFrame> childNodeFromServer, TreeNode parentNode) {
		OntologyEntityFrame childClassFrame = childNodeFromServer.getData();
		TreeNode newChildNode = new TreeNode(childClassFrame.getLabel(),"owlClassTreeNode");
		newChildNode.setAttribute("type", "owlClassTreeNode");
		newChildNode.setAttribute("frame", childClassFrame);
		
		// If an IRI exists in frame from server, add to node
		if (childClassFrame.getIri().isPresent()) {
			newChildNode.setAttribute("iri", childClassFrame.getIri().get());
		}
		
		// If a comment exists in frame from server, set as tooltip
		if (childClassFrame.getComment().isPresent()) {
			newChildNode.setTooltip(childClassFrame.getComment().get());
		}
		
		parentNode.appendChild(newChildNode);
		for (FrameTreeNode<OntologyEntityFrame> nextChild: childNodeFromServer.getChildren()) {
			recursivelyAddClassNode(nextChild, newChildNode);
		}
	}

	/**
	 * Recursively creates and adds ObjectPropertyTreeNodes to the entityTreePanel.
	 * @param subClass
	 * @param rootNode
	 */
	private void recursivelyAddObjectPropertyNode(FrameTreeNode<OntologyEntityFrame> childNodeFromServer, TreeNode parentNode) {
		OntologyEntityFrame childObjectPropertyFrame = childNodeFromServer.getData();
		TreeNode newChildNode = new TreeNode(childObjectPropertyFrame.getLabel(),"owlObjectPropertyTreeNode");
		newChildNode.setAttribute("type", "owlObjectPropertyTreeNode");
		newChildNode.setAttribute("frame", childObjectPropertyFrame);
		
		// If IRI exists in frame from server, set it for node
		if (childObjectPropertyFrame.getIri().isPresent()) {
			newChildNode.setAttribute("iri", childObjectPropertyFrame.getIri().get());
		}
		
		// If a comment exists in frame from server, set as tooltip
		if (childObjectPropertyFrame.getComment().isPresent()) {
			newChildNode.setTooltip(childObjectPropertyFrame.getComment().get());
		}

		parentNode.appendChild(newChildNode);
		for (FrameTreeNode<OntologyEntityFrame> nextChild: childNodeFromServer.getChildren()) {
			recursivelyAddObjectPropertyNode(nextChild, newChildNode);
		}
	}
	
	private Set<Alignment> getChildAlignments(TreeNode rootNode) {
		Set<Alignment> alignments = new HashSet<Alignment>();
		// We are looking for nodes that have minted IRIs (e.g., existing ODP entities)
		// and children that do not.
		if (rootNode.getAttribute("iri") != null) {
			// This is an existing ODP concept that has a minted IRI
			
			for (Node childNode: rootNode.getChildNodes()) {
				TreeNode childTreeNode = (TreeNode)childNode;
				if (childTreeNode.getAttribute("iri")==null) {
					// Found IRI-carrying parent/Non-IRI-carrying child alignment!
					String childType = childTreeNode.getAttribute("type");
					if (childType.equalsIgnoreCase("owlClassTreeNode")) {
						ClassFrame parentFrame = (ClassFrame)rootNode.getAttributeAsObject("frame");
						ClassFrame childFrame = (ClassFrame)childTreeNode.getAttributeAsObject("frame");
						SubClassAlignment alignment = new SubClassAlignment(parentFrame, childFrame);
						alignments.add(alignment);
					}
					else if (childType.equalsIgnoreCase("owlDataPropertyTreeNode")) {
						DataPropertyFrame parentFrame = (DataPropertyFrame)rootNode.getAttributeAsObject("frame");
						DataPropertyFrame childFrame = (DataPropertyFrame)childTreeNode.getAttributeAsObject("frame");
						SubDataPropertyAlignment alignment = new SubDataPropertyAlignment(parentFrame, childFrame);
						alignments.add(alignment);
					}
					else if (childType.equalsIgnoreCase("owlObjectPropertyTreeNode")) {
						ObjectPropertyFrame parentFrame = (ObjectPropertyFrame)rootNode.getAttributeAsObject("frame");
						ObjectPropertyFrame childFrame = (ObjectPropertyFrame)childTreeNode.getAttributeAsObject("frame");
						SubObjectPropertyAlignment alignment = new SubObjectPropertyAlignment(parentFrame, childFrame);
						alignments.add(alignment);
					}
				}
				else {
					// Recurse one step deeper into child tree
					alignments.addAll(getChildAlignments(childTreeNode));
				}
			}
		}
		return alignments;
	}
	
	public Set<Alignment> getAlignments() {
		Set<Alignment> alignments = new HashSet<Alignment>();
		// For all childnodes of the root (hidden node)
		for (Node childNode: rootNode.getChildNodes()) {
			TreeNode treeNode = (TreeNode)childNode;
			// Recursively search and get any alignments 
			alignments.addAll(getChildAlignments(treeNode));
		}
		return alignments;
	}
	
	/**
	 * Returns a tree of frames suitable for GWT-RPC serialization from a given 
	 * starting node in a GWT-Ext TreePanel.
	 * @param rootClassNode Starting node
	 * @return
	 */
	private FrameTreeNode<OntologyEntityFrame> getChildFrameTrees(TreeNode rootClassNode) {
		OntologyEntityFrame rootEntityFrame = (OntologyEntityFrame)rootClassNode.getAttributeAsObject("frame");
		/*if (rootClassNode.getAttribute("iri") != null) {
			rootEntityFrame.setIri(IRI.create(rootClassNode.getAttribute("iri")));
		}*/
		FrameTreeNode<OntologyEntityFrame> rootTreeNodeToServer = new FrameTreeNode<OntologyEntityFrame>(rootEntityFrame);
		for (Node childTreeNode: rootClassNode.getChildNodes()) {
			TreeNode childClassTreeNode = (TreeNode)childTreeNode;
			rootTreeNodeToServer.addChildTree(getChildFrameTrees(childClassTreeNode));
		}
		return rootTreeNodeToServer;
	}
	
	/**
	 * Recursively creates and adds DataPropertyTreeNodes to the entityTreePanel.
	 * @param subClass
	 * @param rootNode
	 */
	private void recursivelyAddDataPropertyNode(FrameTreeNode<OntologyEntityFrame> childNodeFromServer, TreeNode parentNode) {
		OntologyEntityFrame childDataPropertyFrame = childNodeFromServer.getData();
		TreeNode newChildNode = new TreeNode(childDataPropertyFrame.getLabel(), "owlDataPropertyTreeNode");
		newChildNode.setAttribute("type", "owlDataPropertyTreeNode");
		newChildNode.setAttribute("frame", childDataPropertyFrame);
		
		// If IRI exists in frame from server, set it for node
		if (childDataPropertyFrame.getIri().isPresent()) {
			newChildNode.setAttribute("iri", childDataPropertyFrame.getIri().get());
		}
		
		// If a comment exists in frame from server, set as tooltip
		if (childDataPropertyFrame.getComment().isPresent()) {
			newChildNode.setTooltip(childDataPropertyFrame.getComment().get());
		}
		
		parentNode.appendChild(newChildNode);
		for (FrameTreeNode<OntologyEntityFrame> nextChild: childNodeFromServer.getChildren()) {
			recursivelyAddDataPropertyNode(nextChild, newChildNode);
		}
	}
	
	public void resetSpecializations() {
		for (Node childNode: rootNode.getChildNodes()) {
			TreeNode treeNode = (TreeNode)childNode;
			this.deleteSpecializedSubTrees(treeNode);
		}
		entityTreePanel.getSelectionModel().clearSelections();
	}
	
	/*
	 * Returns a set of trees holding the ClassFrames that were specialized by the user.  
	 * @return
	 */
	/*public Set<FrameTreeNode<OntologyEntityFrame>> getSpecializedClasses() {
		Set<FrameTreeNode<OntologyEntityFrame>> trees = new HashSet<FrameTreeNode<OntologyEntityFrame>>();
		
		// For those root tree nodes that are classes
		for (Node childNode: rootNode.getChildNodes()) {
			TreeNode treeNode = (TreeNode)childNode;
			if (treeNode.getAttribute("type").equalsIgnoreCase("owlClassTreeNode")) {
				// Get and add all specialized class subtrees that exist below those tree nodes 
				trees.addAll(getSpecializedFrames(treeNode));
			}
		}
		return trees;
	}*/
	
	/*public Set<FrameTreeNode<OntologyEntityFrame>> getSpecializedObjectProperties() {
		Set<FrameTreeNode<OntologyEntityFrame>> trees = new HashSet<FrameTreeNode<OntologyEntityFrame>>();
		
		// For those root tree nodes that are object properties
		for (Node childNode: rootNode.getChildNodes()) {
			TreeNode treeNode = (TreeNode)childNode;
			if (treeNode.getAttribute("type").equalsIgnoreCase("owlObjectPropertyTreeNode")) {
				// Get and add all specialized class subtrees that exist below those tree nodes 
				trees.addAll(getSpecializedFrames(treeNode));
			}
		}
		return trees;
	}*/
	
	/*
	public Set<FrameTreeNode<OntologyEntityFrame>> getSpecializedDataProperties() {
		Set<FrameTreeNode<OntologyEntityFrame>> trees = new HashSet<FrameTreeNode<OntologyEntityFrame>>();
		
		// For those root tree nodes that are datatype properties
		for (Node childNode: rootNode.getChildNodes()) {
			TreeNode treeNode = (TreeNode)childNode;
			if (treeNode.getAttribute("type").equalsIgnoreCase("owlDataPropertyTreeNode")) {
				// Get and add all specialized class subtrees that exist below those tree nodes 
				trees.addAll(getSpecializedFrames(treeNode));
			}
		}
		return trees;
	}*/

	public FrameTreeNode<OntologyEntityFrame> getAllClasses() {
		
		// Copy existing root class frame, place in new tree
		FrameTreeNode<OntologyEntityFrame> rootClassNode = new FrameTreeNode<OntologyEntityFrame>(this.allClasses.getData());
		
		// For those root tree nodes in the entity tree panel that are classes
		for (Node childNode: rootNode.getChildNodes()) {
			TreeNode treeNode = (TreeNode)childNode;
			if (treeNode.getAttributeAsObject("frame").getClass() == ClassFrame.class) {
				// Get and add child trees 
				rootClassNode.addChildTree(getChildFrameTrees(treeNode));
			}
		}
		return rootClassNode;
	}

	public FrameTreeNode<OntologyEntityFrame> getAllObjectProperties() {
		// Copy existing root object property frame, place in new tree
		FrameTreeNode<OntologyEntityFrame> rootObjectPropertyNode = new FrameTreeNode<OntologyEntityFrame>(this.allObjectProperties.getData());
		
		// For those root tree nodes in the entity tree panel that are object properties
		for (Node childNode: rootNode.getChildNodes()) {
			TreeNode treeNode = (TreeNode)childNode;
			if (treeNode.getAttributeAsObject("frame").getClass() == ObjectPropertyFrame.class) {
				// Get and add child trees
				rootObjectPropertyNode.addChildTree(getChildFrameTrees(treeNode));
			}
		}
		return rootObjectPropertyNode;
	}

	
	public FrameTreeNode<OntologyEntityFrame> getAllDataProperties() {
		// Copy existing root data property frame, place in new tree
		FrameTreeNode<OntologyEntityFrame> rootDataPropertyNode = new FrameTreeNode<OntologyEntityFrame>(this.allDataProperties.getData());
		
		// For those root tree nodes in the entity tree panel that are object properties
		for (Node childNode: rootNode.getChildNodes()) {
			TreeNode treeNode = (TreeNode)childNode;
			if (treeNode.getAttributeAsObject("frame").getClass() == DataPropertyFrame.class) {
				// Get and add child trees
				rootDataPropertyNode.addChildTree(getChildFrameTrees(treeNode));
			}
		}
		return rootDataPropertyNode;
	}
}
