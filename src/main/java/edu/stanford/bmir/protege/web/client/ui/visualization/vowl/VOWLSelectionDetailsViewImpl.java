package edu.stanford.bmir.protege.web.client.ui.visualization.vowl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class VOWLSelectionDetailsViewImpl extends Composite implements VOWLSelectionDetailsView {

	private static VOWLSelectionDetailsViewImplUiBinder uiBinder = GWT
			.create(VOWLSelectionDetailsViewImplUiBinder.class);

	public static String ontologyAsJSONStr;

	private HTMLPanel rootElement;

	protected VerticalPanel selectionDetailsPanel;

	interface VOWLSelectionDetailsViewImplUiBinder extends
	UiBinder<HTMLPanel, VOWLSelectionDetailsViewImpl> {
	}

	public VOWLSelectionDetailsViewImpl() {
		rootElement = uiBinder.createAndBindUi(this);
		initWidget(rootElement);

		selectionDetailsPanel = new VerticalPanel();
	}


	/**
	 * This renders details for the selected element, either a node or a label. 
	 * @param vPanel The panel that describes the attributes of the selected element.
	 * @param header The header of the panel.
	 */
	public void renderDetailsDynamicInfo(VerticalPanel vPanel, String header) {
		if(rootElement.getWidgetCount() > 0)
			rootElement.remove(0);
		rootElement.add(vPanel);
		
	}
	
	@Override
	public Widget getWidget() {
		return this;
	}

}
