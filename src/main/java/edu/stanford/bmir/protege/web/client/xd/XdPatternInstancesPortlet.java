package edu.stanford.bmir.protege.web.client.xd;

import java.util.Collection;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.rpc.data.EntityData;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;

/***
 * Portlet listing existing ODP instantiations in ontology.
 * @author Karl Hammar
 *
 */
@SuppressWarnings("unchecked")
public class XdPatternInstancesPortlet extends AbstractOWLEntityPortlet {
	
	private Label debugLabel;
	private PopupPanel contextMenu;
	
	public XdPatternInstancesPortlet(Project project) {
		super(project);
	}

	@Override
	public Collection<EntityData> getSelection() {
		return null;
	}

	@Override
	public void reload() {
		setTitle("Instantiated ODPs Reloaded!");
	}

	// Initialization method for GUI
	@Override
	public void initialize() {
		setTitle("Instantiated ODPs");
		
	    contextMenu = new PopupPanel(true);
	    contextMenu.add(new HTML("My Context menu!"));
	    contextMenu.hide();
		
	    // Create a tree with a few items in it.
		Label naryLabel = new Label("Nary Participation");
		naryLabel.setTitle("http://www.ontologydesignpatterns.org/cp/owl/naryparticipation.owl");
	    TreeItem nary = new TreeItem(naryLabel);
	    nary.addItem(new OdpInstantiationWidget("Instantiation: Class attendance","http://www.example.com/classAttendanceModule.owl"));
	    nary.addItem(new OdpInstantiationWidget("Instantiation: Club membership","http://www.example.com/clubMembershipModule.owl"));
	    nary.addItem(new OdpInstantiationWidget("Instantiation: Meeting attendance","http://www.example.com/meetingAttendanceModule.owl"));
	    nary.setState(true);
	    
	    Label infoRLabel = new Label("Information Realization");
	    infoRLabel.setTitle("http://www.ontologydesignpatterns.org/cp/owl/informationrealization.owl ");
	    TreeItem infoR = new TreeItem(infoRLabel);
	    infoR.addItem(new OdpInstantiationWidget("Instantiation: Books and Printed Copies","http://www.example.com/printedBooksModule.owl"));
	    infoR.setState(true);
	    
	    Label accLabel = new Label("Accountability");
	    accLabel.setTitle("http://infoeng.se/~karl/ilog2014/odps/accountability.owl");
	    TreeItem acc = new TreeItem(accLabel);
	    acc.addItem(new OdpInstantiationWidget("Instantiation: Patient Consent","http://www.example.com/patientConsentModule.owl"));
	    acc.addItem(new OdpInstantiationWidget("Instantiation: House-keeping","http://www.example.com/houseKeepingModule.owl"));
	    acc.setState(true);
	    
	    Tree t = new Tree();
	    t.addItem(nary);
	    t.addItem(infoR);
	    t.addItem(acc);
	    
	    // Add it to the root panel.
	    add(t);
	    
	    // Debug
	    debugLabel = new Label("debug label");
	    add(debugLabel);
	}
}
