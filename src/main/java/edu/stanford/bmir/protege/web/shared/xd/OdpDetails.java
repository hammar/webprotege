package edu.stanford.bmir.protege.web.shared.xd;

import java.io.Serializable;

public class OdpDetails implements Serializable {
	private static final long serialVersionUID = 5859893598140995111L;
	private String uri;
	private String name;
	
	public OdpDetails() {
		
	}
	
	public OdpDetails(String uri, String name) {
		this.uri = uri;
		this.name = name;
	}
	
	public String getUri() {
		return uri;
	}
	
	public String getName() {
		return name;
	}
}
