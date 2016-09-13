package edu.stanford.bmir.protege.web.shared.xd.data.alignment.old;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

@Deprecated
public class SubClassAlignment extends AbstractSubsumptionAlignment {
	
	private static final long serialVersionUID = 8496915564455099583L;
	
	private ClassFrame superClass;
	private ClassFrame subClass;
	
	/**
	 * GWT-RPC constructor
	 */
	@SuppressWarnings("unused")
	private SubClassAlignment() {
	}
	
	public SubClassAlignment(ClassFrame superClass, ClassFrame subClass) {
		this.superClass = superClass;
		this.subClass = subClass;
	}
	
	public ClassFrame getSuperEntity() {
		return superClass;
	}

	public ClassFrame getSubEntity() {
		return subClass;
	}

	public String toString() {
		return (subClass.getCurrentLabel() + " is a subclass of " + superClass.getCurrentLabel());
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
