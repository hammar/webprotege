package edu.stanford.bmir.protege.web.client.xd;

import java.util.Collection;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.PaddedPanel;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.TableLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;
import com.karlhammar.xdpservices.data.CodpDetails;

import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;
import edu.stanford.bmir.protege.web.client.xd.instantiation.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.client.xd.selection.SelectionEvent;
import edu.stanford.bmir.protege.web.client.xd.selection.SelectionListener;
import edu.stanford.bmir.protege.web.shared.selection.SelectionModel;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetOdpDetailsAction;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpDetailsResult;

/***
 * Portlet that displays details about a particular ODP, and allows user to fire up
 * ODP specialization wizard for that ODP.
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
public class DesignPatternDetailsPortlet extends AbstractOWLEntityPortlet implements SelectionListener {
	
	// Core stuff
	private ToolbarButton useOdpButton;
	private Panel mainPanel;
	private CodpDetails odp;
	
	// ODP description widgets
	private Image odpIllustration;
	private Label odpTitleLabel;
	private HTML odpDescription;
	
	// ODP remote visualisation frame
	private Frame visualisationFrame;
	private static String WEBVOWL_URI = "http://vis.xd-protege.com/#iri=";
	
	private Label odpDomainsLabel;
	private HTML odpDomainsList;
	
	private Label odpCqsLabel;
	private HTML odpCqsList;
	
	private Label odpScenariosLabel;
	private HTML odpScenariosList;
	
	private Label odpIriLabel;
	private Anchor odpIriLink;
	
	// References to specialisation wizard and its popup
	private DesignPatternInstantiationWizard wizard;
	
	public DesignPatternDetailsPortlet(SelectionModel selectionModel, Project project) {
		super(selectionModel, project);
		wizard = new DesignPatternInstantiationWizard(this);
	}
	
	/* ---- SelectionListener implementation method ---- 
	 * Called when notified by some Selectable object (e.g., the search or browsing portlets) that 
	 * their selections have been updated and that listeners should refresh content. */
	@Override
	public void selectionChanged(SelectionEvent event) {
		Collection<? extends Object> selection = event.getSelectable().getSelection();
		if (selection.size() > 0) {
			Object selectionData = selection.iterator().next();
			if (selectionData instanceof String) {
				String odpUri = (String)selectionData;
				
				DispatchServiceManager.get().execute(new GetOdpDetailsAction(odpUri), new DispatchServiceCallback<GetOdpDetailsResult>() {
					@Override
					public void handleSuccess(GetOdpDetailsResult result) {
						odp = result.getDetails();
						renderOdpDetails(odp);
					}
				});
			}
		}
	}
	
	public void renderOdpDetails(CodpDetails odp) {
		
		odpTitleLabel.setVisible(true);
		odpTitleLabel.setText(odp.getName());
		
		if (odp.getDescription().isPresent()) {
			odpDescription.setHTML(new SafeHtmlBuilder().appendEscapedLines(odp.getDescription().get()).toSafeHtml());
		}
		else {
			odpDescription.setText("");
		}
		
		if (odp.getImageIri().isPresent()) {
			odpIllustration.setUrl(odp.getImageIri().get());
			odpIllustration.setVisible(true);
		}
		else {
			odpIllustration.setVisible(false);
		}
		
		if (odp.getDomains().isEmpty()) {
			odpDomainsLabel.setVisible(false);
			odpDomainsList.setVisible(false);
		}
		else {
			odpDomainsLabel.setVisible(true);
			odpDomainsList.setVisible(true);
			String domains = "";
			for (String domain: odp.getDomains()) {
				domains += "<li>" + domain + "</li>\n";
			}
			odpDomainsList.setHTML("<ul>" + domains + "</ul>");
		}
		
		if (odp.getCqs().size() < 1) {
			odpCqsLabel.setVisible(false);
			odpCqsList.setVisible(false);
		}
		else {
			odpCqsLabel.setVisible(true);
			odpCqsList.setVisible(true);
			String cqs = "";
			for (String cq: odp.getCqs()) {
				cqs += "<li>" + cq + "</li>\n";
			}
			odpCqsList.setHTML("<ul>" + cqs + "</ul>");
		}
		
		if (odp.getScenarios().size() < 1) {
			odpScenariosLabel.setVisible(false);
			odpScenariosList.setVisible(false);
		}
		else {
			odpScenariosLabel.setVisible(true);
			odpScenariosList.setVisible(true);
			String scenarios = "";
			for (String scenario: odp.getScenarios()) {
				scenarios += "<li>" + scenario + "</li>\n";
			}
			odpScenariosList.setHTML("<ul>" + scenarios + "</ul>");
		}
		
		odpIriLink.setHref(odp.getUri().toString());
		odpIriLink.setText(odp.getUri().toString());
		
		// Enable visualisation
		visualisationFrame.setUrl(WEBVOWL_URI + odp.getUri());
		
		useOdpButton.enable();
		mainPanel.show();
	}

	// Initialization method for GUI
	@Override
	public void initialize() {
		this.setTitle("ODP Details");
		this.addStyleName("odpDetailsPortlet");
		addToolbarButtons();
		
		// Set up tab panel
		TabPanel tabPanel = new TabPanel();  
        tabPanel.setTabPosition(Position.TOP); 
        tabPanel.setMinTabWidth(115);  
        tabPanel.setTabWidth(135);  
        tabPanel.setActiveTab(0); 
		
		// Set up main ODP metadata panel
		mainPanel = new Panel("Pattern Description");  
		mainPanel.setPaddings(10);
		mainPanel.setLayout(new VerticalLayout(15)); 
		mainPanel.setAutoScroll(true);
		
		// Initialize widgets
		odpTitleLabel = new Label();
		odpTitleLabel.addStyleName("odpTitleLabel");
		mainPanel.add(odpTitleLabel);
		
		odpIllustration = new Image();
		odpIllustration.addStyleName("odpIllustration");
		mainPanel.add(odpIllustration);
		
		odpDescription = new HTML();
		mainPanel.add(odpDescription);
		
		Label odpDetailsHeading = new Label("Pattern Details");
		odpDetailsHeading.addStyleName("odpDetailsHeading");
		mainPanel.add(odpDetailsHeading);
		
		Panel odpDetailsPanel = new Panel();
		odpDetailsPanel.setLayout(new TableLayout(2));
		odpDetailsPanel.setBorder(true);
		odpDetailsPanel.setBodyBorder(true);
		
		odpDomainsLabel = new Label("Domains:");
		odpDomainsLabel.addStyleName("odpDetailLabel");
		odpDomainsList = new HTML();
		odpDomainsList.addStyleName("odpDetail");
		odpDetailsPanel.add(new PaddedPanel(new Panel(odpDomainsLabel.getElement()),5,0,0,5));
		odpDetailsPanel.add(new PaddedPanel(new Panel(odpDomainsList.getElement()),5,0,0,5));
		
		odpCqsLabel = new Label("Competency Questions:");
		odpCqsLabel.addStyleName("odpDetailLabel");
		odpCqsList = new HTML();
		odpCqsList.addStyleName("odpDetail");
		odpDetailsPanel.add(new PaddedPanel(new Panel(odpCqsLabel.getElement()),5,0,0,5));
		odpDetailsPanel.add(new PaddedPanel(new Panel(odpCqsList.getElement()),5,0,0,5));
		
		odpScenariosLabel = new Label("Scenarios:");
		odpScenariosLabel.addStyleName("odpDetailLabel");
		odpScenariosList = new HTML();
		odpScenariosList.addStyleName("odpDetail");
		odpDetailsPanel.add(new PaddedPanel(new Panel(odpScenariosLabel.getElement()),5,0,0,5));
		odpDetailsPanel.add(new PaddedPanel(new Panel(odpScenariosList.getElement()),5,0,0,5));
		
		odpIriLabel = new Label("IRI:");
		odpIriLabel.addStyleName("odpDetailLabel");
		odpIriLink = new Anchor();
		odpIriLink.addStyleName("odpDetail");
		odpDetailsPanel.add(new PaddedPanel(new Panel(odpIriLabel.getElement()),5,0,0,5));
		odpDetailsPanel.add(new PaddedPanel(new Panel(odpIriLink.getElement()),5,0,0,5));
		
		mainPanel.add(odpDetailsPanel);
		mainPanel.setVisible(false);
		
		visualisationFrame = new Frame();
		Panel visualisationPanel = new Panel("WebVOWL Visualisation");  
		visualisationPanel.setLayout(new FitLayout());  
		visualisationPanel.setIconCls("tab-icon");  
		visualisationPanel.add(visualisationFrame); 
		
		// Add panels to tabpanel and tabpanel to page
		tabPanel.add(mainPanel);
		tabPanel.add(visualisationPanel);
		add(tabPanel);
	}

	
	private void addToolbarButtons() {
        setTopToolbar(new Toolbar());
        final Toolbar toolbar = getTopToolbar();
        
        useOdpButton = new ToolbarButton("Use this Pattern");
        useOdpButton.setCls("toolbar-button");
        
        useOdpButton.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(final Button button, final EventObject e) {
            	wizard.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                    public void setPosition(int offsetWidth, int offsetHeight) {
                      int left = (Window.getClientWidth() - offsetWidth) / 2;
                      int top = (Window.getClientHeight() - offsetHeight) / 2;
                      wizard.setPopupPosition(left, top);
                    }
                  });
            	wizard.loadOdp(odp.getUri().toString());
            }
        });
        
        useOdpButton.setDisabled(true);
        toolbar.addButton(useOdpButton);
	}
}
