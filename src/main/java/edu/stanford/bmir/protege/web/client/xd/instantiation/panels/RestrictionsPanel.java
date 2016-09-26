package edu.stanford.bmir.protege.web.client.xd.instantiation.panels;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.stanford.bmir.protege.web.client.xd.instantiation.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.client.xd.instantiation.old.restriction.DomainRestriction;
import edu.stanford.bmir.protege.web.client.xd.instantiation.old.restriction.Restriction;
import edu.stanford.bmir.protege.web.client.xd.instantiation.widgets.RestrictionsWidget;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class RestrictionsPanel extends VerticalPanel implements InstantiationWizardPanel {
	
	private DesignPatternInstantiationWizard parentWizard;
	private VerticalPanel restrictionsHolderPanel = new VerticalPanel();
	private Map<Restriction,Boolean> restrictionsMap = new HashMap<Restriction,Boolean>();

	public RestrictionsPanel(DesignPatternInstantiationWizard parentWizard) {
		super();
		this.setSpacing(10);
		this.parentWizard = parentWizard;
		this.setTitle("Restrictions");
		
		Label instructionsLabel = new Label("Please select those of the suggested restrictions below that make are relevant in "
        		+ "the domain that you are modelling. These restrictions will be persisted as domain/range axioms and as "
        		+ "property restriction axioms.");
		this.add(instructionsLabel);
		
		this.restrictionsHolderPanel.setSpacing(10);
		this.restrictionsHolderPanel.addStyleName("xdpRestrictionsPanelRestrictionsHolder");
		ScrollPanel sp = new ScrollPanel(restrictionsHolderPanel);
		sp.setHeight("315px");
		sp.addStyleName("xdpRestrictionsPanelScrollWrapper");
		this.add(sp);
	}

	@Override
	public void renderPanel() {
		// TODO: Do something about timestamps and resetting the values and so forth
		// TODO: Actually implement this
		Set<Restriction> candidateRestrictions = generateCandidateRestrictions();
		for (Restriction r: candidateRestrictions) {
			this.restrictionsMap.put(r, false);
			this.restrictionsHolderPanel.add(new RestrictionsWidget(this, r));
		}
	}
	
	public void setRestrictionSelectedStatus(Restriction restriction, Boolean status) {
		this.restrictionsMap.put(restriction, status);
	}
	
	public void persistRestrictions() {
		// TODO: store everything in parent wizard trees
		// TODO: also update the modification timestamps accordingly
	}
	
	private Set<Restriction> generateCandidateRestrictions() {
		// TODO: Implement this
		// TODO: add code to take strategy choice into account when generating restriction suggestions, in case we add it back to the wizard later.
		// TODO: migrate restrictions definitions to shared package
		Set<Restriction> retVal = new HashSet<Restriction>();
		
		// Add initial test data
		FrameTreeNode<OntologyEntityFrame> classTree = this.parentWizard.getClassTree();
		DomainRestriction rootRestriction = new DomainRestriction(new DataPropertyFrame("Property " + classTree.getData().getCurrentLabel()), new ClassFrame("Class " + classTree.getData().getCurrentLabel()));
		retVal.add(rootRestriction);
		
		// Add some more junk test data
		for (int i=0; i<10; i++) {
			DomainRestriction restriction = new DomainRestriction(new DataPropertyFrame("Data Property " + i), new ClassFrame("Class " + i));
			retVal.add(restriction);
		}
		return retVal;
	}
}
