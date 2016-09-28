package edu.stanford.bmir.protege.web.client.xd.instantiation.panels;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.stanford.bmir.protege.web.client.xd.instantiation.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.client.xd.instantiation.old.restriction.DomainRestriction;
import edu.stanford.bmir.protege.web.client.xd.instantiation.old.restriction.ObjectPropertyRangeRestriction;
import edu.stanford.bmir.protege.web.client.xd.instantiation.old.restriction.Restriction;
import edu.stanford.bmir.protege.web.client.xd.instantiation.widgets.RestrictionsWidget;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.LabelOrIri;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.PropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.util.TreeMethods;

public class RestrictionsPanel extends VerticalPanel implements InstantiationWizardPanel {
	
	private DesignPatternInstantiationWizard parentWizard;
	private VerticalPanel restrictionsHolderPanel = new VerticalPanel();
	private Map<String,OntologyEntityFrame> classLookupMap = new HashMap<String, OntologyEntityFrame>();
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
		
		// Clear out old data
		this.classLookupMap.clear();
		
		// Build map of labels or IRIs to classes, needed for looking up class frames from 
		// property ranges later on. Use strings so as to avoid having to overload equals() and hashCode()
		// in LabelOrIri class.
		Set<OntologyEntityFrame> classFramesSet = TreeMethods.flattenFrameTreeToSet(this.parentWizard.getClassTree());
		for (OntologyEntityFrame classFrame: classFramesSet) {
			String classLookupKey;
			if (classFrame.getIri().isPresent()) {
				classLookupKey = classFrame.getIri().get().toString();
			}
			else {
				classLookupKey = classFrame.getLabel();
			}
			classLookupMap.put(classLookupKey, classFrame);
		}
		
		// Generate the set of candidate restrictions
		Set<Restriction> candidateRestrictions = generateCandidateRestrictions();
		
