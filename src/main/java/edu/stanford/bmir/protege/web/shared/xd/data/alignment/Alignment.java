package edu.stanford.bmir.protege.web.shared.xd.data.alignment;

import java.io.Serializable;

import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public interface Alignment extends Serializable {
	public OntologyEntityFrame getInstantiationEntityFrame();
	public OntologyEntityFrame getOntologyEntityFrame();
	public String getAlignmentDescription();
	public String toString();
}
