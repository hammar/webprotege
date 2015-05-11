package edu.stanford.bmir.protege.web.client.xd.specialization;

import com.gwtext.client.widgets.tree.TreeNode;

public class EditClassDetailsWindow extends ClassDetailsWindow {

	private TreeNode selectedClass;
	
	public EditClassDetailsWindow(XdSpecializationWizard parentWizard) {
		super(parentWizard);
	}
	
	public void loadClass() {
		selectedClass = parentWizard.getSelectedClass();
		this.className.setValue(selectedClass.getText());
		if (selectedClass.getAttribute("rdfsComment") != null) {
			this.classComment.setValue(selectedClass.getAttribute("rdfsComment"));
		}
	}
	
	public void persistAndClose() {
		selectedClass.setText(className.getValueAsString());
		if (classComment.getText().length() > 0) {
			selectedClass.setAttribute("rdfsComment", classComment.getText());
		}
		this.hide();
	}
}
