package edu.stanford.bmir.protege.web.shared.xd.data.alignment;

import com.gwtext.client.util.Format;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;

public class SubObjectPropertyAlignment implements Alignment {
	
	private static final long serialVersionUID = 8496915564455099583L;
	
	private ObjectPropertyFrame superProperty;
	private ObjectPropertyFrame subProperty;
	
	/**
	 * GWT-RPC constructor
	 */
	@SuppressWarnings("unused")
	private SubObjectPropertyAlignment() {
	}
	
	public SubObjectPropertyAlignment(ObjectPropertyFrame superProperty, ObjectPropertyFrame subProperty) {
		this.superProperty = superProperty;
		this.subProperty = subProperty;
	}
	
	public ObjectPropertyFrame getSuperProperty() {
		return superProperty;
	}

	public ObjectPropertyFrame getSubProperty() {
		return subProperty;
	}

	public String toString() {
		return Format.format("{0} is a subobjectproperty of {1}", subProperty.getLabel(), superProperty.getLabel());
	}

}
