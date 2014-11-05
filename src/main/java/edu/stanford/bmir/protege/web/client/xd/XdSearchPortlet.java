package edu.stanford.bmir.protege.web.client.xd;

import java.util.Collection;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.rpc.data.EntityData;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;

/***
 * Portlet providing an ODP search GUI.
 * @author Karl Hammar
 *
 */
@SuppressWarnings("unchecked")
public class XdSearchPortlet extends AbstractOWLEntityPortlet {
	
	public XdSearchPortlet(Project project) {
		super(project);
	}
	
	// Private parameters
	private boolean isSearchOptionsExpanded;
	
	// Portlet UI widget definitions
	private DockLayoutPanel portletLayout;
	private DockLayoutPanel header;
	private TextBox queryTextBox;
	private Button searchButton;
	private Button searchOptionsButton;
	//private Label resultLabel;
	
	// Search options widgets
	private Grid searchOptions;
	private ListBox categoryListBox;
	private ListBox sizeListBox;
	private ListBox profileListBox;
	private ListBox strategyListBox;
	private CheckBox dolceMappingCB;
	private CheckBox schemaOrgMappingCB;
	private CheckBox dbpediaMappingCB;
	
	// Results widget
	private ListBox resultsList;
	//private FlexTable resultsTable;

	@Override
	public Collection<EntityData> getSelection() {
		return null;
	}

	@Override
	public void reload() {
		setTitle("ODP Search Reloaded!");
	}

	// Initialization method for GUI
	@Override
	public void initialize() {
		isSearchOptionsExpanded = false;
		
		setTitle("ODP Search");
		
		// Initialize UI widgets
		queryTextBox = new TextBox();
		queryTextBox.setWidth("18em");
		searchButton = new Button("S");
		searchOptionsButton = new Button("\u25BC");
		searchOptions = new Grid(5,2);
		resultsList = new ListBox();
		resultsList.setVisibleItemCount(999);
		resultsList.setWidth("100%");
		
		// Search options configuration (labels)
		Label categoryLabel = new Label("Category");
		Label sizeLabel = new Label("Size");
		Label profileLabel = new Label("Profile");
		Label strategyLabel = new Label("Strategy");
		Label mappingLabel = new Label("Mapping");
		searchOptions.setWidget(0, 0, categoryLabel);
		searchOptions.setWidget(1, 0, sizeLabel);
		searchOptions.setWidget(2, 0, profileLabel);
		searchOptions.setWidget(3, 0, strategyLabel);
		searchOptions.setWidget(4, 0, mappingLabel);
		
		// Search options configuration (data)
		categoryListBox = new ListBox();
		categoryListBox.addItem("Any");
		categoryListBox.addItem("Academy", "http://ontologydesignpatterns.org/wiki/Community:Academy");
		categoryListBox.addItem("Agriculture", "http://ontologydesignpatterns.org/wiki/Community:Agriculture");
		categoryListBox.addItem("Biology", "http://ontologydesignpatterns.org/wiki/Community:Biology");
		categoryListBox.addItem("Business", "http://ontologydesignpatterns.org/wiki/Community:Business");
		searchOptions.setWidget(0, 1, categoryListBox);
		sizeListBox = new ListBox();
		sizeListBox.addItem("Any");
		sizeListBox.addItem("Small");
		sizeListBox.addItem("Medium");
		sizeListBox.addItem("Large");
		searchOptions.setWidget(1, 1, sizeListBox);
		profileListBox = new ListBox();
		profileListBox.addItem("Any");
		profileListBox.addItem("OWL Horst");
		profileListBox.addItem("OWL2 EL");
		profileListBox.addItem("OWL2 RL");
		profileListBox.addItem("OWL2 QL");
		profileListBox.addItem("OWL2 DL");
		searchOptions.setWidget(2, 1, profileListBox);
		strategyListBox = new ListBox();
		strategyListBox.addItem("Any");
		strategyListBox.addItem("Class-oriented");
		strategyListBox.addItem("Property-oriented");
		strategyListBox.addItem("Hybrid");
		searchOptions.setWidget(3, 1, strategyListBox);
		dolceMappingCB = new CheckBox("DOLCE");
		schemaOrgMappingCB = new CheckBox("Schema.org");
		dbpediaMappingCB = new CheckBox("DBpedia");
		VerticalPanel mappingSelectionPanel = new VerticalPanel();
		mappingSelectionPanel.add(dolceMappingCB);
		mappingSelectionPanel.add(schemaOrgMappingCB);
		mappingSelectionPanel.add(dbpediaMappingCB);
		searchOptions.setWidget(4, 1, mappingSelectionPanel);
		
		// Search header
		header = new DockLayoutPanel(Unit.EM);
		header.setStylePrimaryName("xdSearchHeader");
		HorizontalPanel searchPanel = new HorizontalPanel();
		searchPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		searchPanel.add(queryTextBox);
		searchPanel.add(searchButton);
		searchPanel.add(searchOptionsButton);
		header.addNorth(searchPanel, 3);
		header.addSouth(searchOptions, 13);
		header.setWidgetHidden(searchOptions, true);
		
		// Main portlet layout
		portletLayout = new DockLayoutPanel(Unit.EM);
		portletLayout.addNorth(header, 2);
		portletLayout.add(resultsList);
		add(portletLayout);
		
		// Search button behavior
        searchButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	runOdpSearch();
            }
          });
        
        // Enter-press in query field behavior
        queryTextBox.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					runOdpSearch();
				}
			}
        });
        
        // Options button behavior
        searchOptionsButton.addClickHandler(new ClickHandler(){
        	public void onClick(ClickEvent event) {
        		if (!isSearchOptionsExpanded) {
        			portletLayout.setWidgetSize(header, 15);
        			header.setWidgetHidden(searchOptions, false);
        			isSearchOptionsExpanded = true;
        			searchOptionsButton.setText("\u25B2");
        		}
        		else {
        			portletLayout.setWidgetSize(header, 2);
        			header.setWidgetHidden(searchOptions, true);
        			isSearchOptionsExpanded = false;
        			searchOptionsButton.setText("\u25BC");
        		}
        	}
        });
	}
	
	private void runOdpSearch() {
		resultsList.clear();
    	
		XdServiceManager.getInstance().getOdpSearchContent(queryTextBox.getText(), new AsyncCallback<List<String>>() {
			@Override
			public void onFailure(Throwable caught) {
				resultsList.addItem("GWT-RPC call failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<String> results) {
				for (String odp: results) {
					resultsList.addItem(odp);
				}
			}
		});
	}
}
