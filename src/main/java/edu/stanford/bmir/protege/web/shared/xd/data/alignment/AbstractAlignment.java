package edu.stanford.bmir.protege.web.shared.xd.data.alignment;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public abstract class AbstractAlignment implements Alignment {

	private static final long serialVersionUID = 7565760520197477716L;
	
	private OntologyEntityFrame instantiationEntityFrame;
	private OntologyEntityFrame ontologyEntityFrame;
	
	/**
	 * GWT-RPC constructor
	 */
	protected AbstractAlignment() {
	}
	
	public AbstractAlignment(OntologyEntityFrame odpEntity, OntologyEntityFrame ontologyEntity) {
		this.instantiationEntityFrame = odpEntity;
		this.ontologyEntityFrame = ontologyEntity;
	}

	@Override
	public OntologyEntityFrame getInstantiationEntityFrame() {
		return this.instantiationEntityFrame;
	}

	@Override
	public OntologyEntityFrame getOntologyEntityFrame() {
		return this.ontologyEntityFrame;
	}
	
	public String toString() {
		return this.getInstantiationEntityFrame().getCurrentLabel() + " " + this.getAlignmentDescription() + " " + this.getOntologyEntityFrame().getCurrentLabel();
	}
}
