package edu.stanford.bmir.protege.web.client.xd;

import java.util.Collection;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
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
	
	// Portlet UI widget definitions
	private TextBox queryTextBox;
	private Button searchButton;
	private Label resultLabel;

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
		setTitle("ODP Search");
		
		// Initialize UI widgets
		queryTextBox = new TextBox();
		searchButton = new Button("Search");
		resultLabel = new Label("Results go here..");
		
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
		
        // Add UI widgets to portlet canvas
		add(queryTextBox);
		add(searchButton);
		add(resultLabel);
	}
}
