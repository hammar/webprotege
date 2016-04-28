package edu.stanford.bmir.protege.web.client.xd.specialization.old.restriction;

import com.gwtext.client.util.Format;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;

public class EquivalentToExistentialRestriction extends AbstractComplexRestriction {

	public EquivalentToExistentialRestriction(ObjectPropertyFrame property, ClassFrame source, ClassFrame destination) {
		super(property, source, destination);
	}

	public String toString() {
		return Format.format("If an entity is connected via \"{0}\" to a \"{1}\" then that entity must be a \"{2}\"", property.toString(), destination.toString(), source.toString());
	}
}
