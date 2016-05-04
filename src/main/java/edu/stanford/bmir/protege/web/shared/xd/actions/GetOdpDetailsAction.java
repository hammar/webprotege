package edu.stanford.bmir.protege.web.shared.xd.actions;

import edu.stanford.bmir.protege.web.shared.dispatch.Action;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpDetailsResult;

public class GetOdpDetailsAction implements Action<GetOdpDetailsResult> {

	/**
	 * No-arg constructor, for GWT-RPC serialization purpose only
	 */
	@SuppressWarnings("unused")
	private GetOdpDetailsAction() {
	}
	
	/**
	 * Constructor
	 * @param odpUri
	 */
	public GetOdpDetailsAction(String odpUri) {
		this.odpUri = odpUri;
	}
	
	private String odpUri;

	/**
	 * URI of ODP to fetch contents for.
	 * @return
	 */
	public String getOdpUri() {
		return odpUri;
	}
}
