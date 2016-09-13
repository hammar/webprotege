package edu.stanford.bmir.protege.web.shared.xd.data.alignment.old;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

@Deprecated
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
		return (homeProperty.getCurrentLabel() + " is equivalent to " + otherProperty.getCurrentLabel());
	}

	@Override
	public OntologyEntityFrame getInstantiationEntityFrame() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyEntityFrame getOntologyEntityFrame() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAlignmentDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
