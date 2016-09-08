package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.xd.specialization.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.client.xd.specialization.widgets.AlignmentWidget;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetSpecializationAlignmentSuggestionsAction;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;
import edu.stanford.bmir.protege.web.shared.xd.results.GetSpecializationAlignmentSuggestionsResult;

public class AlignmentsPanel extends VerticalPanel implements InstantiationWizardPanel {
	
	private DesignPatternInstantiationWizard parentWizard;
	private VerticalPanel alignmentsHolderPanel = new VerticalPanel();
	
	public AlignmentsPanel(DesignPatternInstantiationWizard parent) {
		super();
		this.setSpacing(10);
		this.parentWizard = parent;
		
        this.setTitle("CODP Specialisation Alignment");
        
        Label instructionsLabel = new Label("From the suggested entity alignments listed below, constructed based on your specialised "
        		+ "entities and the existing ontology entities, please select the ones that hold within your model.");
        this.add(instructionsLabel);
        
		this.alignmentsHolderPanel.setSpacing(10);
		this.alignmentsHolderPanel.addStyleName("xdpAlignmentPanelAlignmentHolder");
		ScrollPanel sp = new ScrollPanel(alignmentsHolderPanel);
		sp.setHeight("315px");
		sp.addStyleName("xdpAlignmentPanelScrollWrapper");
		this.add(sp);
	}
	
	private void renderAlignmentWidgets(Set<Alignment> alignments) {
		
		// First put all the alignments into a map structure organised by their heading
		Map<String,List<Alignment>> sortingMap = new HashMap<String,List<Alignment>>();
		for (Alignment a: alignments) {
			String sortableEntityLabel = a.getSortableEntityLabel();
			if (sortingMap.containsKey(sortableEntityLabel)) {
				sortingMap.get(sortableEntityLabel).add(a);
			}
			else {
				sortingMap.put(sortableEntityLabel, new ArrayList<Alignment>(Arrays.asList(a)));
			}
		}
		// Then iterate through said map, constructing suitable headings and alignment widgets on the page as needed
		Iterator<Entry<String, List<Alignment>>> it = sortingMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String,List<Alignment>> pair = (Map.Entry<String,List<Alignment>>)it.next();
			Label aligmentGroupHeading = new Label(pair.getKey());
			aligmentGroupHeading.addStyleName("xdpAlignmentPanelAlignmentGroupHeading");
			this.alignmentsHolderPanel.add(aligmentGroupHeading);
			for (Alignment a: pair.getValue()) {
				this.alignmentsHolderPanel.add(new AlignmentWidget(this.parentWizard, a));
			}
		}
		
	}

	@Override
	public void renderPanel() {
		// Check if alignments are out of date (e.g., if instantiation has been modified after last time alignments were modified)
		// If so, redraw the interface. Otherwise, keep the old interface as is.
		
		if (!parentWizard.areAlignmentsUpToDate()) {
			
			// Initiate some spinner UI
			// TODO: Implement
			
			// Clear out the existing UI and widgets
			this.alignmentsHolderPanel.clear();
			
			// Call server wait for results
			GetSpecializationAlignmentSuggestionsAction action = new GetSpecializationAlignmentSuggestionsAction(parentWizard.getProjectId(), 
					parentWizard.getClassTree(),
					parentWizard.getObjectPropertyTree(),
					parentWizard.getDataPropertyTree(), 
					parentWizard.getInstantiationMethod());
			DispatchServiceManager.get().execute(action, new DispatchServiceCallback<GetSpecializationAlignmentSuggestionsResult>() {
	        	@Override
	            public void handleSuccess(GetSpecializationAlignmentSuggestionsResult result) {
	        		
	        		// Render results
	        		renderAlignmentWidgets(result.getAlignments());
	        		
	        		// Kill the spinner UI
	    			// TODO: Implement

	            }
	        });
		}
	}
}
