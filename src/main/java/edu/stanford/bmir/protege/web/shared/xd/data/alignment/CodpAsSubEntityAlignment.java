package edu.stanford.bmir.protege.web.shared.xd.data.alignment;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class CodpAsSubEntityAlignment extends SubsumptionAlignment {
	
	private static final long serialVersionUID = 3285249579317365532L;

	public CodpAsSubEntityAlignment(OntologyEntityFrame odpEntity, OntologyEntityFrame ontologyEntity) {
		super(odpEntity, ontologyEntity);
	}

	@Override
	public String getAlignmentDescription() {
		return "is a sub-entity of";
	}
}
