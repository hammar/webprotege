package edu.stanford.bmir.protege.web.client.xd.instantiation.widgets;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.HTML;
import edu.stanford.bmir.protege.web.client.xd.visualization.vowl.VOWLVisualizationJso;

public class VisualisationWidget extends HTML {
	
	private String graphContainerId;
	private VOWLVisualizationJso vowlVisualizationJso;
	
	public VisualisationWidget() {
		super();
		
		this.addStyleName("xdpVisualisationWidget");
		
		this.graphContainerId = "graph-container-id-" + hashCode();
		this.getElement().setId(this.graphContainerId);
		
	}

	public void renderJson(String vowlJsonString) {
		if (Document.get().getElementById(this.graphContainerId) != null) {
			if (VOWLVisualizationJso.isBrowserCompatible(this.graphContainerId))
			{
				if (vowlVisualizationJso != null) {
					vowlVisualizationJso.setData(vowlJsonString);
				}
				else {
					vowlVisualizationJso = VOWLVisualizationJso.initialize(this.graphContainerId, vowlJsonString);
				}
				vowlVisualizationJso.reset();
			}
		}
	}
}
