package edu.stanford.bmir.protege.web.shared.xd.data.alignment;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;

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
		return (subClass.getLabel() + " is a subclass of " + superClass.getLabel());
	}

}
