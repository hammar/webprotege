package edu.stanford.bmir.protege.web.client.xd;

import java.util.Collection;

import com.google.gwt.user.client.ui.Label;

import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.rpc.data.EntityData;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;

/***
 * Portlet listing existing ODP instantiations in ontology.
 * @author Karl Hammar
 *
 */
@SuppressWarnings("unchecked")
public class XdPatternDetailsPortlet extends AbstractOWLEntityPortlet {
	
	public XdPatternDetailsPortlet(Project project) {
		super(project);
	}
	
	// Portlet UI widget definitions
	private Label helloWorldLabel;

	@Override
	public Collection<EntityData> getSelection() {
		return null;
	}

	@Override
	public void reload() {
		setTitle("ODP Details Reloaded!");
	}

	// Initialization method for GUI
	@Override
	public void initialize() {
		setTitle("ODP Details");
		
		// Initialize UI widgets
		helloWorldLabel = new Label("Pattern details will go here.");
		
        // Add UI widgets to portlet canvas
		add(helloWorldLabel);
	}
}
