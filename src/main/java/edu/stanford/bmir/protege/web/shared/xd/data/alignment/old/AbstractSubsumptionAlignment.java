package edu.stanford.bmir.protege.web.shared.xd.data.alignment.old;

import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

@Deprecated
public abstract class AbstractSubsumptionAlignment implements Alignment {

	private static final long serialVersionUID = 6743895686685412565L;

	public abstract OntologyEntityFrame getSuperEntity();
	public abstract OntologyEntityFrame getSubEntity();
	
	public String getSortableEntityLabel() {
		return getSubEntity().getCurrentLabel(); 
	}
}
