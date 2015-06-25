package edu.stanford.bmir.protege.web.client.xd;

import java.util.Collection;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.PaddedPanel;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.WidgetComponent;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.TableLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;

import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;
import edu.stanford.bmir.protege.web.client.xd.selection.SelectionEvent;
import edu.stanford.bmir.protege.web.client.xd.selection.SelectionListener;
import edu.stanford.bmir.protege.web.client.xd.specialization.DesignPatternSpecializationWizard;
import edu.stanford.bmir.protege.web.shared.selection.SelectionModel;
import edu.stanford.bmir.protege.web.shared.xd.OdpDetails;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetOdpDetailsAction;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpDetailsResult;

/***
 * Portlet that displays details about a particular ODP, and allows user to fire up
 * ODP specialization wizard for that ODP.
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
@SuppressWarnings("unchecked")
public class DesignPatternDetailsPortlet extends AbstractOWLEntityPortlet implements SelectionListener {
	
	// Core stuff
	private ToolbarButton useOdpButton;
	private Panel mainPanel;
	private OdpDetails odp;
	
	// ODP description widgets
	//private Panel odpIllustrationPanel;
	private Image odpIllustration;
	private Label odpTitleLabel;
	private HTML odpDescription;
	
	private Label odpDomainsLabel;
	private HTML odpDomainsList;
	
	private Label odpCqsLabel;
	private HTML odpCqsList;
	
	private Label odpScenariosLabel;
	private HTML odpScenariosList;
	
	private Label odpIriLabel;
	private Anchor odpIriLink;
	
	// References to specialisation wizard and its popup
	private DesignPatternSpecializationWizard wizard;
	
	public DesignPatternDetailsPortlet(SelectionModel selectionModel, Project project) {
		super(selectionModel, project);
		wizard = new DesignPatternSpecializationWizard(this);
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
	
	public void renderOdpDetails(OdpDetails odp) {
		
		if (odp.getName()!=null) {
			odpTitleLabel.setVisible(true);
			odpTitleLabel.setText(odp.getName());
		}
		else {
			odpTitleLabel.setVisible(false);
		}
		
		if (odp.getDescription() != null) {
			odpDescription.setHTML(new SafeHtmlBuilder().appendEscapedLines(odp.getDescription()).toSafeHtml());
		}
		else {
			odpDescription.setText("");
		}
		
		if (odp.getImage() != null) {
			odpIllustration.setUrl(odp.getImage());
			odpIllustration.setVisible(true);
		}
		else {
			odpIllustration.setVisible(false);
		}
		
		if (odp.getDomains() == null) {
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
		
		if (odp.getCqs() == null) {
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
		
		if (odp.getScenarios() == null) {
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
		
		odpIriLink.setHref(odp.getUri());
		odpIriLink.setText(odp.getUri());
		
		useOdpButton.enable();
		mainPanel.show();
	}

	// Initialization method for GUI
	@Override
	public void initialize() {
		this.setTitle("ODP Details");
		this.addStyleName("odpDetailsPortlet");
		addToolbarButtons();
		
		// Set up main panel
		mainPanel = new Panel();  
		mainPanel.setPaddings(10);
		mainPanel.setLayout(new VerticalLayout(15)); 
		mainPanel.setAutoScroll(true);
		
		// Initialize widgets
		odpTitleLabel = new Label();
		odpTitleLabel.addStyleName("odpTitleLabel");
		mainPanel.add(odpTitleLabel);
		
		odpIllustration = new Image();
		odpIllustration.setWidth("500px");
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
		odpDetailsPanel.add(new PaddedPanel(new Panel(odpDomainsLabel.getElement()),5));
		odpDetailsPanel.add(odpDomainsList);
		
		odpCqsLabel = new Label("Competency Questions:");
		odpCqsLabel.addStyleName("odpDetailLabel");
		odpCqsList = new HTML();
		odpCqsList.addStyleName("odpDetail");
		//odpDetailsPanel.add(odpCqsLabel);
		odpDetailsPanel.add(new PaddedPanel(new Panel(odpCqsLabel.getElement()),5,0,0,5));
		odpDetailsPanel.add(new PaddedPanel(new Panel(odpCqsList.getElement()),5,0,0,5));
		//odpDetailsPanel.add(odpCqsList);
		
		odpScenariosLabel = new Label("Scenarios:");
		odpScenariosLabel.addStyleName("odpDetailLabel");
		odpScenariosList = new HTML();
		odpScenariosList.addStyleName("odpDetail");
		odpDetailsPanel.add(odpScenariosLabel);
		odpDetailsPanel.add(odpScenariosList);
		
		odpIriLabel = new Label("IRI:");
		odpIriLabel.addStyleName("odpDetailLabel");
		odpIriLink = new Anchor();
		odpIriLink.addStyleName("odpDetail");
		odpDetailsPanel.add(odpIriLabel);
		odpDetailsPanel.add(odpIriLink);
		
		mainPanel.add(odpDetailsPanel);
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
            	wizard.setModal(true);
            	wizard.show();
            	wizard.loadOdp(odp.getUri());
            }
        });
        
        useOdpButton.setDisabled(true);
        toolbar.addButton(useOdpButton);
	}
}
