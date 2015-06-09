package edu.stanford.bmir.protege.web.client.xd.specialization.restriction;

import com.gwtext.client.util.Format;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.PropertyFrame;

public class ObjectPropertyRangeRestriction implements Restriction {

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
}
