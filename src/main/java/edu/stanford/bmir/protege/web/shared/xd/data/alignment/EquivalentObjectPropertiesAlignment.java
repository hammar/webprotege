package edu.stanford.bmir.protege.web.shared.xd.data.alignment;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class EquivalentObjectPropertiesAlignment extends AbstractEquivalenceAlignment {

	private static final long serialVersionUID = -7294572673114965160L;
	
	private ObjectPropertyFrame homeProperty;
	private ObjectPropertyFrame otherProperty;
	
	/**
	 * GWT-RPC constructor
	 */
	@SuppressWarnings("unused")
	private EquivalentObjectPropertiesAlignment() {
		
	}
	
	public EquivalentObjectPropertiesAlignment(ObjectPropertyFrame homeClass, ObjectPropertyFrame otherClass) {
		this.homeProperty = homeClass;
		this.otherProperty = otherClass;
	}
	
	@Override
	public OntologyEntityFrame getHomeEntity() {
		return homeProperty;
	}

	@Override
	public OntologyEntityFrame getOtherEntity() {
		return otherProperty;
	}
	
	public String toString() {
		return (homeProperty.getCurrentLabel() + " is equivalent to " + otherProperty.getCurrentLabel());
	}

}
