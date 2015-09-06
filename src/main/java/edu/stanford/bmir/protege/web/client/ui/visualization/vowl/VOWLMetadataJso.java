package edu.stanford.bmir.protege.web.client.ui.visualization.vowl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

public class VOWLMetadataJso extends JavaScriptObject {
	protected VOWLMetadataJso() {}

	public final native JsArrayString getMetadataElements()  /*-{
	  var array = [];
	  for(var key in this) {
	    var elemArray = this[key]; 
	  	for(var i=0; i<elemArray.length; i++) {
	     	var elem = elemArray[i];
	     	array.push(elem["identifier"] + ": " + elem["value"]);
	     }
		}
		if(array.length == 0) {
			array = ["No annotations available."];
		}
		return array;
	}-*/;

}
