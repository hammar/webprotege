package edu.stanford.bmir.protege.web.client.xd.specialization.old.restriction;

import com.gwtext.client.util.Format;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.PropertyFrame;

public class DomainRestriction implements Restriction {

	public DomainRestriction(PropertyFrame property, ClassFrame domain) {
		this.property = property;
		this.domain = domain;
	}
	
	private PropertyFrame property;
	private ClassFrame domain;
	
	@Override
	public String toString() {
		return Format.format("The property \"{0}\" is only applicable to members of \"{1}\"", property.toString(), domain.toString());
	}

	@Override
	public String getEntityLabel() {
		return this.property.toString();
	}

	public PropertyFrame getProperty() {
		return property;
	}

	public ClassFrame getDomain() {
		return domain;
	}
}
