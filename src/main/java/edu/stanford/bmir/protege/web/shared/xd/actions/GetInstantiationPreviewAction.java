package edu.stanford.bmir.protege.web.shared.xd.actions;

import edu.stanford.bmir.protege.web.shared.HasProjectId;
import edu.stanford.bmir.protege.web.shared.dispatch.Action;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.xd.data.CodpInstantiation;
import edu.stanford.bmir.protege.web.shared.xd.results.GetInstantiationPreviewResult;

public class GetInstantiationPreviewAction implements Action<GetInstantiationPreviewResult>, HasProjectId {

	/**
	 * No-arg constructor, for GWT-RPC serialization purpose only
	 */
	@SuppressWarnings("unused")
	private GetInstantiationPreviewAction() {
	}
	
	/**
	 * Constructor
	 * @param odpUri
	 */
	public GetInstantiationPreviewAction(CodpInstantiation codpInstantiation) {
		this.codpInstantiation = codpInstantiation;
	}
	
	private CodpInstantiation codpInstantiation;

	/**
	 * ODP Specialization wrapper.
	 * @return
	 */
	public CodpInstantiation getOdpSpecialization() {
		return codpInstantiation;
	}

	@Override
	public ProjectId getProjectId() {
		return codpInstantiation.getProjectId();
	}
}
