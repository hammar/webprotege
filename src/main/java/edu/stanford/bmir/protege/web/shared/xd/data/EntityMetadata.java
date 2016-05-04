package edu.stanford.bmir.protege.web.shared.xd.data;

import java.io.Serializable;
import java.util.Map;

public class EntityMetadata implements Serializable {

	private static final long serialVersionUID = 2336790710574945292L;
	
	private String label;
	private Map<String,String> metadata;
	
	/**
	 * GWT-RPC-required constructor
	 */
	public EntityMetadata() {
		
	}
	
	public EntityMetadata(String label, Map<String,String> metadata) {
		this.label = label;
		this.metadata = metadata;
	}
	
	public String getLabel() {
		return label;
	}
	
	public Map<String,String> getMetadata() {
		return metadata;
	}
}
