package edu.stanford.bmir.protege.web.client.ui.visualization.vowl;

import java.util.Collection;

import com.google.gwt.core.client.GWT;
import com.gwtext.client.widgets.layout.FitLayout;

import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;
import edu.stanford.bmir.protege.web.client.ui.visualization.selection.SelectionEvent;
import edu.stanford.bmir.protege.web.client.ui.visualization.selection.SelectionListener;
import edu.stanford.bmir.protege.web.shared.selection.SelectionModel;

/**
 * This renders specific details for the selected node or label.
 * @author Maria Michou
 *
 */
public class VOWLSelectionDetailsPortlet extends AbstractOWLEntityPortlet implements SelectionListener {

	private static final String DETAILS_TITLE = "Selection Details";
	
	private VOWLSelectionDetailsView view;
	
	public VOWLSelectionDetailsPortlet(SelectionModel selectionModel, Project project) {
		super(selectionModel, project);
	}
	
	@Override
	public void initialize() {
		setLayout(new FitLayout());
		setTitle(DETAILS_TITLE);
		view = new VOWLSelectionDetailsViewImpl();
		
		add(view.getWidget());
        
	}
	
	/**
	 * SelectionListener implementation method. 
	 * Called when notified by some {@link edu.stanford.bmir.protege.web.client.ui.visualization.selection.Selectable} object 
	 * (e.g., a class, property, etc in the visualization portlet), that 
	 * their selections have been updated and that listeners should refresh content.
	 */
	@Override
	public void selectionChanged(SelectionEvent event) {

		Collection<? extends Object> selection = event.getSelectable().getSelection();
		
		if (selection.size() > 0) {
			Object selectionData = selection.iterator().next();
			if (selectionData instanceof String) {
				String selectedEntity = (String)selectionData;
				GWT.log("[VOWL] Selection is changed: "+ selectedEntity);

				view.renderDetailsDynamicInfo(event.getSelectable().getPanel(), "<h3>Selection Details</h3>");
			}
		}
		 
	}
	
	@Override
	public void handleActivated() {
		super.handleActivated();
		GWT.log("[VOWL] Selection Details portlet status: Activated.");
	}
	
}
