package edu.stanford.bmir.protege.web.client.xd;

import java.util.Collection;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.VerticalLayout;

import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.rpc.data.EntityData;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;
import edu.stanford.bmir.protege.web.shared.xd.OdpDetails;

/***
 * Portlet that displays details about a particular ODP, and allows user to fire up
 * ODP specialization wizard for that ODP.
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
@SuppressWarnings("unchecked")
public class XdPatternDetailsPortlet extends AbstractOWLEntityPortlet {
	
	// Core stuff
	private ToolbarButton useOdpButton;
	private Panel mainPanel;
	
	// ODP description widgets
	private Image odpIllustration;
	private Label odpTitleLabel;
	private Label odpDescriptionLabel;
	private Label odpDomainsLabel;
	private Label odpCqsLabel;
	private Label odpUriLabel;
	private Grid odpDetailsGrid;
	
	public XdPatternDetailsPortlet(Project project) {
		super(project);
	}

	@Override
	public Collection<EntityData> getSelection() {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void reload() {
		if (getEntity() != null) {
			String odpUri = getEntity().getName();
			XdServiceManager.getInstance().getOdpDetails(odpUri, new AsyncCallback<OdpDetails>() {
				@Override
				public void onFailure(Throwable caught) {
					add(new Label("Could not retrieve ODP details from server. Error message: " + caught.getMessage())); 				
				}
	
				@Override
				public void onSuccess(OdpDetails result) {
					renderOdpDetails(result);
				}
			});
		}
	}
	
	public void renderOdpDetails(OdpDetails odp) {
		odpTitleLabel.setText(odp.getName());
		
		odpDescriptionLabel.setText(odp.getDescription());
		odpDomainsLabel.setText(odp.getDomains());
		odpCqsLabel.setText(odp.getCqs());
		odpUriLabel.setText(odp.getUri());
		
		odpIllustration.setUrl(odp.getImage());
		
		useOdpButton.enable();
		mainPanel.show();
	}

	// Initialization method for GUI
	@Override
	public void initialize() {
		this.setTitle("ODP Details");
		addToolbarButtons();
		
		// Set up main panel
		mainPanel = new Panel();  
		mainPanel.setPaddings(10);
		mainPanel.setLayout(new VerticalLayout(15)); 
		
		// Initialize widgets
		odpTitleLabel = new Label();
		odpDescriptionLabel = new Label();
		odpDomainsLabel = new Label();
		odpCqsLabel = new Label();
		odpUriLabel = new Label();
		
		// Configure widgets
		odpTitleLabel.addStyleName("xdOdpDescriptionTitle");
		
		odpIllustration = new Image();
		odpIllustration.setWidth("40em");
		odpIllustration.addStyleName("xdOdpDetailsIllustration");
		
		odpDetailsGrid = new Grid(4,2);
		odpDetailsGrid.setWidget(0, 0, new Label("Description:"));
		odpDetailsGrid.setWidget(1, 0, new Label("Domains:"));
		odpDetailsGrid.setWidget(2, 0, new Label("Competency questions:"));
		odpDetailsGrid.setWidget(3, 0, new Label("URL:"));
		for (int i = 0; i<4; i++) {
			odpDetailsGrid.getWidget(i, 0).addStyleName("xdOdpDetailsHeader");
		}
		odpDetailsGrid.setWidget(0, 1, odpDescriptionLabel);
		odpDetailsGrid.setWidget(1, 1, odpDomainsLabel);
		odpDetailsGrid.setWidget(2, 1, odpCqsLabel);
		odpDetailsGrid.setWidget(3, 1, odpUriLabel);
		
		// Add widgets to main panel
		mainPanel.add(odpTitleLabel);
		mainPanel.add(odpIllustration);
		mainPanel.add(odpDetailsGrid);
		mainPanel.setVisible(false);
		
		add(mainPanel);
	}
	
	protected void addToolbarButtons() {
        setTopToolbar(new Toolbar());
        final Toolbar toolbar = getTopToolbar();
        
        useOdpButton = new ToolbarButton("Use this Pattern");
        useOdpButton.setCls("toolbar-button");
        useOdpButton.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(final Button button, final EventObject e) {
            	Window.alert("ODP Specialisation Wizard is not yet developed.");
            }
        });
        useOdpButton.setDisabled(true);
        toolbar.addButton(useOdpButton);
	}
}
