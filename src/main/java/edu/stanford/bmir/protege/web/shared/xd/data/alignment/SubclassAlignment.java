package edu.stanford.bmir.protege.web.shared.xd.data.alignment;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;

public class SubclassAlignment extends Alignment {
	
	private static final long serialVersionUID = 8496915564455099583L;
	
	private ClassFrame superClass;
	private ClassFrame subClass;
	
	public SubclassAlignment(ClassFrame superClass, ClassFrame subClass) {
		this.superClass = superClass;
		this.subClass = subClass;
	}
	
	public ClassFrame getSuperClass() {
		return superClass;
	}

	public ClassFrame getSubClass() {
		return subClass;
	}

	@Override
	public String toString() {
		return String.format("%s is a subclass of %s", subClass.getLabel(), superClass.getLabel());
	}

}
