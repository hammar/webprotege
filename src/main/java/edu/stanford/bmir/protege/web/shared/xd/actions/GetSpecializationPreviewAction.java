package edu.stanford.bmir.protege.web.shared.xd.actions;

import edu.stanford.bmir.protege.web.shared.HasProjectId;
import edu.stanford.bmir.protege.web.shared.dispatch.Action;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpSpecialization;
import edu.stanford.bmir.protege.web.shared.xd.results.GetSpecializationPreviewResult;

public class GetSpecializationPreviewAction implements Action<GetSpecializationPreviewResult>, HasProjectId {

	/**
	 * No-arg constructor, for GWT-RPC serialization purpose only
	 */
	@SuppressWarnings("unused")
	private GetSpecializationPreviewAction() {
	}
	
	/**
	 * Constructor
	 * @param odpUri
	 */
	public GetSpecializationPreviewAction(OdpSpecialization odpSpecialization) {
		this.odpSpecialization = odpSpecialization;
	}
	
	private OdpSpecialization odpSpecialization;

	/**
	 * ODP Specialization wrapper.
	 * @return
	 */
	public OdpSpecialization getOdpSpecialization() {
		return odpSpecialization;
	}

	@Override
	public ProjectId getProjectId() {
		return odpSpecialization.getProjectId();
	}
}
