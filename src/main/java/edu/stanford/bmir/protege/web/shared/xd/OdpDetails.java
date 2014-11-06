package edu.stanford.bmir.protege.web.shared.xd;

public class OdpDetails {
	private String uri;
	private String name;
	
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
