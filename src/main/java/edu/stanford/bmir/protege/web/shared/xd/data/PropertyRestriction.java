package edu.stanford.bmir.protege.web.shared.xd.data;

import java.io.Serializable;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;

public class PropertyRestriction implements Serializable {
	
	private static final long serialVersionUID = -1977994232854028764L;
	
	private ObjectPropertyFrame property;
	private ClassFrame target;
	private ValueConstraint valueConstraint;
	
	/**
	 * GWT-RPC no-arg constructor
	 */
	@SuppressWarnings("unused")
	private PropertyRestriction() {
	}
	
	public PropertyRestriction(ObjectPropertyFrame property, ClassFrame target, ValueConstraint valueConstraint) {
		this.property = property;
		this.target = target;
		this.valueConstraint = valueConstraint;
	}
	
	public ObjectPropertyFrame getProperty() {
		return property;
	}

	public ClassFrame getTarget() {
		return target;
	}

	public ValueConstraint getValueConstraint() {
		return valueConstraint;
	}

	public enum ValueConstraint {
		SOME, ONLY
	}
}