		// Render the generated restrictions
		for (Restriction r: candidateRestrictions) {
			this.restrictionsMap.put(r, false);
			this.restrictionsHolderPanel.add(new RestrictionsWidget(this.parentWizard, r));
		}
	}
	
	public void setRestrictionSelectedStatus(Restriction restriction, Boolean status) {
		this.restrictionsMap.put(restriction, status);
	}
	
	private Set<Restriction> generatePropertyDomainRestrictions(FrameTreeNode<OntologyEntityFrame> propertyTree) {
		// Return value variable
		Set<Restriction> domainRestrictions = new HashSet<Restriction>();
		
		// Flatten and iterate the property tree
		Set<FrameTreeNode<OntologyEntityFrame>> specialisedPropertyNodes = TreeMethods.flattenFrameTree(propertyTree, true);
		for (FrameTreeNode<OntologyEntityFrame> propertyNode: specialisedPropertyNodes) {
			OntologyEntityFrame property = propertyNode.getData();
			
			// This variable holds all candidate domain frames for the property in question
			Set<OntologyEntityFrame> candidateDomainFrames = new HashSet<OntologyEntityFrame>();
			
			// Get all parent property domains (transitively) as candidates
			Set<LabelOrIri> allParentDomains = getParentDomains(propertyNode, true);
			for (LabelOrIri loi: allParentDomains) {
				if (classLookupMap.containsKey(loi.toString())) {
					OntologyEntityFrame lookedUpFrame = classLookupMap.get(loi.toString());
					candidateDomainFrames.add(lookedUpFrame);
				}
			}
			
			// Also get subclasses of direct parent property domains (i.e., possibly newly specialised classes)
			Set<LabelOrIri> directParentDomains = getParentDomains(propertyNode, false);
			Set<OntologyEntityFrame> additionalCandidates = new HashSet<OntologyEntityFrame>();
			for (LabelOrIri loi: directParentDomains) {
				if (classLookupMap.containsKey(loi.toString())) {
					OntologyEntityFrame lookedUpFrame = classLookupMap.get(loi.toString());
					Optional<FrameTreeNode<OntologyEntityFrame>> parentDomainFrameAsOptional = TreeMethods.getFrameTreeForFrame(this.parentWizard.getClassTree(), lookedUpFrame);
					if (parentDomainFrameAsOptional.isPresent()) {
						Set<OntologyEntityFrame> subClassFrames = TreeMethods.flattenFrameTreeToSet(parentDomainFrameAsOptional.get());
						additionalCandidates.addAll(subClassFrames);
					}
				}
			}
			candidateDomainFrames.addAll(additionalCandidates);
			
			// Now, for all candidate domain classes, generate suggestion restrictions for user to accept or reject
			// Only generate suggestions for classes that are specialized, e.g. do not have minted IRIs
			for (OntologyEntityFrame classFrame: candidateDomainFrames) {
				if (!classFrame.getIri().isPresent()) {
					DomainRestriction restriction = new DomainRestriction((PropertyFrame)property, (ClassFrame)classFrame);
					domainRestrictions.add(restriction);
				}
			}
			
		}
		return domainRestrictions;
	}
	
	/**
	 * Get the domains of superproperties of a given property node in a tree (possibly recursively).
	 * @param propertyNode Property node whose parents we want to check
	 * @param recursively Should we recurse to the top of the property tree?
	 * @return
	 */
	private Set<LabelOrIri> getParentDomains(FrameTreeNode<OntologyEntityFrame> propertyNode, Boolean recursively) {
		Set<LabelOrIri> retVal = new HashSet<LabelOrIri>();
		if (propertyNode.getParent() != null) {
			if (propertyNode.getParent().getData() instanceof PropertyFrame) {
				PropertyFrame parentPropertyFrame = (PropertyFrame)propertyNode.getParent().getData();				
				retVal.addAll(parentPropertyFrame.getDomains());
				
			}
			// If we are to recurse, do so
			if (recursively) {
				retVal.addAll(getParentDomains(propertyNode.getParent(), recursively));
			}
		}
		return retVal;
	}
	
	/**
	 * Get the ranges of superproperties of a given property node in a tree (possibly recursively).
	 * @param propertyNode Property node whose parents we want to check
	 * @param recursively Should we recurse to the top of the property tree?
	 * @return
	 */
	private Set<LabelOrIri> getParentRanges(FrameTreeNode<OntologyEntityFrame> propertyNode, Boolean recursively) {
		Set<LabelOrIri> retVal = new HashSet<LabelOrIri>();
		if (propertyNode.getParent() != null) {
			if (propertyNode.getParent().getData() instanceof PropertyFrame) {
				ObjectPropertyFrame parentPropertyFrame = (ObjectPropertyFrame)propertyNode.getParent().getData();				
				retVal.addAll(parentPropertyFrame.getRanges());
				
			}
			// If we are to recurse, do so
			if (recursively) {
				retVal.addAll(getParentRanges(propertyNode.getParent(), recursively));
			}
		}
		return retVal;
	}
	
	
	private Set<Restriction> generateCandidateRestrictions() {
		
		// TODO: migrate restrictions definitions to shared package
		// TODO: Test that all of this works
		
		Set<Restriction> retVal = new HashSet<Restriction>();
		
		FrameTreeNode<OntologyEntityFrame> objectPropertyTree = this.parentWizard.getObjectPropertyTree();
		FrameTreeNode<OntologyEntityFrame> dataPropertyTree = this.parentWizard.getDataPropertyTree();

		retVal.addAll(generatePropertyDomainRestrictions(objectPropertyTree));
		retVal.addAll(generateObjectPropertyRangeRestrictions(objectPropertyTree));
		retVal.addAll(generatePropertyDomainRestrictions(dataPropertyTree));
		
		return retVal;
	}

	private Set<Restriction> generateObjectPropertyRangeRestrictions(FrameTreeNode<OntologyEntityFrame> objectPropertyTree) {
		// Return value
		Set<Restriction> rangeRestrictions = new HashSet<Restriction>();
		
		// Iterate over all specialized object properties
		Set<FrameTreeNode<OntologyEntityFrame>> specializedObjectPropertyNodes = TreeMethods.flattenFrameTree(objectPropertyTree, true);
		for (FrameTreeNode<OntologyEntityFrame> propertyNode: specializedObjectPropertyNodes) {
			
			OntologyEntityFrame property = propertyNode.getData();
			
			Set<OntologyEntityFrame> candidateRangeFrames = new HashSet<OntologyEntityFrame>();
			
			// Get all parent property ranges (transitively) as candidates
			Set<LabelOrIri> allParentRanges = getParentRanges(propertyNode, true);
			for (LabelOrIri loi: allParentRanges) {
				if (classLookupMap.containsKey(loi.toString())) {
					OntologyEntityFrame lookedUpFrame = classLookupMap.get(loi.toString());
					candidateRangeFrames.add(lookedUpFrame);
				}
			}
			
			// Also get subclasses of direct parent property domains (i.e., possibly newly specialised classes)
			Set<LabelOrIri> directParentRanges = getParentRanges(propertyNode, false);
			Set<OntologyEntityFrame> additionalCandidates = new HashSet<OntologyEntityFrame>();
			for (LabelOrIri loi: directParentRanges) {
				if (classLookupMap.containsKey(loi.toString())) {
					OntologyEntityFrame lookedUpFrame = classLookupMap.get(loi.toString());
					Optional<FrameTreeNode<OntologyEntityFrame>> parentRangeFrameAsOptional = TreeMethods.getFrameTreeForFrame(this.parentWizard.getClassTree(), lookedUpFrame);
					if (parentRangeFrameAsOptional.isPresent()) {
						Set<OntologyEntityFrame> subClassFrames = TreeMethods.flattenFrameTreeToSet(parentRangeFrameAsOptional.get());
						additionalCandidates.addAll(subClassFrames);
					}
				}
			}
			candidateRangeFrames.addAll(additionalCandidates);
			
			// Now, for all candidate range classes, generate suggestion restrictions for user to accept or reject
			// Only generate suggestions for classes that are specialized, e.g. do not have minted IRIs
			for (OntologyEntityFrame classFrame: candidateRangeFrames) {
				if (!classFrame.getIri().isPresent()) {
					ObjectPropertyRangeRestriction restriction = new ObjectPropertyRangeRestriction((ObjectPropertyFrame)property, (ClassFrame)classFrame);
					rangeRestrictions.add(restriction);
				}
			}
		}
		
		return rangeRestrictions;
	}
}
