package edu.stanford.bmir.protege.web.shared.xd.actions;

import edu.stanford.bmir.protege.web.shared.HasProjectId;
import edu.stanford.bmir.protege.web.shared.dispatch.Action;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpInstantiation;
import edu.stanford.bmir.protege.web.shared.xd.results.PersistInstantiationResult;

public class PersistInstantiationAction implements Action<PersistInstantiationResult>, HasProjectId {

	/**
	 * No-arg constructor, for GWT-RPC serialization purpose only
	 */
	@SuppressWarnings("unused")
	private PersistInstantiationAction() {
	}
	
	/**
	 * Constructor
	 * @param odpUri
	 */
	public PersistInstantiationAction(OdpInstantiation odpInstantiation) {
		this.odpInstantiation = odpInstantiation;
	}
	
	private OdpInstantiation odpInstantiation;

	/**
	 * ODP Instantiation wrapper.
	 * @return
	 */
	public OdpInstantiation getOdpInstantiation() {
		return odpInstantiation;
	}

	@Override
	public ProjectId getProjectId() {
		return odpInstantiation.getProjectId();
	}
}
