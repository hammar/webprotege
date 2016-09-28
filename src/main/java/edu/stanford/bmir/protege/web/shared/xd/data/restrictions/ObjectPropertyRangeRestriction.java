package edu.stanford.bmir.protege.web.shared.xd.data.restrictions;

import com.gwtext.client.util.Format;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;

public class ObjectPropertyRangeRestriction implements Restriction {
	
	private static final long serialVersionUID = -3843719916794476020L;

	// GWT-RPC empty constructor
	protected ObjectPropertyRangeRestriction() {
	}
	
	public ObjectPropertyRangeRestriction(ObjectPropertyFrame property, ClassFrame range) {
		this.property = property;
		this.range = range;
	}
	
	private ObjectPropertyFrame property;
	private ClassFrame range;
	
	@Override
	public String toString() {
		return Format.format("The property \"{0}\" can only have members of \"{1}\" as values", property.toString(), range.toString());
	}

	@Override
	public String getEntityLabel() {
		return this.property.getLabel();
	}

	public ObjectPropertyFrame getProperty() {
		return property;
	}

	public ClassFrame getRange() {
		return range;
	}
}
