package edu.stanford.bmir.protege.web.client.ui.visualization.vowl;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * This is a wrapper class for class and property instances.
 * <i>Superclass</i> of {@link VOWLNodeJso} and {@link VOWLLabelJso}
 * @author Maria Michou
 *
 */
public class VOWLElementJso extends JavaScriptObject {

	protected VOWLElementJso() {
	}

	public final native String getIri() /*-{
	  return this.iri();
  	}-*/;

	public final native String getLabel() /*-{
	  return this.labelForCurrentLanguage();
  	}-*/;

	public final native String getType() /*-{
	  return this.type();
  	}-*/;
	
	public final native String getCharacteristics() /*-{
	  return (this.attributes() == null) ? "" : this.attributes();
	}-*/;
	
	public final native String getId() /*-{
	  return this.id();
	}-*/;
	
	public final native String getComment() /*-{
	  return (this.commentForCurrentLanguage() == null) ? "" : this.commentForCurrentLanguage();
	}-*/;
	
	public final native VOWLAnnotationJso getAnnotations(String property) /*-{
		return (this.annotations() == null) ? null : this.annotations()[property];
	}-*/;
	
}
