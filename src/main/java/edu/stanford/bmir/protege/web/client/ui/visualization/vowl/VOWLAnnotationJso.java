package edu.stanford.bmir.protege.web.client.ui.visualization.vowl;

import com.google.gwt.core.client.JavaScriptObject;

public class VOWLAnnotationJso extends JavaScriptObject {
	
	protected VOWLAnnotationJso() {}
	
	public final native String getAnnotationProperty(String property) /*-{
	  var objAr = this;
	  var obj = objAr[0];
	  if(obj[property] != null)
	  	return obj[property];
	  return "";
	}-*/;
	
}
