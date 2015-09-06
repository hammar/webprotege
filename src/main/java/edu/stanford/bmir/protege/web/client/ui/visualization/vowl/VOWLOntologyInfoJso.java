package edu.stanford.bmir.protege.web.client.ui.visualization.vowl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * Retrieves annotation data, i.e. <i>title</i>, <i>version</i>, <i>authors</i>.
 * @author Maria Michou
 *
 */
public class VOWLOntologyInfoJso extends JavaScriptObject {
	
	protected VOWLOntologyInfoJso() {}
	
	public final native JsArrayString getLanguages()  /*-{
	  return this.languages;
	}-*/;
	
	public final native String getTitle()  /*-{
	  return (this.title == null)? "" : this.title["undefined"];
	}-*/;
	
	public final native String getIRI()  /*-{
	  return this.iri;
	}-*/;
	
	public final native String getVersion()  /*-{
	  return (this.version == null) ? "" : this.version;
	}-*/;
	
	public final native JsArrayString getAuthors()  /*-{
	  return (this.author == null) ? [] : this.author;
	}-*/;
	
	public final native String getDescription()  /*-{
	  return (this.description == null) ? "No description available." : this.description["undefined"];
	}-*/;

	//metadata
	public final native VOWLMetadataJso getOther()  /*-{
	  return this.other;
	}-*/;
}
