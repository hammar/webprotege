package edu.stanford.bmir.protege.web.client.ui.visualization.vowl;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public interface VOWLSelectionDetailsView extends IsWidget {

	void renderDetailsDynamicInfo(VerticalPanel vPanel, String header);
	Widget getWidget();
}
