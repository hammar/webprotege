package edu.stanford.bmir.protege.web.shared.xd.actions;

import edu.stanford.bmir.protege.web.shared.HasProjectId;
import edu.stanford.bmir.protege.web.shared.dispatch.Action;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.xd.data.CodpInstantiation;
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
	public PersistInstantiationAction(CodpInstantiation codpInstantiation) {
		this.codpInstantiation = codpInstantiation;
	}
	
	private CodpInstantiation codpInstantiation;

	/**
	 * ODP Instantiation wrapper.
	 * @return
	 */
	public CodpInstantiation getOdpInstantiation() {
		return codpInstantiation;
	}

	@Override
	public ProjectId getProjectId() {
		return codpInstantiation.getProjectId();
	}
}
