package edu.stanford.bmir.protege.web.shared.xd;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data class used for serializing ODP metadata to be sent over the wire.
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OdpDetails implements Serializable {
	private static final long serialVersionUID = 5859893598140995111L;
	private String uri;
	private String name;
	private String description;
	private String[] domains;
	private String[] cqs;
	private String image;
	private String[] scenarios;
	private String[] classes;
	private String[] properties;
	
	public OdpDetails() {
		
	}
	
	public OdpDetails(String uri, String name, String description, String[] domains, String[] cqs, String image, String[] scenarios) {
		this(uri,name,description,domains,cqs,image,scenarios,null,null);
	}
	
	public OdpDetails(String uri, String name, String description, String[] domains, String[] cqs, String image, String[] scenarios, String[] classes, String[] properties) {
		this.uri = uri;
		this.name = name;
		this.description = description;
		this.domains = domains;
		this.cqs = cqs;
		this.image = image;
		this.scenarios = scenarios;
		this.classes = classes;
		this.properties = properties;
	}
	
	public OdpDetails(String uri) {
		this(uri,null,null,null,null,null,null,null,null);
	}
	
	public OdpDetails(String uri, String name) {
		this(uri,name,null,null,null,null,null,null,null);
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

	public String[] getDomains() {
		return domains;
	}

	public String[] getCqs() {
		return cqs;
	}
	
	public String getImage() {
		return image;
	}
	
	public String[] getScenarios() {
		return scenarios;
	}
	
	public String[] getClasses() {
		return classes;
	}
	
	public String[] getProperties() {
		return properties;
	}
}
