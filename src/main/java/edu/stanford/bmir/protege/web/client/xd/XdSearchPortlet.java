package edu.stanford.bmir.protege.web.client.xd;

import java.util.Collection;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

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
	private HorizontalPanel searchPanel;
	private TextBox queryTextBox;
	private Button searchButton;
	private Button searchOptionsButton;
	private Label resultLabel;
	private Grid searchOptions;

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
		searchButton = new Button("S");
		searchOptionsButton = new Button("O");
		searchOptions = new Grid(5,2);
		resultLabel = new Label("Results will go here\nand here\nand here..");
		
		// Search header
		header = new DockLayoutPanel(Unit.EM);
		searchPanel = new HorizontalPanel();
		searchPanel.add(queryTextBox);
		searchPanel.add(searchButton);
		searchPanel.add(searchOptionsButton);
		header.addNorth(searchPanel, 2);
		header.addSouth(searchOptions, 15);
		header.setWidgetHidden(searchOptions, true);
		
		// Main portlet layout
		portletLayout = new DockLayoutPanel(Unit.EM);
		portletLayout.addNorth(header, 2);
		portletLayout.add(resultLabel);
		add(portletLayout);
		
		// Search button behavior
        searchButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
				XdServiceManager.getInstance().getOdpSearchContent(queryTextBox.getText(), new AsyncCallback<List<String>>() {
					@Override
					public void onFailure(Throwable caught) {
						resultLabel.setText("GWT-RPC call failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(List<String> results) {
						String resultString = "Returned search results: \n";
						for (String odp: results) {
							resultString += odp;
							resultString += "\n";
						}
						resultLabel.setText(resultString);
					}
					
				});
            	resultLabel.setText("The button was clicked!");
            }
          });
        
        // Options button behavior
        searchOptionsButton.addClickHandler(new ClickHandler(){
        	public void onClick(ClickEvent event) {
        		if (!isSearchOptionsExpanded) {
        			portletLayout.setWidgetSize(header, 17);
        			header.setWidgetHidden(searchOptions, false);
        			isSearchOptionsExpanded = true;
        		}
        		else {
        			portletLayout.setWidgetSize(header, 2);
        			header.setWidgetHidden(searchOptions, true);
        			isSearchOptionsExpanded = false;
        		}
        	}
        });
	}
}
