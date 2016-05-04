package edu.stanford.bmir.protege.web.shared.xd.results;

import java.io.Serializable;

import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.xd.OdpDetails;

/**
 * 
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
public class GetOdpDetailsResult implements Result, Serializable {
	
	private static final long serialVersionUID = -3660941328171394344L;

	/**
	 * No-arg constructor, for GWT-RPC serialization purpose only
	 */
	public GetOdpDetailsResult() {
	}
	
	/**
	 * Constructor
	 * @param details
	 */
	public GetOdpDetailsResult(OdpDetails details) {
		this.details = details;
	}
	
	// Data fields
	private OdpDetails details;
	
	// Access methods
	public OdpDetails getDetails() {
		return details;
	}
}
