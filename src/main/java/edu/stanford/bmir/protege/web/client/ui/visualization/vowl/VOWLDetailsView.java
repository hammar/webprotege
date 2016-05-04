package edu.stanford.bmir.protege.web.client.ui.visualization.vowl;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public interface VOWLDetailsView extends IsWidget {

	//void renderDetailsDynamicInfo(VerticalPanel vPanel, String header);
	void renderDetailsStaticInfo(VOWLVisualizationJso visualizationJso);
	Widget getWidget();
	ListBox getListBox();
}
