package edu.stanford.bmir.protege.web.client.xd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

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
	
	private ToolbarButton deleteButton;
	private ToolbarButton editButton;
	private TreePanel treePanel;
	
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
		setLayout(new FitLayout());
		setTitle("Instantiated ODPs");
		addToolbarButtons();
		
		// Initialize required member variables
		instantiations = new ArrayList<OdpInstantiation>();
		odps = new ArrayList<OdpDetails>();

		// Tree panel configuration
		treePanel = new TreePanel();
        treePanel.setHeight(560);
        treePanel.setAutoWidth(true);
        treePanel.setAnimate(true);
        treePanel.setAutoScroll(true);
        treePanel.setRootVisible(false);
        // The methods on this listener adapter triggers when users do 
        // something with the tree view (click on it, drag things, etc.)
        treePanel.addListener(new TreePanelListenerAdapter() {
        	// We only want to look for node selections, e.g. clicks.
            @Override
            public void onClick(final TreeNode node, EventObject e) {
                if (node.getChildNodes().length > 0) {
                	// The selected node has children - i.e. is an ODP - i.e. is not editable.
                	editButton.setDisabled(true);
                }
                else {
                	// The selected node has no children - e.g. is an ODP instantiations - e.g., 
                	// is potentially editable (though only if user has edit rights)
                	editButton.setDisabled(!hasWritePermission());
                }
            }
        });
        final TreeNode root = new TreeNode();
	    treePanel.setRootNode(root);
	    add(treePanel);
		
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

				// Populate the tree with child nodes from returned data.
				// Use ODP and ODP Instantiation URIs as node identifiers so
				// that they can be removed easily later if needed.
				for (OdpDetails od: odps) {
					TreeNode odpNode = new TreeNode(od.getName());
					odpNode.setId(od.getUri());
					for (OdpInstantiation oi: instantiations) {
						if (oi.getOdp().getUri() == od.getUri()) {
							TreeNode child = new TreeNode(oi.getName());
							child.setId(oi.getUri());
							odpNode.appendChild(child);
						}
					}
					treePanel.getRootNode().appendChild(odpNode);
				}
				
				// Show all child nodes.
				treePanel.expandAll();
			}
		});
	}
	
    @SuppressWarnings("deprecation")
	protected void addToolbarButtons() {
        setTopToolbar(new Toolbar());
        final Toolbar toolbar = getTopToolbar();

        deleteButton = new ToolbarButton("Delete");
        deleteButton.setCls("toolbar-button");
        deleteButton.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(final Button button, final EventObject e) {
                onDeleteOdpInstantiation();
            }
        });
        deleteButton.setDisabled(!hasWritePermission());
        toolbar.addButton(deleteButton);
        
        editButton = new ToolbarButton("Edit");
        editButton.setCls("toolbar-button");
        editButton.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(final Button button, final EventObject e) {
            	onEditOdpInstantiation();
            }
        });
        editButton.setDisabled(!hasWritePermission());
        toolbar.addElement(editButton.getElement());
    }

	private void onEditOdpInstantiation() {
		// TODO Auto-generated method stub
		// This is where we bring up the specialization or edit wizard again, to edit the instantiation.
		Window.alert("ODP instantiation editing function not yet implemented.");
	}

	private void onDeleteOdpInstantiation() {
		// TODO Auto-generated method stub
		// This is where we bring up some UI to ask for confirmation of deletion of ODP instantiation.
		// Then delete it.
		// Note that deleting an ODP that has multiple instantiations deletes ALL child instantiations.
		Window.alert("ODP instantiation deletion function not yet implemented.");
	}
}
