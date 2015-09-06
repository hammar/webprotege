package edu.stanford.bmir.protege.web.client.ui.visualization.vowl;

import com.google.gwt.core.client.JsArray;

/**
 * This is a wrapper class for properties.
 * @author Maria Michou
 *
 */
public class VOWLLabelJso extends VOWLElementJso {
	protected VOWLLabelJso() {
	}

	public final native VOWLNodeJso getDomain() /*-{
	  return this.domain();
  }-*/;

	public final native VOWLNodeJso getRange() /*-{
	  return this.range();
  }-*/;
	
	public final native VOWLLabelJso getInverse() /*-{
	  return this.inverse();
	}-*/;
	
	public final native JsArray<VOWLLabelJso> getSuperproperties() /*-{
	  return this.superproperties();
  	}-*/; 
	
	public final native JsArray<VOWLLabelJso> getSubproperties() /*-{
	  return this.subproperties();
	}-*/;
	
	public final native String getCardinality() /*-{
	  return this.cardinality();
	}-*/;
	
	public final native String getMinCardinality() /*-{
	  return this.minCardinality();
	}-*/;
	
	public final native String getMaxCardinality() /*-{
	  return this.maxCardinality();
	}-*/;
}
