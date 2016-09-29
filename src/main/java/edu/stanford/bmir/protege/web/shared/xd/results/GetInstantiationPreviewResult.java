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
public class GetInstantiationPreviewResult implements Result {

	/**
	 * No-arg constructor, for GWT-RPC serialization purpose only
	 */
	@SuppressWarnings("unused")
	private GetInstantiationPreviewResult() {
	}
	
	/**
	 * Constructor
	 * @param instantiationPreview
	 */
	public GetInstantiationPreviewResult(String instantiationPreview) {
		this.instantiationPreview = instantiationPreview;
	}
	
	// Data fields
	private String instantiationPreview;
	
	// Access methods
	public String getInstantiationPreview() {
		return instantiationPreview;
	}
}
