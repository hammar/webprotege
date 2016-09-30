package edu.stanford.bmir.protege.web.client.xd.instantiation.panels;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.xd.visualization.vowl.VOWLVisualizationJso;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.xd.visualization.ConvertOntologyAction;
import edu.stanford.bmir.protege.web.shared.xd.visualization.ConvertOntologyResult;

public class VisualisationPanel extends FlowPanel {
	
	private Widget graphContainer;
	private String graphContainerId;
	private String ontologyAsJSONStr;
	private ProjectId projectId;
	
	public VisualisationPanel(ProjectId projectId) {
		this.projectId = projectId;
		this.addStyleName("xdpVisualisationPanel");
		
		this.graphContainerId = "project-id-" + projectId.getId() + "-hash-code-" + hashCode();
		graphContainer = new HTML();
		graphContainer.setWidth("640px");
		graphContainer.setHeight("420px");
		graphContainer.getElement().setId(this.graphContainerId);
		this.add(graphContainer);
	}
	
	@Override
	protected void onLoad() {
		DispatchServiceManager.get().execute(new ConvertOntologyAction(this.projectId), new DispatchServiceCallback<ConvertOntologyResult>() {
			@Override
			public void handleSuccess(ConvertOntologyResult result) {
				ontologyAsJSONStr = result.getOntologyasJSONStr();
				initializeVisualization(ontologyAsJSONStr);
			}
		});
	}

	private void initializeVisualization(final String convertedOntology) {
		if (Document.get().getElementById(this.graphContainerId) != null) {
			if (VOWLVisualizationJso.isBrowserCompatible(this.graphContainerId))
			{
				VOWLVisualizationJso.initialize(this.graphContainerId, convertedOntology);
			}
		}
	}
}
