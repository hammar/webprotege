package edu.stanford.bmir.protege.web.shared.xd.data.alignment;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public interface SubsumptionAlignment extends Alignment {
	public OntologyEntityFrame getSuperEntity();
	public OntologyEntityFrame getSubEntity();
}
