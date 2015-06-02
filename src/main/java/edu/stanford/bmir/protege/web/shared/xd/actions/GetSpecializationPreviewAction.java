package edu.stanford.bmir.protege.web.shared.xd.actions;

import edu.stanford.bmir.protege.web.shared.dispatch.Action;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpSpecialization;
import edu.stanford.bmir.protege.web.shared.xd.results.GetSpecializationPreviewResult;

public class GetSpecializationPreviewAction implements Action<GetSpecializationPreviewResult> {

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
}
