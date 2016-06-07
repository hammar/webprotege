package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.stanford.bmir.protege.web.client.xd.specialization.DesignPatternInstantiationWizard;
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
		
		// TODO: implement real data loading here
		entityTree = new Tree();
		entityTree.addStyleName("entityTree");
		TreeItem root = new TreeItem();
	    root.setText("root");
	    root.addTextItem("item0");
	    root.addTextItem("item1");
	    root.addTextItem("item2");
	    TreeItem item = new TreeItem(new CheckBox("item3"));
	    root.addItem(item);
	    entityTree.addItem(root);
	    root.setState(true);
		this.add(entityTree);
	}

	private DesignPatternInstantiationWizard parentWizard;

	@Override
	public void renderPanel() {
		// TODO: Build using cell tree instead?
		
		// Clear out existing tree
		entityTree.clear();
		
		// Render classes
		FrameTreeNode<OntologyEntityFrame> classTree = parentWizard.getOdpClasses();
		TreeItem rootClass = new TreeItem();
		for (FrameTreeNode<OntologyEntityFrame> subClass: classTree.getChildren()) {
			recursivelyAddClassNode(subClass, rootClass);
		}
		entityTree.addItem(rootClass);
		rootClass.setState(true);
		
		// TODO: Render object properties and datatype properties
	}
	
	private void recursivelyAddClassNode(FrameTreeNode<OntologyEntityFrame> childNodeFromServer, TreeItem parentNode) {
		OntologyEntityFrame childClassFrame = childNodeFromServer.getData();
		TreeItem newChildNode = new TreeItem(new Label(childClassFrame.getLabel()));
		newChildNode.addStyleName("owlClassTreeNode");
		newChildNode.setUserObject(childClassFrame);
		parentNode.addItem(newChildNode);
		for (FrameTreeNode<OntologyEntityFrame> nextChild: childNodeFromServer.getChildren()) {
			recursivelyAddClassNode(nextChild, newChildNode);
		}
	}
	
}
