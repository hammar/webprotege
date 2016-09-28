package edu.stanford.bmir.protege.web.client.xd.instantiation.widgets;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;

import edu.stanford.bmir.protege.web.client.xd.instantiation.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.shared.xd.data.restrictions.Restriction;

public class RestrictionsWidget extends HorizontalPanel {

	private final DesignPatternInstantiationWizard parentWizard;
	private final CheckBox checkBox;
	private final Restriction restriction;
	
	public RestrictionsWidget(DesignPatternInstantiationWizard parentWizard, Restriction restriction) {
		this.parentWizard = parentWizard;
		this.restriction = restriction;
		
		this.addStyleName("xdpRestrictionsWidget");
		
		String checkBoxLabel = this.restriction.toString();
		this.checkBox = new CheckBox(checkBoxLabel,true);
		
		this.checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (event.getValue()==true) {
					RestrictionsWidget.this.parentWizard.addRestriction(RestrictionsWidget.this.restriction);
				}
				else {
					RestrictionsWidget.this.parentWizard.removeRestriction(RestrictionsWidget.this.restriction);
				}
				
			}
		});
		
		this.add(checkBox);
		
	}
}
