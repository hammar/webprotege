package edu.stanford.bmir.protege.web.shared.xd.results;

import java.io.Serializable;
import java.util.List;

import com.karlhammar.xdpservices.data.CodpDetails;

import edu.stanford.bmir.protege.web.shared.dispatch.Result;

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
	public GetOdpsByCategoryResult(List<CodpDetails> odps) {
		this.odps = odps;
	}
	
	// Data fields
	private List<CodpDetails> odps;
	
	// Access methods
	public List<CodpDetails> getOdps() {
		return odps;
	}
}
