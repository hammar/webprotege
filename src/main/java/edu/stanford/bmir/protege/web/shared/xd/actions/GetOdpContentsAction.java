package edu.stanford.bmir.protege.web.shared.xd.actions;

import edu.stanford.bmir.protege.web.shared.dispatch.Action;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpContentsResult;

public class GetOdpContentsAction implements Action<GetOdpContentsResult> {

	/**
	 * No-arg constructor, for GWT-RPC serialization purpose only
	 */
	@SuppressWarnings("unused")
	private GetOdpContentsAction() {
	}
	
	/**
	 * Constructor
	 * @param odpUri
	 */
	public GetOdpContentsAction(String odpUri) {
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
