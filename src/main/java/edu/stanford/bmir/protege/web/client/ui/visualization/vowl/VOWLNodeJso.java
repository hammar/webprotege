package edu.stanford.bmir.protege.web.client.ui.visualization.vowl;

import com.google.gwt.core.client.JsArray;

/**
 * This is a wrapper class for owl:Class, rdfs:Class, etc.
 * @author Maria Michou
 *
 */
public class VOWLNodeJso extends VOWLElementJso {
	protected VOWLNodeJso() {
	}

	public final native JsArray<VOWLElementJso> getIndividuals() /*-{
	  return this.individuals();
	}-*/;
	
}
