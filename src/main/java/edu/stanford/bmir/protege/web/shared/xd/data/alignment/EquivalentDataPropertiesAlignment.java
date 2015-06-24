package edu.stanford.bmir.protege.web.shared.xd.data.alignment;

import com.gwtext.client.util.Format;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class EquivalentDataPropertiesAlignment extends AbstractEquivalenceAlignment {

	private static final long serialVersionUID = 2019720192826227962L;
	
	private DataPropertyFrame homeProperty;
	private DataPropertyFrame otherProperty;
	
	/**
	 * GWT-RPC constructor
	 */
	@SuppressWarnings("unused")
	private EquivalentDataPropertiesAlignment() {
		
	}
	
	public EquivalentDataPropertiesAlignment(DataPropertyFrame homeClass, DataPropertyFrame otherClass) {
		this.homeProperty = homeClass;
		this.otherProperty = otherClass;
	}
	
	@Override
	public OntologyEntityFrame getHomeEntity() {
		return homeProperty;
	}

	@Override
	public OntologyEntityFrame getOtherEntity() {
		return otherProperty;
	}
	
	public String toString() {
		return Format.format("\"{0}\" is equivalent to \"{1}\"", homeProperty.getLabel(), otherProperty.getLabel());
	}

}
