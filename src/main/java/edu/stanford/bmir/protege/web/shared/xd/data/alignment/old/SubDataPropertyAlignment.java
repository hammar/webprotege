package edu.stanford.bmir.protege.web.shared.xd.data.alignment.old;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

@Deprecated
public class SubDataPropertyAlignment extends AbstractSubsumptionAlignment {
	
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
	
	public DataPropertyFrame getSuperEntity() {
		return superProperty;
	}

	public DataPropertyFrame getSubEntity() {
		return subProperty;
	}

	public String toString() {
		return (subProperty.getCurrentLabel() + " is a subdataproperty of " + superProperty.getCurrentLabel());
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
