package edu.stanford.bmir.protege.web.shared.xd.results;

import java.io.Serializable;

import com.karlhammar.xdpservices.data.CodpDetails;

import edu.stanford.bmir.protege.web.shared.dispatch.Result;

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
	public GetOdpDetailsResult(CodpDetails details) {
		this.details = details;
	}
	
	// Data fields
	private CodpDetails details;
	
	// Access methods
	public CodpDetails getDetails() {
		return details;
	}
}
