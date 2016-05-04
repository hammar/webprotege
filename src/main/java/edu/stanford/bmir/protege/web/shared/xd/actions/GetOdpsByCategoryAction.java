package edu.stanford.bmir.protege.web.shared.xd.actions;

import edu.stanford.bmir.protege.web.shared.dispatch.Action;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpsByCategoryResult;

public class GetOdpsByCategoryAction implements Action<GetOdpsByCategoryResult> {

	/**
	 * No-arg constructor, for GWT-RPC serialization purpose only
	 */
	@SuppressWarnings("unused")
	private GetOdpsByCategoryAction() {
	}
	
	/**
	 * Constructor
	 * @param odpUri
	 */
	public GetOdpsByCategoryAction(String category) {
		this.category = category;
	}
	
	private String category;
	
	// Access methods
	public String getCategory() {
		return category;
	}
}
