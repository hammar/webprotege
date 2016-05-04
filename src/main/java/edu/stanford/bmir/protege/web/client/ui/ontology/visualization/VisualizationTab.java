/*
 * VisualizationTab.java
 *
 */

package edu.stanford.bmir.protege.web.client.ui.ontology.visualization;

import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.ui.tab.AbstractTab;
import edu.stanford.bmir.protege.web.client.ui.visualization.vowl.VOWLControlPortlet;
import edu.stanford.bmir.protege.web.client.ui.visualization.vowl.VOWLDetailsPortlet;
import edu.stanford.bmir.protege.web.client.ui.visualization.vowl.VOWLDetailsViewImpl;
import edu.stanford.bmir.protege.web.client.ui.visualization.vowl.VOWLSelectionDetailsPortlet;
import edu.stanford.bmir.protege.web.client.ui.visualization.vowl.VOWLVisualizationPortlet;
import edu.stanford.bmir.protege.web.shared.selection.SelectionModel;

/**
 * Visualization tab usable for displaying visualization portlets.
 */
@SuppressWarnings("unchecked")
public class VisualizationTab extends AbstractTab {

	private VOWLVisualizationPortlet vowlVisualizationPortlet;
	private VOWLDetailsPortlet vowlDetailsPortlet;
	//private VOWLDetailsViewImpl vowlDetailsViewImpl;
	private VOWLSelectionDetailsPortlet vowlSelectionDetailsPortlet;
	private VOWLControlPortlet vowlControlPortlet;

	public VisualizationTab(SelectionModel selectionModel, Project project) {
		super(selectionModel, project);
	}

	@Override
	public void setup() {
		super.setup();    

		vowlDetailsPortlet = (VOWLDetailsPortlet) getPortletByClassName(VOWLDetailsPortlet.class.getName());
		vowlSelectionDetailsPortlet = (VOWLSelectionDetailsPortlet) getPortletByClassName(VOWLSelectionDetailsPortlet.class.getName());
		vowlVisualizationPortlet = (VOWLVisualizationPortlet)getPortletByClassName(VOWLVisualizationPortlet.class.getName());
		vowlControlPortlet = (VOWLControlPortlet)getPortletByClassName(VOWLControlPortlet.class.getName());
		// Hook up details portlet to listen to changes in visualization (graph) portlet
		//vowlVisualizationPortlet.addSelectionListener(vowlDetailsPortlet);
		vowlVisualizationPortlet.addSelectionListener(vowlSelectionDetailsPortlet);
		vowlVisualizationPortlet.addGraphListener(vowlDetailsPortlet);
		vowlDetailsPortlet.addChangeListener(vowlVisualizationPortlet);
		vowlControlPortlet.addSelectionListener(vowlVisualizationPortlet);
	}
}
