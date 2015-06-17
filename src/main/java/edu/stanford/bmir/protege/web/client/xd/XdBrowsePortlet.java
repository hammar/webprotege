package edu.stanford.bmir.protege.web.client.xd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.event.GridRowListenerAdapter;
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
import edu.stanford.bmir.protege.web.shared.xd.OdpDetails;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetOdpsByCategoryAction;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpsByCategoryResult;

/***
 * Portlet providing an ODP search GUI.
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
@SuppressWarnings("unchecked")
public class XdBrowsePortlet extends AbstractOWLEntityPortlet implements Selectable {
	
	public XdBrowsePortlet(SelectionModel selectionModel, Project project) {
		super(selectionModel, project);
		this.listeners = new ArrayList<SelectionListener>();
	}
		
	// Core search filters
	private ComboBox categoryCb;

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
			public void onRowClick(GridPanel grid, int rowIndex, EventObject e) {
				notifySelectionListeners(new SelectionEvent(XdBrowsePortlet.this));
			}
		});
		
		// Initial list of ODPs
		populateOdpList("Any");
	}
	
	private void populateOdpList(String category) {
		
		// Execute remote query method
		DispatchServiceManager.get().execute(new GetOdpsByCategoryAction(category), new DispatchServiceCallback<GetOdpsByCategoryResult>() {
			
			@Override
			public void handleSuccess(GetOdpsByCategoryResult result) {
				// Populate results list by creating a new temporary data store using the existing 
				// reader configuration, then moving records from that temporary store to the one
				// that actually backs the GridPanel. Seems inefficient but was easiest to implement
				// in code and there's not that much volume of data that it'll become a problem anyhow.
				// TODO: Clean this up and actually use the store as intended by its designers.
				List<OdpDetails> odps = result.getOdps();
				List<Object[]> tempList = new ArrayList<Object[]>();
				for (OdpDetails odp: odps) {
					tempList.add(new Object[]{odp.getName(),odp.getUri()});
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
		// Not implemented for this portlet - we don't allow extenral sources
		// to set the selection.
	}
}
