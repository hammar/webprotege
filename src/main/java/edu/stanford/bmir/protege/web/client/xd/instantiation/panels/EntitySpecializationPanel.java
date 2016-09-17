package edu.stanford.bmir.protege.web.client.xd.instantiation.panels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.stanford.bmir.protege.web.client.xd.instantiation.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.client.xd.instantiation.widgets.EntityTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class EntitySpecializationPanel extends VerticalPanel implements InstantiationWizardPanel {
	
	private final DesignPatternInstantiationWizard parentWizard;
	private Button specializeButton;
	private Button modifyButton;
	private Button deleteButton;
	private Tree entityTree;
	private TreeItem selectedTreeItem;
	
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
		specializeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String newEntityLabel = Window.prompt("Please enter the specialised entity label.", "");
				if (newEntityLabel != null && newEntityLabel != "") {
					OntologyEntityFrame parentFrame = (OntologyEntityFrame)selectedTreeItem.getUserObject();
					OntologyEntityFrame newFrame = createChildFrame(newEntityLabel, parentFrame);
					TreeItem newTreeItem = new TreeItem(new EntityTreeNode(newFrame));
					newTreeItem.setUserObject(newFrame);
					selectedTreeItem.addItem(newTreeItem);
					selectedTreeItem.setState(true);
					deleteButton.setEnabled(false);
					EntitySpecializationPanel.this.parentWizard.addSpecializedFrame(parentFrame, newFrame);
				}
			}

			private OntologyEntityFrame createChildFrame(String childFrameLabel, OntologyEntityFrame parentFrame) {
				if (parentFrame instanceof ClassFrame) {
					return new ClassFrame(childFrameLabel);
				}
				else if (parentFrame instanceof ObjectPropertyFrame) {
					return new ObjectPropertyFrame(childFrameLabel);
				}
				else if (parentFrame instanceof DataPropertyFrame) {
					return new DataPropertyFrame(childFrameLabel);
				}
				// The below should not occur
				return null;
			}
		});
		
		modifyButton = new Button("Modify");
		modifyButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String oldEntityLabel = selectedTreeItem.getText();
				String newEntityLabel = Window.prompt("Please enter the label ", oldEntityLabel);
				if (newEntityLabel != null && newEntityLabel != oldEntityLabel)  {
					((EntityTreeNode)selectedTreeItem.getWidget()).setLabelText(newEntityLabel);
					((OntologyEntityFrame)selectedTreeItem.getUserObject()).setLabel(newEntityLabel);
					// TODO: Check if any calls to parent wizard are needed. Probably not, same object in memory, right?
				}
			}
		});
		
		deleteButton = new Button("Delete");
		deleteButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Boolean confirmDelete = Window.confirm("Are you certain that you wish to delete \"" + selectedTreeItem.getText() + "\"?");
				if (confirmDelete) {
					OntologyEntityFrame frameToRemove = (OntologyEntityFrame)selectedTreeItem.getUserObject();
					EntitySpecializationPanel.this.parentWizard.removeSpecializedFrame(frameToRemove);
					entityTree.setSelectedItem(null);
					specializeButton.setEnabled(false);
					modifyButton.setEnabled(false);
					deleteButton.setEnabled(false);
					selectedTreeItem.remove();
					selectedTreeItem = null;
				}
			}
		});
		
		specializeButton.setEnabled(false);
		modifyButton.setEnabled(false);
		deleteButton.setEnabled(false);
		innerEditingToolbar.add(specializeButton);
		innerEditingToolbar.add(modifyButton);
		innerEditingToolbar.add(deleteButton);
		editingToolbar.add(innerEditingToolbar);
		this.add(editingToolbar);
		
		// The tree itself
		ScrollPanel sp = new ScrollPanel();
		sp.addStyleName("entityTreeScrollWrapper");
		entityTree = new Tree();
		entityTree.addStyleName("entityTree");
		entityTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				selectedTreeItem = event.getSelectedItem();
				specializeButton.setEnabled(true);
				OntologyEntityFrame oef = (OntologyEntityFrame)event.getSelectedItem().getUserObject();
				if (oef.getIri().isPresent()) {
					modifyButton.setEnabled(false);
					deleteButton.setEnabled(false);
	        	}
	        	else {
	        		modifyButton.setEnabled(true);
	        		if (selectedTreeItem.getChildCount() == 0) {
	        			deleteButton.setEnabled(true);
	        		}
	        		else {
	        			deleteButton.setEnabled(false);
	        		}
				}
			}
		});
		TreeItem root = new TreeItem();
	    root.setText("Placeholder");
	    entityTree.addItem(root);
	    sp.add(entityTree);
		this.add(sp);
	}

	@Override
	public void renderPanel() {
		// Clear out existing tree
		entityTree.clear();
		
		// Render classes, object properties, and datatype properties
		renderEntityTree(parentWizard.getClassTree());
		renderEntityTree(parentWizard.getObjectPropertyTree());
		renderEntityTree(parentWizard.getDataPropertyTree());
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
