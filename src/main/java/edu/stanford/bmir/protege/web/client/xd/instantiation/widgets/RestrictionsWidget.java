package edu.stanford.bmir.protege.web.client.xd.instantiation.widgets;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;

import edu.stanford.bmir.protege.web.client.xd.instantiation.old.restriction.Restriction;
import edu.stanford.bmir.protege.web.client.xd.instantiation.panels.RestrictionsPanel;

public class RestrictionsWidget extends HorizontalPanel {

	private final RestrictionsPanel parentRestrictionsPanel;
	private final CheckBox checkBox;
	private final Restriction restriction;
	
	public RestrictionsWidget(RestrictionsPanel parentRestrictionsPanel, Restriction restriction) {
		this.parentRestrictionsPanel = parentRestrictionsPanel;
		this.restriction = restriction;
		
		this.addStyleName("xdpRestrictionsWidget");
		
		String checkBoxLabel = "<strong>heyhey</strong>" + this.restriction.toString();
		this.checkBox = new CheckBox(checkBoxLabel,true);
		
		this.checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				RestrictionsWidget.this.parentRestrictionsPanel.setRestrictionSelectedStatus(RestrictionsWidget.this.restriction, event.getValue());
			}
		});
		
		this.add(checkBox);
		
	}
}
