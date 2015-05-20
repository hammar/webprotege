package edu.stanford.bmir.protege.web.client.xd.specialization.propertydetails;

import com.gwtext.client.widgets.tree.TreeNode;

import edu.stanford.bmir.protege.web.client.xd.specialization.XdSpecializationWizard;

public class NewObjectPropertyDetailsWindow extends ObjectPropertyDetailsWindow {

	public NewObjectPropertyDetailsWindow(XdSpecializationWizard parentWizard) {
		super(parentWizard);
	}

	@Override
	public void persistAndClose() {
		TreeNode selectedProperty = parentWizard.getSelectedObjectProperty();
		TreeNode newProperty = new TreeNode(propertyName.getValueAsString());
		if (propertyComment.getText().length() > 0) {
			newProperty.setAttribute("rdfsComment", propertyComment.getText());
		}
		if (propertyDomain.getValue() != null) {
			newProperty.setAttribute("rdfsDomain", propertyDomain.getValue());
		}
		if (propertyRange.getValue() != null) {
			newProperty.setAttribute("rdfsRange", propertyRange.getValue());
		}
		selectedProperty.appendChild(newProperty);
		this.hide();
	}
}
