package edu.stanford.bmir.protege.web.shared.xd.data.alignment;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public abstract class AbstractSubsumptionAlignment extends AbstractAlignment {
	
	/**
	 * GWT-RPC constructor
	 */
	protected AbstractSubsumptionAlignment() {
	}
	
	private static final long serialVersionUID = -8960834920811736266L;
	
	public AbstractSubsumptionAlignment(OntologyEntityFrame odpEntity, OntologyEntityFrame ontologyEntity) {
		super(odpEntity, ontologyEntity);
	}
}
