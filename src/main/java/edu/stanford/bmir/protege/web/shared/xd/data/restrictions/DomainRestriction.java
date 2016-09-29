package edu.stanford.bmir.protege.web.shared.xd.data.restrictions;

import com.gwtext.client.util.Format;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.PropertyFrame;

public class DomainRestriction implements Restriction {

	private static final long serialVersionUID = -4110533737946747499L;

	// GWT-RPC empty constructor
	protected DomainRestriction() {
	}
	
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

	@Override
	public String toHtmlString() {
		return Format.format("The property <em>{0}</em> is only applicable to members of <em>{1}</em>", property.toString(), domain.toString());
	}
}
