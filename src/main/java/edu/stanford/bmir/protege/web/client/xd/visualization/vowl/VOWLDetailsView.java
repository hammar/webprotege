package edu.stanford.bmir.protege.web.client.xd.visualization.vowl;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public interface VOWLDetailsView extends IsWidget {

	void renderDetailsStaticInfo(VOWLVisualizationJso visualizationJso);
	Widget getWidget();
	ListBox getListBox();
}
