package edu.stanford.bmir.protege.web.client.xd.instantiation.panels;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import edu.stanford.bmir.protege.web.client.xd.instantiation.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.client.xd.visualization.vowl.VOWLVisualizationJso;

public class VisualisationPanel extends FlowPanel {
	
	//private DesignPatternInstantiationWizard parentWizard;
	private Widget graphContainer;
	private String graphContainerId;
	private VOWLVisualizationJso vowlVisualizationJso;
	
	public VisualisationPanel(DesignPatternInstantiationWizard parentWizard) {
		this.addStyleName("xdpVisualisationPanel");
		
		//this.parentWizard = parentWizard;
		this.graphContainerId = "graph-container-id-" + hashCode();
		graphContainer = new HTML();
		graphContainer.setWidth("800px");
		graphContainer.setHeight("540px");
		graphContainer.getElement().setId(this.graphContainerId);
		this.add(graphContainer);
	}

	public void renderPanel(String vowlJsonString) {
		//String odpAsJsonString = parentWizard.getOdpAsJsonString();
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
