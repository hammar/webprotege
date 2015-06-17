package edu.stanford.bmir.protege.web.client.xd;

import java.util.Collection;

import com.google.common.base.Optional;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.VerticalLayout;

import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.rpc.data.EntityData;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;
import edu.stanford.bmir.protege.web.client.ui.selection.SelectionEvent;
import edu.stanford.bmir.protege.web.client.ui.selection.SelectionListener;
import edu.stanford.bmir.protege.web.client.xd.specialization.XdSpecializationWizard;
import edu.stanford.bmir.protege.web.shared.entity.OWLEntityData;
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
public class XdPatternDetailsPortlet extends AbstractOWLEntityPortlet implements SelectionListener {
	
	// Core stuff
	private ToolbarButton useOdpButton;
	private Panel mainPanel;
	private OdpDetails odp;
	
	// ODP description widgets
	private Image odpIllustration;
	private Label odpTitleLabel;
	private HTML odpDescription;
	private HTML odpDomainsList;
	private HTML odpCqsList;
	private Anchor odpUriLink;
	private HTML odpScenariosList;
	private Grid odpDetailsGrid;
	
	// References to specialisation wizard and its popup
	//private XdSpecializationWizard wizard;
	//private PopupPanel wizardPopup;
	private XdSpecializationWizard wizard;
	
	public XdPatternDetailsPortlet(SelectionModel selectionModel, Project project) {
		super(selectionModel, project);
		wizard = new XdSpecializationWizard(this);
	}

	
	/* ---- SelectionListener implementation method ---- 
	 * Called when notified by some Selectable object (e.g., the search or browsing portlets) that 
	 * their selections have been updated and that listeners should refresh content. */
	@Override
	public void selectionChanged(SelectionEvent event) {
		Collection<EntityData> selection = event.getSelectable().getSelection();
		if (selection.size() > 0) {
			EntityData entityData = selection.iterator().next();
			String odpUri = entityData.getName();
			
			DispatchServiceManager.get().execute(new GetOdpDetailsAction(odpUri), new DispatchServiceCallback<GetOdpDetailsResult>() {
				@Override
				public void handleSuccess(GetOdpDetailsResult result) {
					odp = result.getDetails();
					renderOdpDetails(odp);
				}
			});
		}
	}
	
	protected void handleAfterSetEntity(Optional<OWLEntityData> entityData) {

	}
	
	public void renderOdpDetails(OdpDetails odp) {
		
		// TODO: clean up this part: which fields are actually mandatory that we get back from backend?
		
		if (odp.getName()!=null) {
			odpTitleLabel.setText(odp.getName());
		}
		else {
			odpTitleLabel.setText("Name undefined");
		}
		
		if (odp.getDescription() != null) {
			odpDescription.setHTML(new SafeHtmlBuilder().appendEscapedLines(odp.getDescription()).toSafeHtml());
		}
		else {
			odpDescription.setText("");
		}
		
		String domains = "";
		if (odp.getDomains()!=null) {
			for (String domain: odp.getDomains()) {
				domains += "<li>" + domain + "</li>\n";
			}
		}
		odpDomainsList.setHTML("<ul>" + domains + "</ul>");
		
		String cqs = "";
		if (odp.getCqs()!=null){
			for (String cq: odp.getCqs()) {
				cqs += "<li>" + cq + "</li>\n";
			}
		}
		odpCqsList.setHTML(cqs);
		
		odpUriLink.setHref(odp.getUri());
		odpUriLink.setText(odp.getUri());
		
		if (odp.getImage() != null) {
			odpIllustration.setUrl(odp.getImage());
		}
		else {
			odpIllustration.setUrl("");
		}
		
		String scenarios = "";
		if (odp.getScenarios() != null) {
			for (String scenario: odp.getScenarios()) {
				scenarios += "<li>" + scenario + "</li>\n";
			}
		}
		odpScenariosList.setHTML("<ul>" + scenarios + "</ul>");
		
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
		mainPanel.setAutoScroll(true);
		
		// Initialize widgets
		odpTitleLabel = new Label();
		odpDescription = new HTML();
		odpDomainsList = new HTML();
		odpCqsList = new HTML();
		odpUriLink = new Anchor();
		odpScenariosList = new HTML();
		
		// Configure widgets
		odpTitleLabel.addStyleName("xdOdpDescriptionTitle");
		
		odpIllustration = new Image();
		odpIllustration.setWidth("40em");
		odpIllustration.addStyleName("xdOdpDetailsIllustration");
		
		odpDetailsGrid = new Grid(5,2);
		odpDetailsGrid.setWidget(0, 0, new Label("Description:"));
		odpDetailsGrid.setWidget(1, 0, new Label("Domains:"));
		odpDetailsGrid.setWidget(2, 0, new Label("Competency questions:"));
		odpDetailsGrid.setWidget(3, 0, new Label("Scenarios:"));
		odpDetailsGrid.setWidget(4, 0, new Label("URL:"));
		for (int i = 0; i<5; i++) {
			odpDetailsGrid.getWidget(i, 0).addStyleName("xdOdpDetailsHeader");
		}
		odpDetailsGrid.setWidget(0, 1, odpDescription);
		odpDetailsGrid.setWidget(1, 1, odpDomainsList);
		odpDetailsGrid.setWidget(2, 1, odpCqsList);
		odpDetailsGrid.setWidget(3, 1, odpScenariosList);
		odpDetailsGrid.setWidget(4, 1, odpUriLink);
		
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
            	wizard.setModal(true);
            	wizard.show();
            	wizard.loadOdp(odp.getUri());
            }
        });
        
        useOdpButton.setDisabled(true);
        toolbar.addButton(useOdpButton);
	}
}
