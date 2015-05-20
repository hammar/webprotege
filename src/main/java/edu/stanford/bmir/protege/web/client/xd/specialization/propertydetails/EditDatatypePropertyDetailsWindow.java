package edu.stanford.bmir.protege.web.client.xd.specialization.propertydetails;

import com.gwtext.client.widgets.tree.TreeNode;

import edu.stanford.bmir.protege.web.client.xd.specialization.XdSpecializationWizard;

public class EditDatatypePropertyDetailsWindow extends
		DatatypePropertyDetailsWindow {

	private TreeNode selectedProperty;
	
	public EditDatatypePropertyDetailsWindow(XdSpecializationWizard parentWizard) {
		super(parentWizard);
	}

	public void loadProperty() {
		selectedProperty = parentWizard.getSelectedDatatypeProperty();
		this.propertyName.setValue(selectedProperty.getText());
		if (selectedProperty.getAttribute("rdfsComment") != null) {
			this.propertyComment.setValue(selectedProperty.getAttribute("rdfsComment"));
		}
		if (selectedProperty.getAttribute("rdfsDomain") != null) {
			propertyDomain.setValue(selectedProperty.getAttribute("rdfsDomain"));
		}
		if (selectedProperty.getAttribute("rdfsRange") != null) {
			propertyRange.setValue(selectedProperty.getAttribute("rdfsRange"));
		}
	}
	
	@Override
	public void persistAndClose() {
		selectedProperty.setText(propertyName.getValueAsString());
		if (propertyComment.getText().length() > 0) {
			selectedProperty.setAttribute("rdfsComment", propertyComment.getText());
		}
		if (propertyDomain.getValue() != null) {
			selectedProperty.setAttribute("rdfsDomain", propertyDomain.getValue());
		}
		if (propertyRange.getValue() != null) {
			selectedProperty.setAttribute("rdfsRange", propertyRange.getValue());
		}
		this.hide();
	}
}
