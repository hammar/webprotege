package edu.stanford.bmir.protege.web.client.xd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.CheckboxListener;
import com.gwtext.client.widgets.form.event.CheckboxListenerAdapter;
import com.gwtext.client.widgets.form.event.TextFieldListenerAdapter;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.event.GridRowListenerAdapter;
import com.gwtext.client.widgets.layout.FormLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;

import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.rpc.data.EntityData;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;
import edu.stanford.bmir.protege.web.client.ui.selection.SelectionEvent;

/***
 * Portlet providing an ODP search GUI.
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
@SuppressWarnings("unchecked")
public class XdSearchPortlet extends AbstractOWLEntityPortlet {
	
	public XdSearchPortlet(Project project) {
		super(project);
	}
	
	private TextField queryField;
	private Button searchButton;
	//private Button searchOptionsButton;
	//private Label resultLabel;
	
	// Search mapping filters
	Checkbox allMappingsCheck;
	Checkbox dolceMappingCheck;
	Checkbox schemaOrgMappingCheck;
	Checkbox dbpediaMappingCheck;
	
	// Results widget
	Store resultsStore;
	ArrayReader resultsReader;
	GridPanel resultsGrid;
	ColumnModel columnModel;
	
	@Override
	public Collection<EntityData> getSelection() {
		if (resultsGrid.getSelectionModel().hasSelection()) {
			Record r = resultsGrid.getSelectionModel().getSelected();
			String selectedOdp = r.getAsString("uri");
			return Arrays.asList(new EntityData(selectedOdp));
		}
		else {
			return null;
		}
	}

	@Override
	public void reload() {
		setTitle("ODP Search Reloaded!");
	}

	// Initialization method for GUI
	@Override
	public void initialize() {
		
		setTitle("ODP Search");
		
		// TODO: get reasonable sizes in width and height of the widgets. 
		
		// Create the search form  
        FormPanel formPanel = new FormPanel(); 
        formPanel.setTitle("Search controls");  
        formPanel.setPaddings(5, 5, 5, 0);  
        formPanel.setAutoWidth(true);
        formPanel.setAutoHeight(true);
        formPanel.setLabelWidth(75);
        
        // Add query field
        queryField = new TextField("CQ");
        formPanel.add(queryField);
        
        // Create filters FieldSet and child tab panel
        FieldSet filtersFS = new FieldSet("Filters");  
        filtersFS.setCollapsible(true);  
        filtersFS.setAutoWidth(true);
        filtersFS.setCollapsed(true);
        TabPanel filterTabs = new TabPanel(); 
        filterTabs.setPlain(true);  
        filterTabs.setActiveTab(0);  
        filterTabs.setHeight(235); 
        
        // Add first filter tab ("Core", containing selectors)
        Panel coreTab = new Panel();
        coreTab.setTitle("Core");
        coreTab.setLayout(new FormLayout());  
        coreTab.setPaddings(10);  
  
        // Create category selection combobox
        Store categoryStore = new SimpleStore(new String[]{"name", "uri"}, getODPCategories());  
        categoryStore.load();  
        ComboBox categoryCb = new ComboBox();  
        categoryCb.setFieldLabel("Category");    
        categoryCb.setStore(categoryStore);  
        categoryCb.setDisplayField("name");  
        categoryCb.setMode(ComboBox.LOCAL);    
        categoryCb.setForceSelection(true);
        categoryCb.setReadOnly(true);
        coreTab.add(categoryCb); 
        
        // Create size selection combobox
        Store sizeStore = new SimpleStore(new String[]{"label","abbr"}, new String[][]{
        		new String[]{"Any","any"},
        		new String[]{"Small","s"},
        		new String[]{"Medium","m"},
        		new String[]{"Large","l"}}
        		);
        sizeStore.load();
        ComboBox sizeCb = new ComboBox();
        sizeCb.setFieldLabel("Size");
        sizeCb.setStore(sizeStore);
        sizeCb.setDisplayField("label");
        sizeCb.setMode(ComboBox.LOCAL);
        sizeCb.setForceSelection(true);
        sizeCb.setReadOnly(true);
        coreTab.add(sizeCb);
        
        // Create profile selection combobox
        Store profileStore = new SimpleStore(new String[]{"label","abbr"}, new String[][]{
        		new String[]{"Any","any"},
        		new String[]{"OWL Horst","horst"},
        		new String[]{"OWL2 EL","el"},
        		new String[]{"OWL2 RL","rl"},
        		new String[]{"OWL2 QL","ql"},
        		new String[]{"OWL2 DL","dl"}}
        		);
        profileStore.load();
        ComboBox profileCb = new ComboBox();
        profileCb.setFieldLabel("Profile");
        profileCb.setStore(profileStore);
        profileCb.setDisplayField("label");
        profileCb.setMode(ComboBox.LOCAL);
        profileCb.setForceSelection(true);
        profileCb.setReadOnly(true);
        coreTab.add(profileCb);
        
        // Create strategy selection combobox
        Store strategyStore = new SimpleStore(new String[]{"label","abbr"}, new String[][]{
        		new String[]{"Any","any"},
        		new String[]{"Class-oriented","class"},
        		new String[]{"Property-oriented","property"},
        		new String[]{"Hybrid","hybrid"}}
        		);
        strategyStore.load();
        ComboBox strategyCb = new ComboBox();
        strategyCb.setFieldLabel("Strategy");
        strategyCb.setStore(strategyStore);
        strategyCb.setDisplayField("label");
        strategyCb.setMode(ComboBox.LOCAL);
        strategyCb.setForceSelection(true);
        strategyCb.setReadOnly(true);
        coreTab.add(strategyCb);
        
        // Add second filter tab (containing alignment filters)
        Panel mappingsTab = new Panel();
        mappingsTab.setTitle("Mappings");
        mappingsTab.setLayout(new FormLayout());  
        
        // Listener that ensures that either the "Any" checkbox or one of the specific
        // mappings checkboxes is ticked.
        CheckboxListener mappingCheckboxListener = new CheckboxListenerAdapter() {
        	public void onCheck(Checkbox field, boolean checked)  {
        		if (dolceMappingCheck.getValue() || 
        				schemaOrgMappingCheck.getValue() || 
        				dbpediaMappingCheck.getValue()) {
        			allMappingsCheck.setChecked(false);
        		}
        		else {
        			allMappingsCheck.setChecked(true);
        		}
        	}
        };
        
        // A bunch of mapping checkboxes
        allMappingsCheck = new Checkbox();
        allMappingsCheck.setFieldLabel("Any");
        allMappingsCheck.setChecked(true);
        mappingsTab.add(allMappingsCheck);
        
        dolceMappingCheck = new Checkbox();
        dolceMappingCheck.setFieldLabel("DOLCE");
        dolceMappingCheck.addListener(mappingCheckboxListener);
        mappingsTab.add(dolceMappingCheck);
        
        schemaOrgMappingCheck = new Checkbox();
        schemaOrgMappingCheck.setFieldLabel("Schema.org");
        schemaOrgMappingCheck.addListener(mappingCheckboxListener);
        mappingsTab.add(schemaOrgMappingCheck);
        
        dbpediaMappingCheck = new Checkbox();
        dbpediaMappingCheck.setFieldLabel("DBpedia");
        dbpediaMappingCheck.addListener(mappingCheckboxListener);
        mappingsTab.add(dbpediaMappingCheck);
        
        // Add tabs to tabpanel, tabpanel to filters fieldset, 
        // and filters fieldset to form.
        filterTabs.add(coreTab);
        filterTabs.add(mappingsTab);
        filtersFS.add(filterTabs);
        formPanel.add(filtersFS);
  
        // Finally, add the search button.
        searchButton = new Button("Search");
		searchButton.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				runOdpSearch();
			}
		});
        formPanel.addButton(searchButton);   
		
		// Results list
        resultsReader = new ArrayReader(new RecordDef(
     		   new FieldDef[]{
     		     new StringFieldDef("uri"),
     		     new StringFieldDef("title")
     		     }
     		   ));
        resultsStore = new Store(resultsReader);
        ColumnConfig[] columns = new ColumnConfig[]{  
                new ColumnConfig("Name", "title"),  
                new ColumnConfig("URI", "uri"),  
        };
        columnModel = new ColumnModel(columns);
        resultsGrid = new GridPanel(resultsStore,columnModel);
        resultsGrid.setHeight(300);
        Panel resultsPanel = new Panel("Results list");
        resultsPanel.add(resultsGrid);
		
		// Main portlet layout using a GWT-EXT accordion panel
		Panel mainPanel = new Panel();
        mainPanel.setLayout(new VerticalLayout(15));
        mainPanel.setAutoWidth(true);
        mainPanel.add(formPanel);
        mainPanel.add(resultsPanel);
		add(mainPanel);
        
        // Enter-press in query field behavior
		queryField.addListener(new TextFieldListenerAdapter(){
			public void onSpecialKey(Field field, EventObject e) {
				if (e.getKey() == KeyCodes.KEY_ENTER) {
					runOdpSearch();
				}
			}
		});
        
        // Behavior when a result from the search is clicked - notifies listeners
        // that selection changed.
		resultsGrid.addGridRowListener(new GridRowListenerAdapter(){
			@SuppressWarnings("deprecation")
			public void onRowClick(GridPanel grid, int rowIndex, EventObject e) {
				notifySelectionListeners(new SelectionEvent(XdSearchPortlet.this));
			}
		});
	}
	
	// Runs the ODP search on the server using the query string and populates the results list.
	// TODO: Plug in the search filters from the GUI form also.
	private void runOdpSearch() {
		
		XdServiceManager.getInstance().getOdpSearchContent(queryField.getText(), new AsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("Changes saved successfully");
				MessageBox.alert("Error", "GWT-RPC call failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<String> results) {
				List<String[]> tempList = new ArrayList<String[]>();
				for (String odp: results) {
					tempList.add(new String[]{odp,"Temp title"});
				}
				String[][] newData = new String[tempList.size()][]; 
				newData = tempList.toArray(newData);
				MemoryProxy tempProxy = new MemoryProxy(newData); 
				Store tempStore = new Store(tempProxy, resultsReader);
				tempStore.load();
				resultsStore.removeAll();
				resultsStore.add(tempStore.getRecords());
				resultsStore.commitChanges();
			}
		});
	}
	
	private String[][] getODPCategories() {
		return new String[][]{
				new String[]{"Any", "http://ontologydesignpatterns.orgg/wiki/Community:ANY"},  
				new String[]{"Academy", "http://ontologydesignpatterns.org/wiki/Community:Academy"},
				new String[]{"Agriculture", "http://ontologydesignpatterns.org/wiki/Community:Agriculture"},
				new String[]{"Biology", "http://ontologydesignpatterns.org/wiki/Community:Biology"},
				new String[]{"Business", "http://ontologydesignpatterns.org/wiki/Community:Business"}
		};
	}
}
