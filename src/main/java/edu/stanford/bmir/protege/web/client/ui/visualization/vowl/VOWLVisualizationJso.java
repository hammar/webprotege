/*
 * VowlVisualization.java
 *
 */

package edu.stanford.bmir.protege.web.client.ui.visualization.vowl;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Initializes the graph and converts the ontology as a JSON object.
 * This is the top wrapper class for the JSON object.
 * @author Maria Michou
 *
 */
public class VOWLVisualizationJso extends JavaScriptObject {

	protected VOWLVisualizationJso() {
	}

	public static native VOWLVisualizationJso initialize(String containerId, String data) /*-{
	  return $wnd.webvowlApp.app("#" + containerId, data).initialize();
  }-*/;

	public final native void setData(String data) /*-{
	  this.data(data);
  }-*/;

	/**
	 * If not compatible shows an error message in given container.
	 *
	 * @return True if not IE.
	 */
	public static native boolean isBrowserCompatible(String containerId) /*-{
	  var supported = $wnd.webvowlApp.browserWarning("#" + containerId).isSupported();
	  if (!supported) {
		  $wnd.webvowlApp.browserWarning("#" + containerId).showError();
	  }
	  return supported;
  }-*/;
	
	public final native VOWLDetailsJso getStatistics()  /*-{
	  return this.statistics();
  }-*/;
	
	public final native VOWLOntologyInfoJso getOntologyInfo()  /*-{
	  return this.ontologyInfo();
	}-*/;
	

	public final native VOWLNodeJso getSelectedNode() /*-{
	  return this.selectedNode();
  }-*/;

	public final native VOWLLabelJso getSelectedLabel() /*-{
	  return this.selectedLabel();
  }-*/;
	
	public final native void setLanguage(String newLanguage) /*-{
	  this.language(newLanguage);
}-*/;
	
	public final native void pause() /*-{
	  this.pause();
}-*/;
	public final native void unpause() /*-{
	  this.unpause();
}-*/;
	
	public final native void reset() /*-{
	  this.reset();
}-*/;

  public final native void togglePickAndPin() /*-{
	  this.togglePickAndPin();
  }-*/;

  public final native void collapsingDegree(int collapsingDegree) /*-{
	  this.collapsingDegree(collapsingDegree);
  }-*/;

  public final native void classDistance(int classDistance) /*-{
	  this.classDistance(classDistance);
  }-*/;

  public final native void datatypeDistance(int datatypeDistance) /*-{
	  this.datatypeDistance(datatypeDistance);
  }-*/;

}
