package edu.stanford.bmir.protege.web.client.xd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.dom.client.KeyCodes;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.FloatFieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
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
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.FormLayout;
import com.gwtext.client.widgets.layout.RowLayout;
import com.gwtext.client.widgets.layout.RowLayoutData;

import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;
import edu.stanford.bmir.protege.web.client.xd.selection.Selectable;
import edu.stanford.bmir.protege.web.client.xd.selection.SelectionEvent;
import edu.stanford.bmir.protege.web.client.xd.selection.SelectionListener;
import edu.stanford.bmir.protege.web.shared.selection.SelectionModel;
import edu.stanford.bmir.protege.web.shared.xd.OdpSearchFilterConfiguration;
import edu.stanford.bmir.protege.web.shared.xd.OdpSearchResult;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetOdpSearchHitsAction;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpSearchHitsResult;

/***
 * Portlet providing an ODP search GUI.
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
@SuppressWarnings("unchecked")
public class XdSearchPortlet extends AbstractOWLEntityPortlet implements Selectable {
	
	public XdSearchPortlet(SelectionModel selectionModel, Project project) {
		super(selectionModel, project);
		this.listeners = new ArrayList<SelectionListener>();
	}
	
	private TextField queryField;
	private Button searchButton;
	
	// Core search filters
	private ComboBox categoryCb;
	private ComboBox sizeCb;
	private ComboBox profileCb;
	private ComboBox strategyCb;
	
	// Search mapping filters
	private Checkbox allMappingsCheck;
	private Checkbox dolceMappingCheck;
	private Checkbox schemaOrgMappingCheck;
	private Checkbox dbpediaMappingCheck;
	
	// Results widget
	private Store resultsStore;
	private ArrayReader resultsReader;
	private GridPanel resultsGrid;
	private ColumnModel columnModel;
	
	// Listeners to selection events in this portlet
	private Collection<SelectionListener> listeners;
	
	@Override
	public List<String> getSelection() {
		if (resultsGrid.getSelectionModel().hasSelection()) {
			Record r = resultsGrid.getSelectionModel().getSelected();
			String selectedOdp = r.getAsString("uri");
			return Arrays.asList(selectedOdp);
		}
		else {
			return Collections.emptyList();
		}
	}

	// Initialization method for GUI
	@Override
	public void initialize() {
		
		setTitle("ODP Search");
		
		// Create the search form  
        FormPanel formPanel = new FormPanel(); 
        formPanel.setTitle("Search controls");  
        formPanel.setPaddings(5, 5, 5, 0);
        formPanel.setAutoWidth(true);
        formPanel.setAutoHeight(true);
        formPanel.setLabelWidth(65);
        
        // Add query field
        queryField = new TextField("Query");
        queryField.setWidth(145);
        formPanel.add(queryField);
        
        // Create filters FieldSet and child tab panel
        FieldSet filtersFS = new FieldSet("Filters");
        filtersFS.setLayout(new FitLayout());
        filtersFS.setCollapsible(true);  
        filtersFS.setCollapsed(true);
        TabPanel filterTabs = new TabPanel(); 
        filterTabs.setPlain(true);  
        filterTabs.setActiveTab(0);
        filterTabs.setHeight(150); 
        
        // Add first filter tab ("Core", containing selectors)
        Panel coreTab = new Panel();
        coreTab.setTitle("Core");
        coreTab.setLayout(new FormLayout());
        coreTab.setPaddings(5);
  
        // Create category selection combobox
        Store categoryStore = new SimpleStore(new String[]{"name", "uri"}, getODPCategories());  
        categoryStore.load();  
        categoryCb = new ComboBox();  
        categoryCb.setFieldLabel("Category");    
        categoryCb.setStore(categoryStore);  
        categoryCb.setDisplayField("name");  
        categoryCb.setMode(ComboBox.LOCAL);    
        categoryCb.setForceSelection(true);
        categoryCb.setReadOnly(true);
        categoryCb.setWidth(120);
        coreTab.add(categoryCb); 
        
        // Create size selection combobox
        Store sizeStore = new SimpleStore(new String[]{"label","abbr"}, new String[][]{
        		new String[]{"Any","any"},
        		new String[]{"Small","s"},
        		new String[]{"Medium","m"},
        		new String[]{"Large","l"}}
        		);
        sizeStore.load();
        sizeCb = new ComboBox();
        sizeCb.setFieldLabel("Size");
        sizeCb.setStore(sizeStore);
        sizeCb.setDisplayField("label");
        sizeCb.setMode(ComboBox.LOCAL);
        sizeCb.setForceSelection(true);
        sizeCb.setReadOnly(true);
        sizeCb.setWidth(120);
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
        profileCb = new ComboBox();
        profileCb.setFieldLabel("Profile");
        profileCb.setStore(profileStore);
        profileCb.setDisplayField("label");
        profileCb.setMode(ComboBox.LOCAL);
        profileCb.setForceSelection(true);
        profileCb.setReadOnly(true);
        profileCb.setWidth(120);
        coreTab.add(profileCb);
        
        // Create strategy selection combobox
        Store strategyStore = new SimpleStore(new String[]{"label","abbr"}, new String[][]{
        		new String[]{"Any","any"},
        		new String[]{"Class-oriented","class"},
        		new String[]{"Property-oriented","property"},
        		new String[]{"Hybrid","hybrid"}}
        		);
        strategyStore.load();
        strategyCb = new ComboBox();
        strategyCb.setFieldLabel("Strategy");
        strategyCb.setStore(strategyStore);
        strategyCb.setDisplayField("label");
        strategyCb.setMode(ComboBox.LOCAL);
        strategyCb.setForceSelection(true);
        strategyCb.setReadOnly(true);
        strategyCb.setWidth(120);
        coreTab.add(strategyCb);
        
        // Add second filter tab (containing alignment filters)
        Panel mappingsTab = new Panel();
        mappingsTab.setTitle("Mappings");
        mappingsTab.setPaddings(5);
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
        schemaOrgMappingCheck.setFieldLabel("Schema");
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
     		     new StringFieldDef("title"),
     		     new StringFieldDef("uri"),
     		     new FloatFieldDef("confidence")
     		     }
     		   ));
        resultsStore = new Store(resultsReader);
        ColumnConfig nameColumn = new ColumnConfig("Name", "title");
        ColumnConfig confidenceColumn = new ColumnConfig("Confidence", "confidence");
        confidenceColumn.setSortable(true);
        confidenceColumn.setWidth(90);
        ColumnConfig[] columns = new ColumnConfig[]{  
        		nameColumn,  
                confidenceColumn  
        };
        columnModel = new ColumnModel(columns);
        resultsGrid = new GridPanel(resultsStore,columnModel);
        resultsGrid.setEnableHdMenu(false);
        resultsGrid.setEnableColumnMove(false);
        resultsGrid.setEnableColumnResize(false);
        resultsGrid.setTitle("Results list");
        resultsGrid.setAutoExpandColumn(nameColumn.getId());
		
		// Main portlet layout using a GWT-EXT accordion panel
		Panel mainPanel = new Panel();
		mainPanel.setLayout(new RowLayout());
        mainPanel.add(formPanel);
        mainPanel.add(resultsGrid, new RowLayoutData("75%"));
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
			public void onRowClick(GridPanel grid, int rowIndex, EventObject e) {
				notifySelectionListeners(new SelectionEvent(XdSearchPortlet.this));
			}
		});
	}
	
	// Runs the ODP search on the server using the query string and populates the results list.
	private void runOdpSearch() {
		
		// Set up filter object
		OdpSearchFilterConfiguration filterConfiguration = new OdpSearchFilterConfiguration();
		if (categoryCb.getValue() != null) {
			filterConfiguration.setCategory(categoryCb.getValue());
		}
		if (sizeCb.getValue() != null) {
			filterConfiguration.setSize(sizeCb.getValue());
		}
		if (profileCb.getValue() != null) {
			filterConfiguration.setProfile(profileCb.getValue());
		}
		if (strategyCb.getValue() != null) {
			filterConfiguration.setStrategy(strategyCb.getValue());
		}
		filterConfiguration.setDolceMappingRequired(dolceMappingCheck.getValue());
		filterConfiguration.setSchemaOrgMappingRequired(schemaOrgMappingCheck.getValue());
		filterConfiguration.setDbPediaMappingRequired(dbpediaMappingCheck.getValue());
		
		// Execute remote query method
		DispatchServiceManager.get().execute(new GetOdpSearchHitsAction(queryField.getText(), filterConfiguration), 
				new DispatchServiceCallback<GetOdpSearchHitsResult>() {
			@Override
			public void handleSuccess(GetOdpSearchHitsResult result) {
				// Populate results list by creating a new temporary data store using the existing 
				// reader configuration, then moving records from that temporary store to the one
				// that actually backs the GridPanel. Seems inefficient but was easiest to implement
				// in code and there's not that much volume of data that it'll become a problem anyhow.
				// TODO: Clean this up and actually use the store as intended by its designers.
				List<OdpSearchResult> searchHits = result.getSearchResults();
				List<Object[]> tempList = new ArrayList<Object[]>();
				for (OdpSearchResult hit: searchHits) {
					tempList.add(new Object[]{hit.getOdp().getName(),hit.getOdp().getUri(),hit.getConfidence()});
				}
				Object[][] newData = new Object[tempList.size()][]; 
				newData = tempList.toArray(newData);
				MemoryProxy tempProxy = new MemoryProxy(newData); 
				Store tempStore = new Store(tempProxy, resultsReader);
				tempStore.load();
				resultsStore.removeAll();
				resultsStore.add(tempStore.getRecords());
				resultsStore.sort("confidence", SortDir.DESC);
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

	/* ---- Selectable implementation methods ----*/
	@Override
	public void notifySelectionListeners(final SelectionEvent selectionEvent) {
		for (SelectionListener listener: listeners) {
			listener.selectionChanged(new SelectionEvent(this));
		}
	}
	
	@Override
	public void addSelectionListener(SelectionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeSelectionListener(SelectionListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void setSelection(Collection<? extends Object> selection) {
		// We don't allow external sources to modify the selection of this portlet.
	}
}
