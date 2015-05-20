package edu.stanford.bmir.protege.web.client.xd.specialization.classdetails;

import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;

import edu.stanford.bmir.protege.web.client.xd.specialization.DetailsWindow;
import edu.stanford.bmir.protege.web.client.xd.specialization.XdSpecializationWizard;

public abstract class ClassDetailsWindow extends DetailsWindow {
	
	XdSpecializationWizard parentWizard;
	FormPanel formPanel;
	TextField className;
	TextField classComment;
     
	public ClassDetailsWindow(XdSpecializationWizard parentWizard) {
		super(parentWizard);
	}
	
	@Override
	public void reset() {
		className.setValue("");
		classComment.setValue("");
	}

	@Override
	public void initialize() {
		formPanel = new FormPanel();
		this.add(formPanel);
		
		className = new TextField("Class name", "className");
		className.setAllowBlank(false);
		formPanel.add(className);  
		
		classComment = new TextField("Comment", "classComment");
		classComment.setAllowBlank(true);
		formPanel.add(classComment);
	}
}
