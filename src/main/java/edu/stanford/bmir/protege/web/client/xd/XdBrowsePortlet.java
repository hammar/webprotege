package edu.stanford.bmir.protege.web.client.xd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
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
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.event.GridRowListenerAdapter;
import com.gwtext.client.widgets.layout.RowLayout;
import com.gwtext.client.widgets.layout.RowLayoutData;

import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.rpc.data.EntityData;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;
import edu.stanford.bmir.protege.web.client.ui.selection.SelectionEvent;
import edu.stanford.bmir.protege.web.shared.xd.OdpDetails;

/***
 * Portlet providing an ODP search GUI.
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
@SuppressWarnings("unchecked")
public class XdBrowsePortlet extends AbstractOWLEntityPortlet {
	
	public XdBrowsePortlet(Project project) {
		super(project);
	}
		
	// Core search filters
	private ComboBox categoryCb;

	// Results widget
	private Store resultsStore;
	private ArrayReader resultsReader;
	private GridPanel resultsGrid;
	private ColumnModel columnModel;
	
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
		setTitle("ODP Browser Reloaded!");
	}

	// Initialization method for GUI
	@Override
	public void initialize() {
		
		setTitle("ODP Browser");
		
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
        categoryCb.addListener(new ComboBoxListenerAdapter(){
        	
			@Override
			public void onSelect(ComboBox comboBox, Record record, int index) {
				populateOdpList(comboBox.getValue());
			}
        });
		
		// Results list
        resultsReader = new ArrayReader(new RecordDef(
     		   new FieldDef[]{
     		     new StringFieldDef("title"),
     		     new StringFieldDef("uri"),
     		     new FloatFieldDef("confidence")
     		     }
     		   ));
        resultsStore = new Store(resultsReader);
        ColumnConfig nameColumn = new ColumnConfig("Name", "title",100,true);
        ColumnConfig uriColumn = new ColumnConfig("URI", "uri");
        ColumnConfig[] columns = new ColumnConfig[]{  
        		nameColumn,  
                uriColumn  
        };
        columnModel = new ColumnModel(columns);
        resultsGrid = new GridPanel(resultsStore,columnModel);
        resultsGrid.setEnableHdMenu(false);
        resultsGrid.setEnableColumnMove(false);
        //resultsGrid.setEnableColumnResize(false);
        resultsGrid.setTitle("Pattern list");
        resultsGrid.setAutoExpandColumn(uriColumn.getId());
		
		// Main portlet layout using a GWT-EXT accordion panel
		Panel mainPanel = new Panel();
		mainPanel.setLayout(new RowLayout());
        mainPanel.add(categoryCb, new RowLayoutData("18"));
        mainPanel.add(resultsGrid);
		add(mainPanel);
        
        
        // Behavior when a result from the search is clicked - notifies listeners
        // that selection changed.
		resultsGrid.addGridRowListener(new GridRowListenerAdapter(){
			@SuppressWarnings("deprecation")
			public void onRowClick(GridPanel grid, int rowIndex, EventObject e) {
				notifySelectionListeners(new SelectionEvent(XdBrowsePortlet.this));
			}
		});
		
		// Initial list of ODPs
		populateOdpList("Any");
	}
	
	private void populateOdpList(String category) {
		
		XdServiceManager.getInstance().getOdpsByCategory(category, new AsyncCallback<List<OdpDetails>>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("Error", "GWT-RPC call failed: " + caught.getMessage());				
			}

			@Override
			public void onSuccess(List<OdpDetails> results) {
				// Populate results list by creating a new temporary data store using the existing 
				// reader configuration, then moving records from that temporary store to the one
				// that actually backs the GridPanel. Seems inefficient but was easiest to implement
				// in code and there's not that much volume of data that it'll become a problem anyhow.
				List<Object[]> tempList = new ArrayList<Object[]>();
				for (OdpDetails result: results) {
					tempList.add(new Object[]{result.getName(),result.getUri()});
				}
				Object[][] newData = new Object[tempList.size()][]; 
				newData = tempList.toArray(newData);
				MemoryProxy tempProxy = new MemoryProxy(newData); 
				Store tempStore = new Store(tempProxy, resultsReader);
				tempStore.load();
				resultsStore.removeAll();
				resultsStore.add(tempStore.getRecords());
				resultsStore.sort("title", SortDir.ASC);
				resultsStore.commitChanges();
			}
		});
		// TODO Auto-generated method stub
		
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
