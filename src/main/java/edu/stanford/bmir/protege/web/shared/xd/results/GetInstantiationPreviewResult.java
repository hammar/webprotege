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
	 * @param instantiationAxioms
	 */
	public GetInstantiationPreviewResult(String instantiationAxioms, String instantiationAsJson) {
		this.instantiationAxioms = instantiationAxioms;
		this.instantiationAsJson = instantiationAsJson;
	}
	
	// Data fields
	private String instantiationAxioms;
	private String instantiationAsJson;
	
	// Access methods
	public String getInstantiationAxioms() {
		return instantiationAxioms;
	}
	
	public String getInstantiationAsJson() {
		return this.instantiationAsJson;
	}
}
