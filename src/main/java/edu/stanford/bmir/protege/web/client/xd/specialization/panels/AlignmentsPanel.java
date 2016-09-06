package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.stanford.bmir.protege.web.client.xd.specialization.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.client.xd.specialization.widgets.AlignmentWidget;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.EquivalentClassesAlignment;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;

public class AlignmentsPanel extends VerticalPanel implements InstantiationWizardPanel {
	
	private DesignPatternInstantiationWizard parentWizard;
	private VerticalPanel innerVp = new VerticalPanel();
	private VerticalPanel alignmentsHolderPanel = new VerticalPanel();
	
	
	
	// TODO: Figure out how to detect and handle the case when this panel needs to be cleared and
	// re-rendered, i.e., when the entities and/or restrictions from the previous two panels are
	// re-labelled or otherwise changed.
	
	public AlignmentsPanel(DesignPatternInstantiationWizard parent) {
		super();
		this.setSpacing(10);
		this.parentWizard = parent;
		
        this.setTitle("CODP Specialisation Alignment");
        
        //FlowPanel instructionPanel = new FlowPanel();
        Label instructionsLabel = new Label("From the suggested entity alignments listed below, constructed based on your specialised "
        		+ "entities and the existing ontology entities, please select the ones that hold within your model.");
        this.add(instructionsLabel);
        //instructionPanel.add(instruction);
        //this.addNorth(instructionPanel, 4);
        
        //VerticalPanel innerVp = new VerticalPanel();
		innerVp.setSpacing(10);
		ScrollPanel sp = new ScrollPanel(innerVp);
		sp.setHeight("315px");
		// TODO: Change this into a unique name for the alignments panel
		sp.addStyleName("entityCloningFieldsScrollWrapper");
        
		this.alignmentsHolderPanel.setSpacing(3);
		this.alignmentsHolderPanel.addStyleName("xdpAlignmentPanelAlignmentHolder");
		innerVp.add(this.alignmentsHolderPanel);
		this.add(sp);
        
		// TODO: remove the below test code 
		EquivalentClassesAlignment eca1 = new EquivalentClassesAlignment(new ClassFrame("Test 1"),new ClassFrame("Test 2"));
		EquivalentClassesAlignment eca2 = new EquivalentClassesAlignment(new ClassFrame("Test 3"),new ClassFrame("Test 4"));
		EquivalentClassesAlignment eca3 = new EquivalentClassesAlignment(new ClassFrame("Test 5"),new ClassFrame("Test 6"));
		EquivalentClassesAlignment eca4 = new EquivalentClassesAlignment(new ClassFrame("Test 3"),new ClassFrame("Test 8"));
		Set<Alignment> alignments = new HashSet<Alignment>();
		alignments.add(eca1);
		alignments.add(eca2);
		alignments.add(eca3);
		alignments.add(eca4);
		this.renderAlignmentWidgets(alignments);
		/*this.alignmentsHolderPanel.add(new AlignmentWidget(this.parentWizard, eca1));
		this.alignmentsHolderPanel.add(new AlignmentWidget(this.parentWizard, eca2));
		this.alignmentsHolderPanel.add(new AlignmentWidget(this.parentWizard, eca3));
		this.alignmentsHolderPanel.add(new AlignmentWidget(this.parentWizard, eca4));*/
        
        //this.add(new Label("Alignment interface to be implemented."));
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
			
			// TODO: Clear out the existing UI and widgets
			//this.verticalPanel.clear();
			
			// TODO: Flatten data, call server wait for results
			
			// TODO: Render results
			// TODO: Remove test data

			//this.add(new Label("Behold; I have been redrawn!")); 
			
			// Kill the spinner UI
			// TODO: Implement
		}
		

	}
}
