package edu.stanford.bmir.protege.web.shared.xd.results;

import java.io.Serializable;
import java.util.List;

import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.xd.OdpSearchResult;

/**
 * 
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
public class GetOdpSearchHitsResult implements Result, Serializable {

	private static final long serialVersionUID = 2436469262991273996L;

	/**
	 * No-arg constructor, for GWT-RPC serialization purpose only
	 */
	public GetOdpSearchHitsResult() {
	}
	
	/**
	 * Constructor
	 * @param searchResults
	 */
	public GetOdpSearchHitsResult(List<OdpSearchResult> searchResults) {
		this.searchResults = searchResults;
	}
	
	// Data fields
	private List<OdpSearchResult> searchResults;
	
	// Access methods
	public List<OdpSearchResult> getSearchResults() {
		return searchResults;
	}
}
