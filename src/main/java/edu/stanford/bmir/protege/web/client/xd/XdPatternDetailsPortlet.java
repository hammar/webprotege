package edu.stanford.bmir.protege.web.client.xd;

import java.util.Collection;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

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
	
	private FlowPanel odpDetailsPanel;
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
		odpTitleLabel.setVisible(true);
		
		odpDescriptionLabel.setText(odp.getDescription());
		odpDomainsLabel.setText(odp.getDomains());
		odpCqsLabel.setText(odp.getCqs());
		odpUriLabel.setText(odp.getUri());
		odpDetailsGrid.setVisible(true);
		
		odpIllustration.setUrl(odp.getImage());
		odpIllustration.setVisible(true);
	}

	// Initialization method for GUI
	@Override
	public void initialize() {
		this.setTitle("ODP Details");
		
		odpDetailsPanel = new FlowPanel();
		
		// Initialize widgets
		odpTitleLabel = new Label();
		odpDescriptionLabel = new Label();
		odpDomainsLabel = new Label();
		odpCqsLabel = new Label();
		odpUriLabel = new Label();
		
		// Configure widgets
		odpTitleLabel.addStyleName("xdOdpDescriptionTitle");
		odpDetailsPanel.add(odpTitleLabel);
		odpTitleLabel.setVisible(false);
		
		odpIllustration = new Image();
		odpIllustration.setWidth("60em");
		odpIllustration.addStyleName("xdOdpDetailsIllustration");
		odpDetailsPanel.add(odpIllustration);
		odpIllustration.setVisible(false);
		
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
		odpDetailsPanel.add(odpDetailsGrid);
		odpDetailsGrid.setVisible(false);

		this.add(odpDetailsPanel);
	}
}
