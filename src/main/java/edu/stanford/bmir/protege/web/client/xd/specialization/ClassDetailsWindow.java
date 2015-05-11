package edu.stanford.bmir.protege.web.client.xd.specialization;

import org.semanticweb.owlapi.model.OWLEntity;

import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.tree.TreeNode;

public class ClassDetailsWindow extends DetailsWindow {
	
	XdSpecializationWizard parentWizard;
	FormPanel formPanel;
	TextField className;
	TextField classComment;
       
     
	public ClassDetailsWindow(XdSpecializationWizard parentWizard) {
		super();
		this.parentWizard = parentWizard;
	}
	
	@Override
	public void reset() {
		// TODO: Write this
	}

	@Override
	public void load(OWLEntity entity) {
		// TODO Auto-generated method stub
		
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

	@Override
	public void persistAndClose() {
		// TODO Auto-generated method stub
		TreeNode selectedClass = parentWizard.getSelectedClass();
		selectedClass.appendChild(new TreeNode("Test"));
		this.hide();
	}
	
}
