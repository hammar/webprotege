package edu.stanford.bmir.protege.web.shared.xd.data.alignment;

import com.gwtext.client.util.Format;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;

public class SubDataPropertyAlignment implements Alignment {
	
	private static final long serialVersionUID = -2437051887984587228L;
	
	private DataPropertyFrame superProperty;
	private DataPropertyFrame subProperty;
	
	/**
	 * GWT-RPC constructor
	 */
	@SuppressWarnings("unused")
	private SubDataPropertyAlignment() {
	}
	
	public SubDataPropertyAlignment(DataPropertyFrame superProperty, DataPropertyFrame subProperty) {
		this.superProperty = superProperty;
		this.subProperty = subProperty;
	}
	
	public DataPropertyFrame getSuperProperty() {
		return superProperty;
	}

	public DataPropertyFrame getSubProperty() {
		return subProperty;
	}

	public String toString() {
		return Format.format("{0} is a subdataproperty of {1}", subProperty.getLabel(), superProperty.getLabel());
	}

}
