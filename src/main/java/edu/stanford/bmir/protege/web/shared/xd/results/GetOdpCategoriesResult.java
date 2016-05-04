package edu.stanford.bmir.protege.web.shared.xd.results;

import java.io.Serializable;
import java.util.List;

import edu.stanford.bmir.protege.web.shared.dispatch.Result;

/**
 * 
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
public class GetOdpCategoriesResult implements Result, Serializable {

	private static final long serialVersionUID = -1203531879065821742L;
	
	// Data field
	private List<String> categories;

	/**
	 * No-arg constructor, for GWT-RPC serialization purpose only
	 */
	public GetOdpCategoriesResult() {
	}
	
	/**
	 * Constructor
	 * @param classFrames
	 * @param objectPropertyFrames
	 * @param dataPropertyFrames
	 */
	public GetOdpCategoriesResult(List<String> categories) {
		this.categories = categories;
	}

	
	// Access methods
	public List<String> getCategories() {
		return categories;
	}
}
