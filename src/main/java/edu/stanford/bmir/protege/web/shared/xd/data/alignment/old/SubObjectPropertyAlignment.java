package edu.stanford.bmir.protege.web.shared.xd.data.alignment.old;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

@Deprecated
public class SubObjectPropertyAlignment extends AbstractSubsumptionAlignment {
	
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
	
	public ObjectPropertyFrame getSuperEntity() {
		return superProperty;
	}

	public ObjectPropertyFrame getSubEntity() {
		return subProperty;
	}

	public String toString() {
		return (subProperty.getCurrentLabel() + " is a subobjectproperty of " + superProperty.getCurrentLabel());
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
