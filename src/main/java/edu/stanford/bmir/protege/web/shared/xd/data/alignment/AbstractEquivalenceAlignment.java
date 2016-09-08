package edu.stanford.bmir.protege.web.shared.xd.data.alignment;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public abstract class AbstractEquivalenceAlignment implements Alignment {

	private static final long serialVersionUID = 4906645333610819096L;

	// The entity on which the equivalence will be asserted upon being persisted
	public abstract OntologyEntityFrame getHomeEntity();
	
	// The other entity with which the home entity is equivalent
	public abstract OntologyEntityFrame getOtherEntity();
	
	public String getSortableEntityLabel() {
		return getHomeEntity().getCurrentLabel(); 
	}
}
