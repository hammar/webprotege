package edu.stanford.bmir.protege.web.shared.xd.data.alignment;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class EquivalenceAlignment extends AbstractAlignment {

	private static final long serialVersionUID = 2931022338738519548L;

	public EquivalenceAlignment(OntologyEntityFrame odpEntity, OntologyEntityFrame ontologyEntity) {
		super(odpEntity, ontologyEntity);
	}

	@Override
	public String getAlignmentDescription() {
		return "is equivalent to";
	}
}
