package edu.stanford.bmir.protege.web.client.xd.specialization.old.restriction;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;

public abstract class AbstractComplexRestriction implements Restriction {

	protected ObjectPropertyFrame property;
	protected ClassFrame source;
	protected ClassFrame destination;
	
	protected AbstractComplexRestriction(ObjectPropertyFrame property, ClassFrame source, ClassFrame destination) {
		this.property = property;
		this.source = source;
		this.destination = destination;
	}
	
	public abstract String toString();
	
	@Override
	public String getEntityLabel() {
		return property.getLabel();
	}

	public ObjectPropertyFrame getProperty() {
		return property;
	}

	public ClassFrame getSource() {
		return source;
	}

	public ClassFrame getDestination() {
		return destination;
	}
}
