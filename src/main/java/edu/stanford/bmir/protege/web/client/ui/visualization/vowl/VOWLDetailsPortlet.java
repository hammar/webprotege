package edu.stanford.bmir.protege.web.client.ui.visualization.vowl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.google.common.base.Optional;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.gwtext.client.widgets.layout.FitLayout;

import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;
import edu.stanford.bmir.protege.web.client.ui.visualization.change.ChangeListener;
import edu.stanford.bmir.protege.web.client.ui.visualization.change.Changeable;
import edu.stanford.bmir.protege.web.client.ui.visualization.change.ChangedEvent;
import edu.stanford.bmir.protege.web.client.ui.visualization.loading.GraphListener;
import edu.stanford.bmir.protege.web.client.ui.visualization.loading.GraphLoadedEvent;
import edu.stanford.bmir.protege.web.shared.selection.SelectionModel;

/**
 * This renders general details for the selected ontology.
 * @author Maria Michou
 *
 */
public class VOWLDetailsPortlet extends AbstractOWLEntityPortlet implements GraphListener, Changeable {

	private static final String DETAILS_TITLE = " Ontology Details";
	
	private VOWLDetailsViewPresenter presenter;
	private VOWLDetailsView view;
	private boolean loaded = false;
	// Listeners to selection events in this portlet
		private Collection<ChangeListener> changeListeners;
		private Optional<String> selectedValue = Optional.absent();
	
	public VOWLDetailsPortlet(SelectionModel selectionModel, Project project) {
		super(selectionModel, project);
		this.changeListeners = new ArrayList<ChangeListener>();
	}

	@Override
	public void initialize() {
		setLayout(new FitLayout());
		setTitle(DETAILS_TITLE);
		view = new VOWLDetailsViewImpl();
		add(view.getWidget());
	}
	
	
	/**
	 * GraphListener implementation method.
	 * This is called when the graph is loaded in the Visualization portlet.
	 * It then passes the Visualization Javascript object to the Details portlet,
	 * which in turn, renders the corresponding details.
	 */
	@Override
	public void graphLoaded(GraphLoadedEvent event) {

		VOWLVisualizationJso visualizationJso = event.getLoadable().getVisualizationObject();
		GWT.log("[VOWL] Graph status: Loaded.");
		if(!loaded) {
			view.renderDetailsStaticInfo(visualizationJso);
			loaded = true;
			
			ListBox lang = view.getListBox();
			lang.addChangeHandler(new ChangeHandler(){

				@Override
				public void onChange(ChangeEvent event) {
					OptionElement element = event.getNativeEvent().getEventTarget().cast();
					selectedValue = Optional.of(element.getValue());
					GWT.log("[VOWL] Change in Drop Down: "+ selectedValue.get());
					notifyChangeListeners(new ChangedEvent(VOWLDetailsPortlet.this));
				}
		    	
		    });
		}
	}


	@Override
	public void handleActivated() {
		super.handleActivated();
		GWT.log("[VOWL] Ontology Details portlet status: Activated.");
		
		
	}

	@Override
	protected void onRefresh() {

	}	
	
	@Override
	public void addChangeListener(ChangeListener listener) {
		changeListeners.add(listener);
		
	}



	@Override
	public void notifyChangeListeners(ChangedEvent changeEvent) {
		for (ChangeListener listener: changeListeners) {
			listener.isChanged(new ChangedEvent(this));
		}
	}



	@Override
	public void removeChangeListener(ChangeListener listener) {
		changeListeners.remove(listener);
	}



	@Override
	public Collection<? extends Object> getChange() {
		if (selectedValue.isPresent())
			return Arrays.asList(selectedValue.get());
		else return Collections.emptyList();
	}



	@Override
	public void setChange(Collection<? extends Object> selection) {
		// TODO Auto-generated method stub
		
	}

}
