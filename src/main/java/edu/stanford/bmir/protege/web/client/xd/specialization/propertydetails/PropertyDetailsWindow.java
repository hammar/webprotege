package edu.stanford.bmir.protege.web.client.xd.specialization.propertydetails;

import com.google.gwt.user.client.Window;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;

import edu.stanford.bmir.protege.web.client.xd.specialization.DetailsWindow;
import edu.stanford.bmir.protege.web.client.xd.specialization.XdSpecializationWizard;

public abstract class PropertyDetailsWindow extends DetailsWindow {
	
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
	
	protected SimpleStore getDomainStore() {
		return new SimpleStore("label", parentWizard.getLeafClasses());
	}
	
	protected abstract SimpleStore getRangeStore();
	
	public void show() {
		rangeStore = getRangeStore();
		propertyRange.setStore(rangeStore);
		
		domainStore = getDomainStore();//
		propertyDomain.setStore(domainStore);
		super.show();
	}
	
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
		
		//domainStore.load();
		
		//rangeStore.load();
		
		formPanel = new FormPanel();
		this.add(formPanel);
		
		propertyName = new TextField("Property name", "propertyName");
		propertyName.setAllowBlank(false);
		formPanel.add(propertyName);  
		
		propertyComment = new TextField("Comment", "propertyComment");
		propertyComment.setAllowBlank(true);
		formPanel.add(propertyComment);
		
		propertyDomain = new ComboBox("Domain","domain");
		propertyDomain.setDisplayField("label");  
		propertyDomain.setMode(ComboBox.LOCAL);    
		propertyDomain.setForceSelection(true);
		formPanel.add(propertyDomain);
		
		propertyRange = new ComboBox("Range","range");
		propertyRange.setDisplayField("label");
		propertyRange.setMode(ComboBox.LOCAL);    
		propertyRange.setForceSelection(true);
		formPanel.add(propertyRange);
	}
}
