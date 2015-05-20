package edu.stanford.bmir.protege.web.client.xd.specialization;

import com.gwtext.client.data.SimpleStore;

public abstract class ObjectPropertyDetailsWindow extends PropertyDetailsWindow {

	public ObjectPropertyDetailsWindow(XdSpecializationWizard parentWizard) {
		super(parentWizard);
	}

	// TODO: Fix below to actually return classes in ontology
	@Override
	protected String[][] getRanges() {
		return new String[][]{
				new String[]{"TestRange", "http://ontologydesignpatterns.orgg/wiki/Community:ANY"},  
				new String[]{"TestRange2", "http://ontologydesignpatterns.org/wiki/Community:Academy"},
				new String[]{"TestRange3", "http://ontologydesignpatterns.org/wiki/Community:Agriculture"},
				new String[]{"TestRange4", "http://ontologydesignpatterns.org/wiki/Community:Biology"},
				new String[]{"TestRange5", "http://ontologydesignpatterns.org/wiki/Community:Business"}
		};
	}
	
	
}
