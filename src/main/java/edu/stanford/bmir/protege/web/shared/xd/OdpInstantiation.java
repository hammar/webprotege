package edu.stanford.bmir.protege.web.shared.xd;

import java.io.Serializable;

public class OdpInstantiation implements Serializable {
	private static final long serialVersionUID = -8164990474684842383L;
	private String uri;
	private String name;
	private OdpDetails odp;
	
	public OdpInstantiation() {
		
	}
	
	public OdpInstantiation(String uri, String name, OdpDetails odp) {
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

	public OdpDetails getOdp() {
		return odp;
	}
}
