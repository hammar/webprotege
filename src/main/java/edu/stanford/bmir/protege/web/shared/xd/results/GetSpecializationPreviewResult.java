package edu.stanford.bmir.protege.web.shared.xd.results;

import edu.stanford.bmir.protege.web.shared.dispatch.Result;

/**
 * This class carries a preview of an ODP specialization, in some suitable 
 * human-readable syntax (most likely Turtle or Manchester syntax) back to the
 * client-side. 
 * 
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
public class GetSpecializationPreviewResult implements Result {

	/**
	 * No-arg constructor, for GWT-RPC serialization purpose only
	 */
	@SuppressWarnings("unused")
	private GetSpecializationPreviewResult() {
	}
	
	/**
	 * Constructor
	 * @param specializationPreview
	 */
	public GetSpecializationPreviewResult(String specializationPreview) {
		this.specializationPreview = specializationPreview;
	}
	
	// Data fields
	private String specializationPreview;
	
	// Access methods
	public String getSpecializationPreview() {
		return specializationPreview;
	}
}
