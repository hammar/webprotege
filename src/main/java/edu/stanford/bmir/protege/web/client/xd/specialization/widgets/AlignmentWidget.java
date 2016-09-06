package edu.stanford.bmir.protege.web.client.xd.specialization.widgets;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;

import edu.stanford.bmir.protege.web.client.xd.specialization.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;

public class AlignmentWidget extends HorizontalPanel {
	
	private final DesignPatternInstantiationWizard parentWizard;
	private final CheckBox checkBox;
	private final Alignment alignment;
	
	public AlignmentWidget(DesignPatternInstantiationWizard parentWizard, Alignment alignment) {
		this.parentWizard = parentWizard;
		this.alignment = alignment;
		
		this.addStyleName("xdpAlignmentWidget");
		
		this.checkBox = new CheckBox(alignment.toString());
		this.checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (event.getValue()==true) {
					AlignmentWidget.this.parentWizard.addAlignment(AlignmentWidget.this.alignment);
				}
				else {
					AlignmentWidget.this.parentWizard.removeAlignment(AlignmentWidget.this.alignment);
				}
			}
		});
		this.add(checkBox);
	}
}
