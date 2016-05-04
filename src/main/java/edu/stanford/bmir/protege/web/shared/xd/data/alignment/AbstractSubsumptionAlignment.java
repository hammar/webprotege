package edu.stanford.bmir.protege.web.shared.xd.data.alignment;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public abstract class AbstractSubsumptionAlignment implements Alignment {

	private static final long serialVersionUID = 6743895686685412565L;

	public abstract OntologyEntityFrame getSuperEntity();
	public abstract OntologyEntityFrame getSubEntity();
	
	public String getSortableEntityLabel() {
		return getSubEntity().getLabel(); 
	}
}
