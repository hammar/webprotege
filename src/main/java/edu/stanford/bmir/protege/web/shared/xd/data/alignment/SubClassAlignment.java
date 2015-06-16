package edu.stanford.bmir.protege.web.shared.xd.data.alignment;

import com.gwtext.client.util.Format;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;

public class SubClassAlignment implements Alignment {
	
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
	
	public ClassFrame getSuperClass() {
		return superClass;
	}

	public ClassFrame getSubClass() {
		return subClass;
	}

	public String toString() {
		return Format.format("{0} is a subclass of {1}", subClass.getLabel(), superClass.getLabel());
	}

}
