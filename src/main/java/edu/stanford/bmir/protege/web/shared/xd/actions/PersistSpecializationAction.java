package edu.stanford.bmir.protege.web.shared.xd.actions;

import edu.stanford.bmir.protege.web.shared.dispatch.Action;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpSpecialization;
import edu.stanford.bmir.protege.web.shared.xd.results.PersistSpecializationResult;

public class PersistSpecializationAction implements Action<PersistSpecializationResult> {

	/**
	 * No-arg constructor, for GWT-RPC serialization purpose only
	 */
	@SuppressWarnings("unused")
	private PersistSpecializationAction() {
	}
	
	/**
	 * Constructor
	 * @param odpUri
	 */
	public PersistSpecializationAction(OdpSpecialization odpSpecialization) {
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
