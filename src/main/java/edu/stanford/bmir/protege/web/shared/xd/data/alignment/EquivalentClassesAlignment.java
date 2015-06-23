package edu.stanford.bmir.protege.web.shared.xd.data.alignment;

import com.gwtext.client.util.Format;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class EquivalentClassesAlignment extends AbstractEquivalenceAlignment {

	private static final long serialVersionUID = 1444993182680393709L;
	
	private ClassFrame homeClass;
	private ClassFrame otherClass;
	
	/**
	 * GWT-RPC constructor
	 */
	@SuppressWarnings("unused")
	private EquivalentClassesAlignment() {
		
	}
	
	public EquivalentClassesAlignment(ClassFrame homeClass, ClassFrame otherClass) {
		this.homeClass = homeClass;
		this.otherClass = otherClass;
	}
	
	@Override
	public OntologyEntityFrame getHomeEntity() {
		return homeClass;
	}

	@Override
	public OntologyEntityFrame getOtherEntity() {
		return otherClass;
	}
	
	public String toString() {
		return Format.format("{0} is a equivalent to {1}", homeClass.getLabel(), otherClass.getLabel());
	}

}
