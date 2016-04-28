package edu.stanford.bmir.protege.web.client.xd.specialization.old.restriction;

import com.gwtext.client.util.Format;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;

public class SubClassOfUniversalRestriction extends AbstractComplexRestriction {
	
	public SubClassOfUniversalRestriction(ObjectPropertyFrame property, ClassFrame source, ClassFrame destination) {
		super(property, source, destination);
	}

	public String toString() {
		return Format.format("If a \"{0}\" is connected to an entity via \"{1}\", then that entity must be a \"{2}\"", source.toString(), property.toString(), destination.toString());
	}
}
