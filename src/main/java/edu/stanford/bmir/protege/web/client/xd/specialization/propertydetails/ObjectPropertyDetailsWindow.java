package edu.stanford.bmir.protege.web.client.xd.specialization.propertydetails;

import edu.stanford.bmir.protege.web.client.xd.specialization.XdSpecializationWizard;

public abstract class ObjectPropertyDetailsWindow extends PropertyDetailsWindow {

	public ObjectPropertyDetailsWindow(XdSpecializationWizard parentWizard) {
		super(parentWizard);
	}

	@Override
	protected String[][] getRanges() {
		String[] classLabels = parentWizard.getDisplayedClassLabels();
		String[][] retVal = new String[classLabels.length][2];
		for (int i = 0; i< classLabels.length; i++) {
			String label = classLabels[i];
			retVal[i][0] = label;
			// Setting IRI empty as the range classes may not have been created yet and thus
			// don't have IRIs.
			retVal[i][1] = "";
		}
		return retVal;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		propertyRange.setValueField("label");
	}
}
