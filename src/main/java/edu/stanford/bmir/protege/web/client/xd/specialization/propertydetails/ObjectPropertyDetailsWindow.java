package edu.stanford.bmir.protege.web.client.xd.specialization.propertydetails;

import com.gwtext.client.data.SimpleStore;

import edu.stanford.bmir.protege.web.client.xd.specialization.XdSpecializationWizard;

public abstract class ObjectPropertyDetailsWindow extends PropertyDetailsWindow {

	public ObjectPropertyDetailsWindow(XdSpecializationWizard parentWizard) {
		super(parentWizard);
	}

	@Override
	protected String[] getRanges() {
		return parentWizard.getLeafClasses();
	}
}
