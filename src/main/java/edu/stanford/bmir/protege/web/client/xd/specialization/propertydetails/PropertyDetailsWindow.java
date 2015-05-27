package edu.stanford.bmir.protege.web.client.xd.specialization.propertydetails;

import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
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
    
	RecordDef domainRecordDef;
	RecordDef rangeRecordDef;
	Store domainStore;
	Store rangeStore;
	
	
	
	public PropertyDetailsWindow(XdSpecializationWizard parentWizard) {
		super(parentWizard);
	}
	
	protected String[] getDomains() {
		return parentWizard.getDisplayedClassLabels();
	}
	
	protected abstract String[][] getRanges();
	
	public void show() {
		// Update range store for combobox
		String[][] ranges = getRanges();
		rangeStore.removeAll();
		for (String[] range: ranges)
		{
			Record newRangeRecord = rangeRecordDef.createRecord(range);
			rangeStore.add(newRangeRecord);
		}
		rangeStore.commitChanges();
		
		// Update domainStore for combobox
		String[] domains = getDomains();
		domainStore.removeAll();
		for (String domain: domains) {
			Record newDomainRecard = domainRecordDef.createRecord(new String[]{domain});
			domainStore.add(newDomainRecard);
		}
		domainStore.commitChanges();
		
		super.show();
	}
	
	@Override
	public void reset() {
		propertyName.setValue("");
		propertyComment.setValue("");
		propertyDomain.setValue("");
		propertyRange.setValue("");
	}

	@Override
	public void initialize() {
		
		// Two fields as datatype properties have both readable 
		// short forms and IRI values for their ranges
		rangeRecordDef = new RecordDef(
                new FieldDef[]{
                		new StringFieldDef("label"),
                		new StringFieldDef("iri")
                });
		ArrayReader rangeReader = new ArrayReader(rangeRecordDef);
		rangeStore = new Store(rangeReader);
		
		// One field as domain classes do not at this point 
		// actually have IRIs in the ontology project
		domainRecordDef = new RecordDef(
                new FieldDef[]{
                		new StringFieldDef("label")
                });
		ArrayReader domainReader = new ArrayReader(domainRecordDef);
		domainStore = new Store(domainReader);
		
		formPanel = new FormPanel();
		this.add(formPanel);
		
		propertyName = new TextField("Property name", "propertyName");
		propertyName.setAllowBlank(false);
		formPanel.add(propertyName);  
		
		propertyComment = new TextField("Comment", "propertyComment");
		propertyComment.setAllowBlank(true);
		formPanel.add(propertyComment);
		
		propertyDomain = new ComboBox();
		propertyDomain.setForceSelection(true);  
		propertyDomain.setMinChars(1);
		propertyDomain.setFieldLabel("Domain");  
		propertyDomain.setStore(domainStore);  
		propertyDomain.setDisplayField("label");  
        propertyDomain.setMode(ComboBox.LOCAL);  
        propertyDomain.setTriggerAction(ComboBox.ALL);  
        propertyDomain.setEmptyText("Enter domain");  
        propertyDomain.setLoadingText("Searching...");  
        propertyDomain.setTypeAhead(true);  
        propertyDomain.setSelectOnFocus(true);  
        propertyDomain.setHideTrigger(false);
		formPanel.add(propertyDomain);
		
		propertyRange = new ComboBox();
		propertyRange.setForceSelection(true);  
		propertyRange.setMinChars(1);
		propertyRange.setFieldLabel("Range");  
		propertyRange.setStore(rangeStore);  
		propertyRange.setDisplayField("label");  
		propertyRange.setMode(ComboBox.LOCAL);  
		propertyRange.setTriggerAction(ComboBox.ALL);  
        propertyRange.setEmptyText("Enter range");  
        propertyRange.setLoadingText("Searching...");  
        propertyRange.setTypeAhead(true);  
        propertyRange.setSelectOnFocus(true);  
        propertyRange.setHideTrigger(false);
		formPanel.add(propertyRange);
	}
}
