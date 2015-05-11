package edu.stanford.bmir.protege.web.client.xd.specialization;

import com.gwtext.client.widgets.tree.TreeNode;

public class NewClassDetailsWindow extends ClassDetailsWindow {

	public NewClassDetailsWindow(XdSpecializationWizard parentWizard) {
		super(parentWizard);
	}

	public void persistAndClose() {
		TreeNode selectedClass = parentWizard.getSelectedClass();
		TreeNode newClass = new TreeNode(className.getValueAsString());
		if (classComment.getText().length() > 0) {
			newClass.setAttribute("rdfsComment", classComment.getText());
		}
		selectedClass.appendChild(newClass);
		this.hide();
	}
}
