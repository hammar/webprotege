package edu.stanford.bmir.protege.web.shared.xd.data.alignment;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class CodpAsSuperEntityAlignment extends AbstractSubsumptionAlignment {
	
	/**
	 * GWT-RPC constructor
	 */
	@SuppressWarnings("unused")
	private CodpAsSuperEntityAlignment() {
	}
	
	private static final long serialVersionUID = -6227493652993412167L;

	public CodpAsSuperEntityAlignment(OntologyEntityFrame odpEntity, OntologyEntityFrame ontologyEntity) {
		super(odpEntity, ontologyEntity);
	}

	@Override
	public String getAlignmentDescription() {
		return "is a super-entity of";
	}
}
