package edu.stanford.bmir.protege.web.client.xd.specialization.propertydetails;

import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;

import edu.stanford.bmir.protege.web.client.xd.specialization.DetailsWindow;
import edu.stanford.bmir.protege.web.client.xd.specialization.XdSpecializationWizard;

public abstract class PropertyDetailsWindow extends DetailsWindow {
	
	XdSpecializationWizard parentWizard;
	FormPanel formPanel;
	TextField propertyName;
	TextField propertyComment;
	ComboBox propertyDomain;
	ComboBox propertyRange;
     
	Store domainStore;
	Store rangeStore;
	
	public PropertyDetailsWindow(XdSpecializationWizard parentWizard) {
		super(parentWizard);
	}
	
	// TODO: fix this: make list one-dimensional and return only things that exist in parent class tree
	protected String[][] getDomains() {
		return new String[][]{
				new String[]{"Any", "http://ontologydesignpatterns.orgg/wiki/Community:ANY"},  
				new String[]{"Academy", "http://ontologydesignpatterns.org/wiki/Community:Academy"},
				new String[]{"Agriculture", "http://ontologydesignpatterns.org/wiki/Community:Agriculture"},
				new String[]{"Biology", "http://ontologydesignpatterns.org/wiki/Community:Biology"},
				new String[]{"Business", "http://ontologydesignpatterns.org/wiki/Community:Business"}
		};
	}
	
	protected abstract String[][] getRanges();
	
	@Override
	public void reset() {
		propertyName.setValue("");
		propertyComment.setValue("");
		// TODO: clean up the below?
		propertyDomain.setValue("");
		propertyRange.setValue("");
	}

	@Override
	public void initialize() {
		domainStore = new SimpleStore(new String[]{"name", "uri"}, getDomains());
		rangeStore = new SimpleStore(new String[]{"name", "uri"}, getRanges());
		
		formPanel = new FormPanel();
		this.add(formPanel);
		
		propertyName = new TextField("Property name", "propertyName");
		propertyName.setAllowBlank(false);
		formPanel.add(propertyName);  
		
		propertyComment = new TextField("Comment", "propertyComment");
		propertyComment.setAllowBlank(true);
		formPanel.add(propertyComment);
		
		propertyDomain = new ComboBox("Domain","domain");
		formPanel.add(propertyDomain);
		
		propertyRange = new ComboBox("Range","range");
		formPanel.add(propertyRange);
	}
}
