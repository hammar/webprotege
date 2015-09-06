package edu.stanford.bmir.protege.web.client.ui.visualization.vowl;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Retrieves statistics for the selected ontology.
 * @author Maria Michou
 *
 */
public class VOWLDetailsJso extends JavaScriptObject {

	protected VOWLDetailsJso() {
	}

	public final native int getClassCount()  /*-{
	  return this.classCount;
  }-*/;

	public final native int getDatatypeCount() /*-{
	  return this.datatypeCount;
  }-*/;

	public final native int getPropertyCount() /*-{
	  return this.propertyCount;
  }-*/;

	public final native int getObjectPropertyCount()  /*-{
	  return this.objectPropertyCount;
  }-*/;

	public final native int getDatatypePropertyCount()  /*-{
	  return this.datatypePropertyCount;
  }-*/;

	public final native int getNodeCount()  /*-{
	  return this.nodeCount;
  }-*/;

	public final native int getAxiomCount()  /*-{
	  return this.axiomCount;
  }-*/;
	
	public final native int getEdgeCount()  /*-{
	  return this.edgeCount;
}-*/;

	public final native int getIndividualCount() /*-{
	  return this.individualCount;
  }-*/;
	
	public final native int getTotalIndividualCount() /*-{
	  return this.totalIndividualCount;
}-*/;
}
