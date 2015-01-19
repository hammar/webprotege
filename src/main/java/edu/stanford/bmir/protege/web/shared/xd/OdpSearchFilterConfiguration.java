package edu.stanford.bmir.protege.web.shared.xd;

import java.io.Serializable;

/**
 * Data class used to package the search filters enabled by the user when using
 * the ODP Search portlet.
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
public class OdpSearchFilterConfiguration implements Serializable {

	private static final long serialVersionUID = -1205072063125605002L;

	// Core filters
	private String category;
	private String size;
	private String profile;
	private String strategy;
	
	// ODP alignment filters
	private Boolean dolceMappingRequired;
	private Boolean schemaOrgMappingRequired;
	private Boolean dbPediaMappingRequired;
	
	// Default no-arg constructor
	public OdpSearchFilterConfiguration() {
	}

	// Accessor functions below
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

	public Boolean getDolceMappingRequired() {
		return dolceMappingRequired;
	}

	public void setDolceMappingRequired(Boolean dolceMappingRequired) {
		this.dolceMappingRequired = dolceMappingRequired;
	}

	public Boolean getSchemaOrgMappingRequired() {
		return schemaOrgMappingRequired;
	}

	public void setSchemaOrgMappingRequired(Boolean schemaOrgMappingRequired) {
		this.schemaOrgMappingRequired = schemaOrgMappingRequired;
	}

	public Boolean getDbPediaMappingRequired() {
		return dbPediaMappingRequired;
	}

	public void setDbPediaMappingRequired(Boolean dbPediaMappingRequired) {
		this.dbPediaMappingRequired = dbPediaMappingRequired;
	}
	
}
