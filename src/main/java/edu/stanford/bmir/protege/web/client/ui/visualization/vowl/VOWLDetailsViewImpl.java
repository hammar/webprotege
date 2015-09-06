package edu.stanford.bmir.protege.web.client.ui.visualization.vowl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedStackPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class VOWLDetailsViewImpl extends Composite implements VOWLDetailsView {

	private static VOWLDetailsViewImplUiBinder uiBinder = GWT
			.create(VOWLDetailsViewImplUiBinder.class);

	public static String ontologyAsJSONStr;
	
	private HTMLPanel rootElement;

	protected Grid staticInfoPanel;

	protected DecoratedStackPanel dynamicInfoPanel;

	protected VerticalPanel descriptionPanel;

	protected VerticalPanel metadataPanel;

	protected VerticalPanel statisticsPanel;
	

	interface VOWLDetailsViewImplUiBinder extends UiBinder<HTMLPanel, VOWLDetailsViewImpl> {

	}

	public VOWLDetailsViewImpl() {
		rootElement = uiBinder.createAndBindUi(this);
		initWidget(rootElement);

		// Set up static info panel (contains name, IRI, version, author(s) and language)
		staticInfoPanel = new Grid(5, 1);
		staticInfoPanel.setCellSpacing(5);
		staticInfoPanel.setCellPadding(3);
		staticInfoPanel.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);

		descriptionPanel = new VerticalPanel();

		metadataPanel = new VerticalPanel();

		statisticsPanel = new VerticalPanel();

		dynamicInfoPanel = new DecoratedStackPanel();
		dynamicInfoPanel.setTitle("Dynamic panel");
		dynamicInfoPanel.add(descriptionPanel, "<h3>Description</h3>", true);
		dynamicInfoPanel.add(metadataPanel, "<h3>Metadata</h3>", true);
		dynamicInfoPanel.add(statisticsPanel, "<h3>Metrics</h3>", true);
		dynamicInfoPanel.showStack(2);
	}
	
	

	/**
	 * This renders general ontology data only the first time the graph is loaded 
	 * (and subsequently onRefresh() and when the Visualization tab is activated). 
	 */
	public void renderDetailsStaticInfo(VOWLVisualizationJso visualizationJso) {

		rootElement.remove(staticInfoPanel);
		rootElement.remove(dynamicInfoPanel);

		staticInfoPanel.setHTML(0, 0, "<h1>" + visualizationJso.getOntologyInfo().getTitle() + "</h1>");
		staticInfoPanel.setHTML(1, 0, "<a href=\"" + visualizationJso.getOntologyInfo().getIRI() + "\">"+visualizationJso.getOntologyInfo().getIRI()+"</a>");
		staticInfoPanel.setText(2, 0, "Version: "+String.valueOf(visualizationJso.getOntologyInfo().getVersion()));
		staticInfoPanel.setWidget(3, 0, new Label("Author(s): "+ visualizationJso.getOntologyInfo().getAuthors().join()));

		String langStr="";
		for(int i=0; i< visualizationJso.getOntologyInfo().getLanguages().length(); i++) {
			langStr += "<option value=\""+visualizationJso.getOntologyInfo().getLanguages().get(i)+"\">"+visualizationJso.getOntologyInfo().getLanguages().get(i)+"</option>";
		}
		if(!langStr.isEmpty())
			staticInfoPanel.setHTML(4, 0, 
					"Language: <select>" + 
							langStr +
					"</select>");
					

		descriptionPanel.add(new Label(visualizationJso.getOntologyInfo().getDescription()));
		
		metadataPanel.add(new HTML(visualizationJso.getOntologyInfo().getOther().getMetadataElements().join("<br>")));


		statisticsPanel.add(new HTML("Classes: <i>"+visualizationJso.getStatistics().getClassCount()+"</i>"));
		statisticsPanel.add(new HTML("Object prop.: <i>"+visualizationJso.getStatistics().getObjectPropertyCount()+"</i>"));
		statisticsPanel.add(new HTML("Datatype prop.: <i>"+visualizationJso.getStatistics().getDatatypePropertyCount()+"</i>"));
		statisticsPanel.add(new HTML("Individuals: <i>"+visualizationJso.getStatistics().getIndividualCount()+"</i>"));
		statisticsPanel.add(new HTML("Nodes: <i>"+visualizationJso.getStatistics().getNodeCount()+"</i>"));
		statisticsPanel.add(new HTML("Edges: <i>"+visualizationJso.getStatistics().getAxiomCount()+"</i>"));
		rootElement.add(staticInfoPanel);
		rootElement.add(dynamicInfoPanel);
		
	}

	@Override
	public Widget getWidget() {
		return this;
	}

}
