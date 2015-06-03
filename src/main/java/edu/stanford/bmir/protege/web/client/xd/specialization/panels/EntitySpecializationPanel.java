package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.RowLayout;
import com.gwtext.client.widgets.layout.RowLayoutData;
import com.gwtext.client.widgets.tree.DefaultSelectionModel;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

import edu.stanford.bmir.protege.web.client.xd.specialization.EntityDetailsWindow;
import edu.stanford.bmir.protege.web.client.xd.specialization.XdSpecializationWizard;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpSpecializationStrategy;
import edu.stanford.bmir.protege.web.shared.xd.data.XdTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class EntitySpecializationPanel extends Panel {

	private TreePanel entityTreePanel;
	private EntityDetailsWindow edWindow;
	private TreeNode rootNode;
	
	private Button specializeButton;
	private Button modifyButton;
	private Button deleteButton;
	
	private XdSpecializationWizard parentWizard;
	
	public EntitySpecializationPanel(XdSpecializationWizard parent) {
		super();
		
		this.parentWizard = parent;
		this.edWindow = new EntityDetailsWindow();
		
		this.setLayout(new RowLayout());  
        this.setId("card-1");
        this.setTitle("Entity Specialization");
        
        Panel instructionPanel = new Panel();
        instructionPanel.setPaddings(10);
        Label instruction = new Label("Please select the classes, object properties, and datatype properties from "
        		+ "the ontology design pattern that you wish to specialize for your modeling need from the list "
        		+ "below. Note that if using the class-oriented strategy you will be prevented from specialising "
        		+ "properties.");
        instructionPanel.add(instruction);
        instructionPanel.setBodyStyle("border-bottom: 1px solid #99bbe8; padding: 15px;");
        this.add(instructionPanel, new RowLayoutData(50));
        
        entityTreePanel = new TreePanel();
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
        
        Panel specializationButtonsPanel = new Panel();
        specializationButtonsPanel.setBodyStyle("border-top: 1px solid #99bbe8;");  
        specializationButtonsPanel.setPaddings(10);
        specializationButtonsPanel.setLayout(new HorizontalLayout(15));
        specializeButton = new Button("Specialize");
        specializeButton.addListener(new ButtonListenerAdapter() {  
            public void onClick(Button button, EventObject e) {
            	DefaultSelectionModel dsm = (DefaultSelectionModel)entityTreePanel.getSelectionModel();
            	edWindow.newFrameAndShow(dsm.getSelectedNode());
            }
        });
        modifyButton = new Button("Modify");
        modifyButton.addListener(new ButtonListenerAdapter() {  
            public void onClick(Button button, EventObject e) {
            	DefaultSelectionModel dsm = (DefaultSelectionModel)entityTreePanel.getSelectionModel();
            	TreeNode selectedNode = dsm.getSelectedNode();
            	if (selectedNode.getAttributeAsObject("frame")!=null) {
            		OntologyEntityFrame selectedFrame = (OntologyEntityFrame)selectedNode.getAttributeAsObject("frame");
            		edWindow.loadFrameAndShow(selectedFrame);
            	}
            }
        });
        deleteButton = new Button("Delete");
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
        specializationButtonsPanel.add(specializeButton);
        specializationButtonsPanel.add(modifyButton);
        specializationButtonsPanel.add(deleteButton);
        this.add(specializationButtonsPanel, new RowLayoutData(50));
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
	public void populateEntityTree(XdTreeNode<ClassFrame> classes, 
			XdTreeNode<ObjectPropertyFrame> objectProperties,
			XdTreeNode<DataPropertyFrame> dataProperties) {
		// Begin by clearing out existing entity tree
		this.clearEntityTree();
		
		// Render classes - ignore top node OWL:Thing which we don't want to display
		for (XdTreeNode<ClassFrame> subClass: classes.getChildren()) {
			recursivelyAddClassNode(subClass, rootNode);
		}
		
		// Render object properties - ignore top level OWL:TopObjectProperty that we don't want to display
		for (XdTreeNode<ObjectPropertyFrame> subObjectProperty: objectProperties.getChildren()) {
			recursivelyAddObjectPropertyNode(subObjectProperty, rootNode);
		}
		
		// Render data properties - ignore top level OWL:TopDataProperty that we don't want to display
		for (XdTreeNode<DataPropertyFrame> subDataProperty: dataProperties.getChildren()) {
			recursivelyAddDataPropertyNode(subDataProperty, rootNode);
		}
		
		entityTreePanel.expandAll();
	}
	
	/**
	 * Recursively creates and adds ClassTreeNodes to the entityTreePanel.
	 * @param subClass
	 * @param rootNode
	 */
	private void recursivelyAddClassNode(XdTreeNode<ClassFrame> childNodeFromServer, TreeNode parentNode) {
		ClassFrame childClassFrame = childNodeFromServer.getData();
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
		for (XdTreeNode<ClassFrame> nextChild: childNodeFromServer.getChildren()) {
			recursivelyAddClassNode(nextChild, newChildNode);
		}
	}

	/**
	 * Recursively creates and adds ObjectPropertyTreeNodes to the entityTreePanel.
	 * @param subClass
	 * @param rootNode
	 */
	private void recursivelyAddObjectPropertyNode(XdTreeNode<ObjectPropertyFrame> childNodeFromServer, TreeNode parentNode) {
		ObjectPropertyFrame childObjectPropertyFrame = childNodeFromServer.getData();
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
		for (XdTreeNode<ObjectPropertyFrame> nextChild: childNodeFromServer.getChildren()) {
			recursivelyAddObjectPropertyNode(nextChild, newChildNode);
		}
	}
	
	/**
	 * Recursively creates and adds DataPropertyTreeNodes to the entityTreePanel.
	 * @param subClass
	 * @param rootNode
	 */
	private void recursivelyAddDataPropertyNode(XdTreeNode<DataPropertyFrame> childNodeFromServer, TreeNode parentNode) {
		DataPropertyFrame childDataPropertyFrame = childNodeFromServer.getData();
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
		for (XdTreeNode<DataPropertyFrame> nextChild: childNodeFromServer.getChildren()) {
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
	
	public XdTreeNode<ClassFrame>[] getSpecializedClasses() {
		// TODO: Implement this, too tired to continue coding right now
		for (Node childNode: rootNode.getChildNodes()) {
			TreeNode treeNode = (TreeNode)childNode;
			if (treeNode.getAttribute("type").equalsIgnoreCase("owlClassTreeNode")) {
				
			}
		}
		// TODO: Implement this
		return null;
	}
	
	
	private Set<TreeNode> getChildNodes(TreeNode parentNode) {
		HashSet<TreeNode> children = new HashSet<TreeNode>();
		children.add(parentNode);
		for (final Node childNode: parentNode.getChildNodes()) {
			children.addAll(getChildNodes((TreeNode)childNode));
		}
		return children;
	}
	
	public XdTreeNode<ObjectPropertyFrame>[] getSpecializedObjectProperties() {
		// TODO: Implement this
		return null;
	}
	
	public XdTreeNode<DataPropertyFrame>[] getSpecializedDataProperties() {
		// TODO: Implement this
		return null;
	}
}
