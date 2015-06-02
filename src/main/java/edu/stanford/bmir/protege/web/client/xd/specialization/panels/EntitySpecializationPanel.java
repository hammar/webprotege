package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.RowLayout;
import com.gwtext.client.widgets.layout.RowLayoutData;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

import edu.stanford.bmir.protege.web.client.xd.specialization.EntityDetailsWindow;
import edu.stanford.bmir.protege.web.client.xd.specialization.treenodes.ClassTreeNode;
import edu.stanford.bmir.protege.web.client.xd.specialization.treenodes.DataPropertyTreeNode;
import edu.stanford.bmir.protege.web.client.xd.specialization.treenodes.ObjectPropertyTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.XdTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;

public class EntitySpecializationPanel extends Panel {

	private TreePanel entityTreePanel;
	private EntityDetailsWindow edWindow;
	private TreeNode rootNode;
	
	private Button specializeButton;
	private Button modifyButton;
	private Button deleteButton;
	
	public EntitySpecializationPanel() {
		super();
		
		this.edWindow = new EntityDetailsWindow();
		
		this.setLayout(new RowLayout());  
        this.setId("card-1");
        this.setTitle("Entity Specialization");
        
        Panel instructionPanel = new Panel();
        instructionPanel.setPaddings(10);
        Label instruction = new Label("Please select the classes, object properties, and datatype properties from "
        		+ "the ontology design pattern that you wish to specialize for your modeling need from the list "
        		+ "below.");
        instructionPanel.add(instruction);
        instructionPanel.setBodyStyle("border-bottom: 1px solid #99bbe8; padding: 15px;");
        this.add(instructionPanel, new RowLayoutData(50));
        
        entityTreePanel = new TreePanel();
        entityTreePanel.addListener(new TreePanelListenerAdapter() {
        	public void onClick(TreeNode node, EventObject e) {
        		// The below if blocks are because GWT does not seem to play nicely with casting
        		// TreeNode to joiny OntologyEntityTreeNode superclass :-(
        		boolean hasIri = true;
        		if (node instanceof ClassTreeNode) {
        			hasIri = ((ClassTreeNode)node).hasIri();
        		}
        		if (node instanceof ObjectPropertyTreeNode) {
        			hasIri = ((ObjectPropertyTreeNode)node).hasIri();
        		}
        		if (node instanceof DataPropertyTreeNode) {
        			hasIri = ((DataPropertyTreeNode)node).hasIri();
        		}
        		if (hasIri) {
        			modifyButton.setDisabled(true);
        			deleteButton.setDisabled(true);
        		}
        		else {
        			modifyButton.setDisabled(false);
        			deleteButton.setDisabled(false);
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
        // TODO: Add button listeners and wire up functionality of the below buttons
        // to show edWindow.
        specializeButton = new Button("Specialize");
        modifyButton = new Button("Modify");
        deleteButton = new Button("Delete");
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
		ClassTreeNode newChildNode = new ClassTreeNode(childClassFrame.getLabel(), childClassFrame.getIri());
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
		ObjectPropertyTreeNode newChildNode = new ObjectPropertyTreeNode(childObjectPropertyFrame.getLabel(), childObjectPropertyFrame.getIri());
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
		DataPropertyTreeNode newChildNode = new DataPropertyTreeNode(childDataPropertyFrame.getLabel(), childDataPropertyFrame.getIri());
		parentNode.appendChild(newChildNode);
		for (XdTreeNode<DataPropertyFrame> nextChild: childNodeFromServer.getChildren()) {
			recursivelyAddDataPropertyNode(nextChild, newChildNode);
		}
	}
	
	public XdTreeNode<ClassFrame>[] getSpecializedClasses() {
		// TODO: Implement this
		return null;
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
