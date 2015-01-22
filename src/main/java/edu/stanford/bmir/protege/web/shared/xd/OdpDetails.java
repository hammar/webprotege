package edu.stanford.bmir.protege.web.shared.xd;

import java.io.Serializable;

/**
 * Data class used for serializing ODP metadata to be sent over the wire.
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
public class OdpDetails implements Serializable {
	private static final long serialVersionUID = 5859893598140995111L;
	private String uri;
	private String name;
	private String description;
	private String domains;
	private String cqs;
	private String image;
	
	public OdpDetails() {
		
	}
	
	public OdpDetails(String uri, String name, String description, String domains, String cqs, String image) {
		this.uri = uri;
		this.name = name;
		this.description = description;
		this.domains = domains;
		this.cqs = cqs;
		this.image = image;
	}
	
	// This is a data class, so equals overridden to match state (field) equality rather than
	// identify (memory address) equality when comparing two instances.
	// TODO: Compare against all fields, also taking into account that each field can actually be null.
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj instanceof OdpDetails) {
			OdpDetails otherOdp = (OdpDetails)obj;
			return 
					this.uri.equals(otherOdp.uri) &&
					this.name.equals(otherOdp.name);
		}
		return false;
	}
	
	public String getUri() {
		return uri;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getDomains() {
		return domains;
	}

	public String getCqs() {
		return cqs;
	}
	
	public String getImage() {
		return image;
	}
}
