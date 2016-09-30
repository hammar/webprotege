package edu.stanford.bmir.protege.web.client.xd.instantiation.panels;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import edu.stanford.bmir.protege.web.client.xd.instantiation.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.client.xd.visualization.vowl.VOWLVisualizationJso;

public class VisualisationPanel extends FlowPanel implements InstantiationWizardPanel {
	
	private DesignPatternInstantiationWizard parentWizard;
	private Widget graphContainer;
	private String graphContainerId;
	private VOWLVisualizationJso vowlVisualizationJso;
	
	public VisualisationPanel(DesignPatternInstantiationWizard parentWizard) {
		this.addStyleName("xdpVisualisationPanel");
		
		this.parentWizard = parentWizard;
		this.graphContainerId = "graph-container-id-" + hashCode();
		graphContainer = new HTML();
		graphContainer.setWidth("640px");
		graphContainer.setHeight("420px");
		graphContainer.getElement().setId(this.graphContainerId);
		this.add(graphContainer);
	}

	@Override
	public void renderPanel() {
		String odpAsJsonString = parentWizard.getOdpAsJsonString();
		if (Document.get().getElementById(this.graphContainerId) != null) {
			if (VOWLVisualizationJso.isBrowserCompatible(this.graphContainerId))
			{
				if (vowlVisualizationJso != null) {
					vowlVisualizationJso.setData(odpAsJsonString);
				}
				else {
					vowlVisualizationJso = VOWLVisualizationJso.initialize(this.graphContainerId, odpAsJsonString);
				}
				vowlVisualizationJso.reset();
			}
		}
	}
}
