package edu.stanford.bmir.protege.web.shared.xd.actions;

import edu.stanford.bmir.protege.web.shared.HasProjectId;
import edu.stanford.bmir.protege.web.shared.dispatch.Action;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpInstantiation;
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
	public GetInstantiationPreviewAction(OdpInstantiation odpInstantiation) {
		this.odpInstantiation = odpInstantiation;
	}
	
	private OdpInstantiation odpInstantiation;

	/**
	 * ODP Specialization wrapper.
	 * @return
	 */
	public OdpInstantiation getOdpSpecialization() {
		return odpInstantiation;
	}

	@Override
	public ProjectId getProjectId() {
		return odpInstantiation.getProjectId();
	}
}
