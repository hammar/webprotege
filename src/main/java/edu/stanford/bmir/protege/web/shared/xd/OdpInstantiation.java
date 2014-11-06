package edu.stanford.bmir.protege.web.shared.xd;

public class OdpInstantiation {
	private String uri;
	private String name;
	private String odp;
	
	public OdpInstantiation(String uri, String name, String odp) {
		this.uri = uri;
		this.name = name;
		this.odp = odp;
	}

	public String getUri() {
		return uri;
	}

	public String getName() {
		return name;
	}

	public String getOdp() {
		return odp;
	}
}
