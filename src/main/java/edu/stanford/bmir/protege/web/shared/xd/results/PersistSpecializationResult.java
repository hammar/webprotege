package edu.stanford.bmir.protege.web.shared.xd.results;

import edu.stanford.bmir.protege.web.shared.dispatch.Result;

/**
 * The response to a ODP specialization persistence action. Contains a reference to the new revision
 * numer this specialization caused. 
 * 
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
public class PersistSpecializationResult implements Result {

	/**
	 * No-arg constructor, for GWT-RPC serialization purpose only
	 */
	@SuppressWarnings("unused")
	private PersistSpecializationResult() {
	}
	
	/**
	 * Constructor
	 * @param specializationPreview
	 */
	public PersistSpecializationResult(Integer revNumber) {
		this.revNumber = revNumber;
	}
	
	// Data fields
	private Integer revNumber;
	
	// Access methods
	public Integer getRevNumber() {
		return revNumber;
	}
}
