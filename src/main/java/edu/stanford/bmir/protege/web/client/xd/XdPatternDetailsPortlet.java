package edu.stanford.bmir.protege.web.client.xd;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLDataFactory;

import com.google.common.base.Optional;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.VerticalLayout;

import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.dispatch.RenderableGetObjectResult;
import edu.stanford.bmir.protege.web.client.dispatch.actions.GetOntologyAnnotationsAction;
import edu.stanford.bmir.protege.web.client.dispatch.actions.SetOntologyAnnotationsAction;
import edu.stanford.bmir.protege.web.client.dispatch.actions.SetOntologyAnnotationsResult;
import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.rpc.data.EntityData;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;
import edu.stanford.bmir.protege.web.shared.DataFactory;
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
	
	// TODO : Debug code below remove when done
	private Optional<Set<OWLAnnotation>> lastSet = Optional.absent();
	
	// References to specialisation wizard and its popup
	//private XdSpecializationWizard wizard;
	//private PopupPanel wizardPopup;
	private XdSpecializationWizard wizard;
	
	public XdPatternDetailsPortlet(Project project) {
		super(project);
		wizard = new XdSpecializationWizard();
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
					odp = result;
					renderOdpDetails(result);
				}
			});
		}
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
		
		// TODO : below is debug code, remove when done
        DispatchServiceManager.get().execute(new GetOntologyAnnotationsAction(getProjectId()), new AsyncCallback<RenderableGetObjectResult<Set<OWLAnnotation>>>() {
            @Override
            public void onFailure(Throwable caught) {
                MessageBox.alert("There was a problem retrieving the annotation for this project.");
            }

            @Override
            public void onSuccess(RenderableGetObjectResult<Set<OWLAnnotation>> result) {
                final Set<OWLAnnotation> object = result.getObject();
                    lastSet = Optional.of(object);
            }
        });
	}

	
	protected void addToolbarButtons() {
        setTopToolbar(new Toolbar());
        final Toolbar toolbar = getTopToolbar();
        
        useOdpButton = new ToolbarButton("Use this Pattern");
        useOdpButton.setCls("toolbar-button");
        
        useOdpButton.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(final Button button, final EventObject e) {
            	wizard.show();
            	wizard.loadOdp(odp.getUri());
            }
        });
        
        useOdpButton.setDisabled(true);
        toolbar.addButton(useOdpButton);
        
        ToolbarButton testImportsButton = new ToolbarButton("Imports test");
        testImportsButton.addListener(new ButtonListenerAdapter() {
        	@Override
            public void onClick(final Button button, final EventObject e) {
        		OWLDataFactory df = DataFactory.get();
        		OWLAnnotation importsAnnotation = DataFactory.get().getOWLAnnotation(df.getOWLAnnotationProperty(IRI.create("http://www.w3.org/2002/07/owl#imports")),IRI.create("http://www.ontologydesignpatterns.org/cp/owl/agentrole.owl"));
        		Set<OWLAnnotation> oldSet = lastSet.get();
        		Set<OWLAnnotation> newSet = new HashSet<OWLAnnotation>();
        		newSet.addAll(oldSet);
        		newSet.add(importsAnnotation);
        		DispatchServiceManager.get().execute(new SetOntologyAnnotationsAction(getProjectId(), oldSet, newSet), new AsyncCallback<SetOntologyAnnotationsResult>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        MessageBox.alert("There was a problem setting the ontology annotations for this project.");
                        GWT.log("Problem setting ontology annotations", caught);
                    }

                    @Override
                    public void onSuccess(SetOntologyAnnotationsResult result) {
                    	MessageBox.alert("Import annotations were added properly. Now attempting to cache imports..");
                    	XdServiceManager.getInstance().cacheImports(getProjectId(), new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								MessageBox.alert("There was a problem updating the project imports closure cache serverside.");
								// TODO Auto-generated method stub
							}

							@Override
							public void onSuccess(Void result) {
								MessageBox.alert("Imports closure succesfully cached!");
								// TODO Auto-generated method stub
							}
                    	});
                    	
                    }
                });
        	}
        });
        toolbar.addButton(testImportsButton);
	}
}
