package edu.stanford.bmir.protege.web.client.xd.specialization;

import com.google.common.base.Optional;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.tree.TreeNode;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.AbstractOntologyEntityFrame;

public class EntityDetailsWindow extends Window {

	private Optional<AbstractOntologyEntityFrame> frame;
	private TextField labelField;
	private TextField commentField;
	private TreeNode parentTreeNode;
	private TreeNode currentNode;

	public EntityDetailsWindow() {
		this.setCloseAction(HIDE);
		this.setTitle("Specialised Entity Details");
		this.setLayout(new FitLayout());
		this.setWidth(335);
		this.setHeight(200);
		
		FormPanel formPanel = new FormPanel();
		formPanel.setPaddings(5);
		
		formPanel.setBorder(true);
		labelField = new TextField("Class name", "className");
		labelField.setWidth(180);
		labelField.setAllowBlank(false);
		formPanel.add(labelField);  
		commentField = new TextArea("Comment", "classComment");
		commentField.setWidth(180);
		commentField.setHeight(100);
		commentField.setAllowBlank(true);
		formPanel.add(commentField);
		
		Toolbar submitCloseToolbar = new Toolbar();
		ToolbarButton closeButton = new ToolbarButton("Cancel");
		closeButton.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(Button button, EventObject e) {
				hide();
			}
		});
		submitCloseToolbar.addButton(closeButton);
		submitCloseToolbar.addFill();
		ToolbarButton submitButton = new ToolbarButton("Submit");
		submitButton.addListener(new ButtonListenerAdapter(){
			@Override
			public void onClick(Button button, EventObject e) {
				persistAndClose();
			}
		});
		submitCloseToolbar.addButton(submitButton);

		formPanel.setBottomToolbar(submitCloseToolbar);
		this.add(formPanel);
	}
	
	// Resets this window to empty state
	public void reset() {
		this.parentTreeNode = null;
		this.currentNode = null;
		this.frame = Optional.absent();
		this.labelField.setValue("");
		this.commentField.setValue("");
	}
	
	// I.e., persist to parent specialization wizard, not to actual ontology!
	public void persistAndClose() {
		
		// If the frame exists already, then we are editing it. Simply update
		// the fields.
		if (this.frame.isPresent()) {
			AbstractOntologyEntityFrame frame = this.frame.get();
			frame.setLabel(labelField.getValueAsString());
			frame.setComment(commentField.getValueAsString());
			this.currentNode.setText(labelField.getValueAsString());
		}
		// Otherwise, we need to create the frame and wire it up against a new
		// OntologyEntityTreeNode in the parent SpecializationWizard
		else {
			// For some reason parentTreeNode.getIconCls() always returns null, so 
			// we also store icon in attribute when creating them.
			String nodeType = parentTreeNode.getAttribute("type");
			TreeNode newTreeNode = new TreeNode(labelField.getValueAsString(), nodeType);
			AbstractOntologyEntityFrame newFrame;
			if (nodeType.equalsIgnoreCase("owlClassTreeNode")) {
				newFrame = new ClassFrame(labelField.getValueAsString());
			}
			else if (nodeType.equalsIgnoreCase("owlDataPropertyTreeNode")) {
				newFrame = new DataPropertyFrame(labelField.getValueAsString());
			}
			else {
				newFrame = new ObjectPropertyFrame(labelField.getValueAsString());
			}
			if(commentField.getValueAsString() != "") {
				newFrame.setComment(commentField.getValueAsString());
			}
			newTreeNode.setCls("specializedNode");
			newTreeNode.setAttribute("frame", newFrame);
			newTreeNode.setAttribute("type", nodeType);
			parentTreeNode.appendChild(newTreeNode);
			parentTreeNode.expand();
		}
		
		this.hide();
	}
	
	public void show() {
		// Overridden and deactivated - show should never be run on its own for this window!
	}
	
	public void newFrameAndShow(TreeNode parentTreeNode) {
		this.reset();
		this.parentTreeNode = parentTreeNode;
		super.show();
	}
	
	// Load the existing node details
	public void loadFrameAndShow(TreeNode currentNode) {
		this.reset();
		this.currentNode = currentNode;
		this.frame = Optional.of((AbstractOntologyEntityFrame)currentNode.getAttributeAsObject("frame"));
		labelField.setValue(frame.get().getLabel());
		commentField.setValue(frame.get().getComment().or(""));
		super.show();
	}
}