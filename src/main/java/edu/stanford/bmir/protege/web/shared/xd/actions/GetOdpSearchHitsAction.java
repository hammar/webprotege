package edu.stanford.bmir.protege.web.shared.xd.actions;

import edu.stanford.bmir.protege.web.shared.dispatch.Action;
import edu.stanford.bmir.protege.web.shared.xd.OdpSearchFilterConfiguration;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpSearchHitsResult;

public class GetOdpSearchHitsAction implements Action<GetOdpSearchHitsResult> {

	/**
	 * No-arg constructor, for GWT-RPC serialization purpose only
	 */
	@SuppressWarnings("unused")
	private GetOdpSearchHitsAction() {
	}
	
	/**
	 * Constructor
	 * @param odpUri
	 */
	public GetOdpSearchHitsAction(String queryString, OdpSearchFilterConfiguration filterConfiguration) {
		this.queryString = queryString;
		this.filterConfiguration = filterConfiguration;
	}
	
	private String queryString;
	private OdpSearchFilterConfiguration filterConfiguration;
	
	// Access methods
	public String getQueryString() {
		return queryString;
	}

	public OdpSearchFilterConfiguration getFilterConfiguration() {
		return filterConfiguration;
	}
}
