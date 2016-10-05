package edu.stanford.bmir.protege.web.client.xd;

import java.util.Collection;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;
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
	
	// ODP description labels/widgets
	private Image odpImage;
	private Label odpImageNotPresentLabel;
	private Label odpNameLabel;
	private Label odpIntentLabel;
	private Label odpDescriptionLabel;
	private Label odpConsequencesLabel;
	private HTML odpCqsList;
	private HTML odpScenariosList;
	private HTML odpDomainsList;
	private Anchor odpIriLink;
	
	// ODP remote visualisation frame
	private Frame visualisationFrame;
	private static String WEBVOWL_URI = "http://vis.xd-protege.com/#iri=";
	

	
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
		
		if (odp.getImageIri().isPresent()) {
			odpImageNotPresentLabel.setVisible(false);
			odpImage.setUrl(odp.getImageIri().get());
			odpImage.setVisible(true);
		}
		else {
			odpImage.setVisible(false);
			odpImageNotPresentLabel.setVisible(true);
		}
		
		odpNameLabel.setText(odp.getName());
		
		if (odp.getIntent().isPresent()) {
			odpIntentLabel.setText(odp.getIntent().get());
		}
		else {
			odpIntentLabel.setText("");
		}
		
		if (odp.getDescription().isPresent()) {
			odpDescriptionLabel.setText(odp.getDescription().get());
		}
		else {
			odpDescriptionLabel.setText("");
		}
		
		if (odp.getConsequences().isPresent()) {
			odpConsequencesLabel.setText(odp.getConsequences().get());
		}
		else {
			odpConsequencesLabel.setText("");
		}
		
		if (odp.getCqs().size() < 1) {
			odpCqsList.setVisible(false);
		}
		else {
			odpCqsList.setVisible(true);
			String cqs = "";
			for (String cq: odp.getCqs()) {
				cqs += "<li>" + cq + "</li>\n";
			}
			odpCqsList.setHTML("<ul>\n" + cqs + "</ul>");
		}
		
		if (odp.getScenarios().size() < 1) {
			odpScenariosList.setVisible(false);
		}
		else {
			odpScenariosList.setVisible(true);
			String scenarios = "";
			for (String scenario: odp.getScenarios()) {
				scenarios += "<li>" + scenario + "</li>\n";
			}
			odpScenariosList.setHTML("<ul>\n" + scenarios + "</ul>");
		}
		
		if (odp.getDomains().size() < 1) {
			odpDomainsList.setVisible(false);
		}
		else {
			odpDomainsList.setVisible(true);
			String domains = "";
			for (String domain: odp.getDomains()) {
				domains += "<li>" + domain + "</li>\n";
			}
			odpDomainsList.setHTML("<ul>\n" + domains + "</ul>");
		}
		
		odpIriLink.setHref(odp.getIri());
		odpIriLink.setText(odp.getIri());
		
		// Enable visualisation
		visualisationFrame.setUrl(WEBVOWL_URI + odp.getIri());
		
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
		Label graphicalRepresentationLabel = new Label("Graphical representation");
		graphicalRepresentationLabel.addStyleName("xdpDetailsPortletHeaderLabel");
		mainPanel.add(graphicalRepresentationLabel);
		
		odpImage = new Image();
		odpImage.addStyleName("odpIllustration");
		mainPanel.add(odpImage);
		odpImageNotPresentLabel = new Label("This CODP has no graphical representation.");
		mainPanel.add(odpImageNotPresentLabel);
		
		Label generalDescriptionLabel = new Label("General description");
		generalDescriptionLabel.addStyleName("xdpDetailsPortletHeaderLabel");
		mainPanel.add(generalDescriptionLabel);
		
		// Instantiate layout grid
		Grid g = new Grid(8, 2);
		g.addStyleName("xdpDetailsPortletTable");
		
		// Row for ODP name
		Label nameHeaderLabel = new Label("Name");
		nameHeaderLabel.addStyleName("xdpDetailsPortletTableHeaderLabel");
		g.setWidget(0, 0, nameHeaderLabel);
		odpNameLabel = new Label();
		g.setWidget(0, 1, odpNameLabel);
		
		// Row for ODP Intent
		Label intentHeaderLabel = new Label("Intent");
		intentHeaderLabel.addStyleName("xdpDetailsPortletTableHeaderLabel");
		g.setWidget(1, 0, intentHeaderLabel);
		odpIntentLabel = new Label();
		g.setWidget(1, 1, odpIntentLabel);
		
		// Row for ODP Description
		Label descriptionHeaderLabel = new Label("Solution description");
		descriptionHeaderLabel.addStyleName("xdpDetailsPortletTableHeaderLabel");
		g.setWidget(2, 0, descriptionHeaderLabel);
		odpDescriptionLabel = new Label();
		g.setWidget(2, 1, odpDescriptionLabel);
		
		// Row for ODP Consequences
		Label consequencesHeaderLabel = new Label("Consequences");
		consequencesHeaderLabel.addStyleName("xdpDetailsPortletTableHeaderLabel");
		g.setWidget(3, 0, consequencesHeaderLabel);
		odpConsequencesLabel = new Label();
		g.setWidget(3, 1, odpConsequencesLabel);
		
		// Row for ODP CQs
		Label cqsHeaderLabel = new Label("Competency Questions");
		cqsHeaderLabel.addStyleName("xdpDetailsPortletTableHeaderLabel");
		g.setWidget(4, 0, cqsHeaderLabel);
		odpCqsList = new HTML();
		g.setWidget(4, 1, odpCqsList);
		
		// Row for ODP usage scenarios
		Label scenariosHeaderLabel = new Label("Scenarios");
		scenariosHeaderLabel.addStyleName("xdpDetailsPortletTableHeaderLabel");
		g.setWidget(5, 0, scenariosHeaderLabel);
		odpScenariosList = new HTML();
		g.setWidget(5, 1, odpScenariosList);
		
		// Row for ODP domains
		Label domainsHeaderLabel = new Label("Domains");
		domainsHeaderLabel.addStyleName("xdpDetailsPortletTableHeaderLabel");
		g.setWidget(6, 0, domainsHeaderLabel);
		odpDomainsList = new HTML();
		g.setWidget(6, 1, odpDomainsList);
		
		// Row for ODP IRI
		Label iriHeaderLabel = new Label("Reusable OWL building block");
		iriHeaderLabel.addStyleName("xdpDetailsPortletTableHeaderLabel");
		g.setWidget(7, 0, iriHeaderLabel);
		odpIriLink = new Anchor();
		g.setWidget(7, 1, odpIriLink);
		
		// Add grid to panel
		g.getCellFormatter().setWidth(0, 0, "120px");
		mainPanel.add(g);
		
		// Hide main panel until some ODP is selected
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
            	wizard.loadOdp(odp.getIri());
            }
        });
        
        useOdpButton.setDisabled(true);
        toolbar.addButton(useOdpButton);
	}
}
