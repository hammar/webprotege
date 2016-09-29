package edu.stanford.bmir.protege.web.shared.xd.data.alignment;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class CodpAsSubEntityAlignment extends AbstractSubsumptionAlignment {
	
	private static final long serialVersionUID = 3285249579317365532L;

	/**
	 * GWT-RPC constructor
	 */
	@SuppressWarnings("unused")
	private CodpAsSubEntityAlignment() {
	}
	
	public CodpAsSubEntityAlignment(OntologyEntityFrame odpEntity, OntologyEntityFrame ontologyEntity) {
		super(odpEntity, ontologyEntity);
	}

	@Override
	public String getAlignmentDescription() {
		return "is a sub-entity of";
	}
}
