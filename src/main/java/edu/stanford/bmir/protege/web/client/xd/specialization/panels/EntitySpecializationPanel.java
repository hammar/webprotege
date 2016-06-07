package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.stanford.bmir.protege.web.client.xd.specialization.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.client.xd.specialization.widgets.EntityTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class EntitySpecializationPanel extends VerticalPanel implements InstantiationWizardPanel {
	
	private Button specializeButton;
	private Button modifyButton;
	private Button deleteButton;
	private Tree entityTree;
	
	public EntitySpecializationPanel(DesignPatternInstantiationWizard parentWizard) {
		this.parentWizard = parentWizard;
		this.setSpacing(10);
		
		this.setTitle("Entity Specialization");
        Label instruction = new Label("Please select the classes, object properties, and datatype properties from "
        		+ "the ontology design pattern that you wish to specialize for your modeling need from the list "
        		+ "below.");
		this.add(instruction);
		
		// Toolbar by which we interact with tree
		HorizontalPanel editingToolbar = new HorizontalPanel();
		editingToolbar.addStyleName("entitySpecializationToolbar");
		HorizontalPanel innerEditingToolbar = new HorizontalPanel();
		innerEditingToolbar.setSpacing(5);
		specializeButton = new Button("Specialise");
		modifyButton = new Button("Modify");
		deleteButton = new Button("Delete");
		innerEditingToolbar.add(specializeButton);
		innerEditingToolbar.add(modifyButton);
		innerEditingToolbar.add(deleteButton);
		editingToolbar.add(innerEditingToolbar);
		this.add(editingToolbar);
		
		// The tree itself
		entityTree = new Tree();
		entityTree.addStyleName("entityTree");
		entityTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				OntologyEntityFrame oef = (OntologyEntityFrame)event.getSelectedItem().getUserObject();
				if (oef.getIri().isPresent()) {
					modifyButton.setEnabled(false);
					deleteButton.setEnabled(false);
	        	}
	        	else {
	        		modifyButton.setEnabled(true);
					deleteButton.setEnabled(true);
				}
			}
		});
		TreeItem root = new TreeItem();
	    root.setText("Placeholder");
	    entityTree.addItem(root);
		this.add(entityTree);
	}

	private DesignPatternInstantiationWizard parentWizard;

	@Override
	public void renderPanel() {
		// Clear out existing tree
		entityTree.clear();
		
		// Render classes, object properties, and datatype properties
		renderEntityTree(parentWizard.getOdpClasses());
		renderEntityTree(parentWizard.getOdpObjectProperties());
		renderEntityTree(parentWizard.getOdpDataProperties());
	}
	
	private void renderEntityTree(FrameTreeNode<OntologyEntityFrame> inputFrameTree) {
		TreeItem rootItem = new TreeItem(new EntityTreeNode(inputFrameTree.getData()));
		for (FrameTreeNode<OntologyEntityFrame> childFrame: inputFrameTree.getChildren()) {
			recursivelyAddEntityNode(childFrame, rootItem);
		}
		entityTree.addItem(rootItem);
		rootItem.setState(true);
	}
	
	private void recursivelyAddEntityNode(FrameTreeNode<OntologyEntityFrame> childNodeFromServer, TreeItem parentNode) {
		OntologyEntityFrame childFrame = childNodeFromServer.getData();
		TreeItem newChildNode = new TreeItem(new EntityTreeNode(childFrame));
		newChildNode.setUserObject(childFrame);
		parentNode.addItem(newChildNode);
		parentNode.setState(true);
		for (FrameTreeNode<OntologyEntityFrame> nextChild: childNodeFromServer.getChildren()) {
			recursivelyAddEntityNode(nextChild, newChildNode);
		}
	}
	
}
