package edu.stanford.bmir.protege.web.shared.xd.results;

import java.io.Serializable;
import java.util.List;

import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.xd.OdpDetails;

/**
 * 
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
public class GetOdpsByCategoryResult implements Result, Serializable {

	private static final long serialVersionUID = 6698696724655288967L;

	/**
	 * No-arg constructor, for GWT-RPC serialization purpose only
	 */
	public GetOdpsByCategoryResult() {
	}
	
	/**
	 * Constructor
	 * @param searchResults
	 */
	public GetOdpsByCategoryResult(List<OdpDetails> odps) {
		this.odps = odps;
	}
	
	// Data fields
	private List<OdpDetails> odps;
	
	// Access methods
	public List<OdpDetails> getOdps() {
		return odps;
	}
}
