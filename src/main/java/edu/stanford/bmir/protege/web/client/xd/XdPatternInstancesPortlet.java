package edu.stanford.bmir.protege.web.client.xd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.rpc.data.EntityData;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;
import edu.stanford.bmir.protege.web.shared.xd.OdpDetails;
import edu.stanford.bmir.protege.web.shared.xd.OdpInstantiation;

/***
 * Portlet listing existing ODP instantiations in ontology and allows users to
 * modify or delete those instantiations.
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
@SuppressWarnings("unchecked")
public class XdPatternInstancesPortlet extends AbstractOWLEntityPortlet {
	
	private List<OdpInstantiation> instantiations;
	private List<OdpDetails> odps;
	
	public XdPatternInstancesPortlet(Project project) {
		super(project);
	}

	@Override
	public Collection<EntityData> getSelection() {
		return null;
	}

	@Override
	public void reload() {
		
	}

	// Initialization method for GUI
	@SuppressWarnings("deprecation")
	@Override
	public void initialize() {
		setTitle("Instantiated ODPs");
		
		// Initialize required member variables
		instantiations = new ArrayList<OdpInstantiation>();
		odps = new ArrayList<OdpDetails>();

		// Call XD Service API to populate list of instantiated ODPs
		XdServiceManager.getInstance().getInstantiatedOdps(getProjectId(), new AsyncCallback<List<OdpInstantiation>>(){

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert("ODP instantiations could not be retreived from server. Error message: " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<OdpInstantiation> results) {
				// Populate the member variables form returned data
				instantiations.addAll(results);
				for (OdpInstantiation oi: results) {
					if (!odps.contains(oi.getOdp())) {
						odps.add(oi.getOdp());
					}
				}

				// Create a tree and populate it from the member variables
				// Not very efficient to iterate over all OdpInstantiations again below,
				// but we are not dealing with large amounts of data anyway..
				Tree t = new Tree();
				for (OdpDetails od: odps) {
					Label odpLabel = new Label(od.getName());
					odpLabel.setTitle(od.getUri());
					TreeItem odpTI = new TreeItem(odpLabel);
					// For each ODP used in project, iterate over instantiations and add
					// child nodes for each.
					for (OdpInstantiation oi: instantiations) {
						if (oi.getOdp().getUri() == od.getUri()) {
							odpTI.addItem(new OdpInstantiationWidget(oi.getName(),oi.getUri()));
						}
					}
					odpTI.setState(true);
					t.addItem(odpTI);
				}
				add(t);
			}
		});
	}
}
