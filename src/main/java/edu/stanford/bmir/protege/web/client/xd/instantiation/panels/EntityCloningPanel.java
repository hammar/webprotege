package edu.stanford.bmir.protege.web.client.xd.instantiation.panels;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.stanford.bmir.protege.web.client.xd.instantiation.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.client.xd.instantiation.util.TreeMethods;
import edu.stanford.bmir.protege.web.client.xd.instantiation.widgets.EntityCloningWidget;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.LabelOrIri;
import edu.stanford.bmir.protege.web.shared.xd.data.PropertyRestriction;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.PropertyFrame;

public class EntityCloningPanel extends VerticalPanel implements InstantiationWizardPanel {
	
	VerticalPanel classHolderPanel;
	VerticalPanel opHolderPanel;
	VerticalPanel dpHolderPanel;
	Label classesHeader;
	Label opHeader;
	Label dpHeader;
	
	
	public EntityCloningPanel(DesignPatternInstantiationWizard parentWizard) {
		super();
		this.setSpacing(10);
		this.parentWizard = parentWizard;
		
		this.classHolderPanel = new VerticalPanel();
		this.classHolderPanel.setSpacing(3);
		this.opHolderPanel = new VerticalPanel();
		this.opHolderPanel.setSpacing(3);
		this.dpHolderPanel = new VerticalPanel();
		this.dpHolderPanel.setSpacing(3);
		
		this.setTitle("Entity Cloning");
		
		Label instructionsLabel = new Label("Please provide labels for the ODP entities below that "
				+ "make sense when adapting the ODP to your domain. ");
		this.add(instructionsLabel);
		
		VerticalPanel innerVp = new VerticalPanel();
		innerVp.setSpacing(10);
		ScrollPanel sp = new ScrollPanel(innerVp);
		sp.setHeight("315px");
		sp.addStyleName("entityCloningFieldsScrollWrapper");
		
		classesHeader = new Label("Classes");
		classesHeader.addStyleName("entityCloningPanelSubheader");
		innerVp.add(classesHeader);
		innerVp.add(this.classHolderPanel);

		opHeader = new Label("Object Properties");
		opHeader.addStyleName("entityCloningPanelSubheader");
		innerVp.add(opHeader);
		innerVp.add(this.opHolderPanel);
		
		dpHeader = new Label("Datatype Properties");
		dpHeader.addStyleName("entityCloningPanelSubheader");
		innerVp.add(dpHeader);
		innerVp.add(this.dpHolderPanel);
		
		this.add(sp);
	}
	
	
	/**
	 * Check whether a property frame should be included in the list of properties to clone.
	 * @param prop - The property in question.
	 * @param propertiesToKeep - A whitelist of properties that should always be kept.
	 * @param clonedClassIris - A list of classes to compare domain/range against.
	 * @return True if the property should be kept.
	 */
	private Boolean propertyCloningFilter(PropertyFrame prop, Set<IRI> propertiesToKeep, Set<IRI> clonedClassIris) {
		Boolean keepFrame = false;
		
		// Keep the node if it is included in the white list of properties to keep
		if (prop.getIri().isPresent()) {
			if (propertiesToKeep.contains(prop.getIri().get())) {
				keepFrame = true;
			}
		}
		
		// Get LabelOrIris involved in domain or range axioms on the property in question
		Set<LabelOrIri> propertyDomainsAndRanges = new HashSet<LabelOrIri>();
		propertyDomainsAndRanges.addAll(prop.getDomains());
		if (prop instanceof ObjectPropertyFrame) {
			ObjectPropertyFrame opf = (ObjectPropertyFrame)prop;
			propertyDomainsAndRanges.addAll(opf.getRanges());
		}
		
		// Keep the frame if the one of the input classes are included in the list of domain/ranges
		for (LabelOrIri loi: propertyDomainsAndRanges) {
			if (loi.getIri().isPresent()) {
				if (clonedClassIris.contains(loi.getIri().get())) {
					keepFrame = true;
				}
			}
		}
		
		return keepFrame;
	}
	
	/** 
	 * Construct a new property frame tree that has been filtered to keep only properties that relate
	 * to input class frames (either where those classes are domain/range or where the properties are used
	 * in restrictions on the classes) 
	 * @param inputTree
	 * @param parentNode - parent node in the tree under construction, initially null 
	 * @param propertiesToKeep - whitelist of properties to keep no matter what
	 * @param clonedClassIris - iris to check against property domains/ranges
	 * @return
	 */
	private FrameTreeNode<OntologyEntityFrame> filterPropertyTree(FrameTreeNode<OntologyEntityFrame> inputTree, FrameTreeNode<OntologyEntityFrame> parentNode, Set<IRI> propertiesToKeep, Set<IRI> clonedClassIris) {
		
		// Construct a new tree
		FrameTreeNode<OntologyEntityFrame> retVal = new FrameTreeNode<OntologyEntityFrame>(inputTree.getData());
		// Cast to property frame, because everything that goes in this 
		// method should be a property frame tree
		PropertyFrame pf = (PropertyFrame)inputTree.getData();
		
		// Check if the node should be added to the target tree or if it should be skipped and 
		// its children attached to its parent instead. Recurse.
		if (propertyCloningFilter(pf, propertiesToKeep, clonedClassIris) || parentNode==null) {
			for (FrameTreeNode<OntologyEntityFrame> childNode: inputTree.getChildren()) {
				FrameTreeNode<OntologyEntityFrame> newChildTree = filterPropertyTree(childNode, retVal, propertiesToKeep, clonedClassIris);
				if (!newChildTree.getData().equals(retVal.getData())) {
					retVal.addChildTree(newChildTree);
				}
			}
		}
		else {
			for (FrameTreeNode<OntologyEntityFrame> childNode: inputTree.getChildren()) {
				FrameTreeNode<OntologyEntityFrame> newChildTree = filterPropertyTree(childNode, parentNode,  propertiesToKeep, clonedClassIris);
				if (!newChildTree.getData().equals(parentNode.getData())) {
					parentNode.addChildTree(newChildTree);
				}
			}
			return parentNode;
		}
		return retVal;
	}
	
	@Override
	public void renderPanel() {
		// Clear out existing cloning widgets
		this.classHolderPanel.clear();
		this.opHolderPanel.clear();
		this.dpHolderPanel.clear();
		
		// Render new class cloning widgets
		FrameTreeNode<OntologyEntityFrame> classesToCloneTree = TreeMethods.tightenTree(parentWizard.getClassTree());
		Map<OntologyEntityFrame, Integer> classesToCloneDepthMap = TreeMethods.getFrameTreeAsIndentMap(classesToCloneTree);
		if (classesToCloneDepthMap.size()>0) {
			classesHeader.setVisible(true);
		}
		else {
			classesHeader.setVisible(false);
		}
		for (Map.Entry<OntologyEntityFrame, Integer> entry : classesToCloneDepthMap.entrySet()) {
			String prefix = buildPrefix(entry.getValue());
			this.classHolderPanel.add(new EntityCloningWidget(this.parentWizard, entry.getKey(), prefix));
		}
		
		// Get the list of source class iris to be cloned from the above, and the list
		// of property iris involved in class restrictions on such classes.
		// TODO: check whether the below used subclassof and equivalent restrictions are actually created server-side!
		Set<IRI> clonedClassIris = new HashSet<IRI>();
		Set<IRI> restrictionPropertyIris = new HashSet<IRI>();
		Set<OntologyEntityFrame> classesToCloneAsOefs = classesToCloneDepthMap.keySet();
		for (OntologyEntityFrame oef: classesToCloneAsOefs) {
			ClassFrame cf = (ClassFrame)oef;
			if (cf.getIri().isPresent()) {
				clonedClassIris.add(cf.getIri().get());
			}
			Set<PropertyRestriction> classRestrictions = cf.getSubClassOfAndEquivalentToRestrictions();
			for (PropertyRestriction restriction: classRestrictions) {
				ObjectPropertyFrame prop = restriction.getProperty();
				if (prop.getIri().isPresent()) {
					restrictionPropertyIris.add(prop.getIri().get());
				}
			}
		}
		
		// Render new object property cloning widgets
		FrameTreeNode<OntologyEntityFrame> objectPropertyTree = parentWizard.getObjectPropertyTree();
		FrameTreeNode<OntologyEntityFrame> filteredObjectPropertyTree = filterPropertyTree(objectPropertyTree, null, restrictionPropertyIris, clonedClassIris);
		Map<OntologyEntityFrame, Integer> objectPropertyDepthMap = TreeMethods.getFrameTreeAsIndentMap(filteredObjectPropertyTree);
		if (objectPropertyDepthMap.size()>0) {
			opHeader.setVisible(true);
		}
		else {
			opHeader.setVisible(false);
		}
		for (Map.Entry<OntologyEntityFrame, Integer> entry : objectPropertyDepthMap.entrySet()) {
			String prefix = buildPrefix(entry.getValue());
			this.opHolderPanel.add(new EntityCloningWidget(this.parentWizard, entry.getKey(), prefix));
		}
		
		// Render new datatype property cloning widgets
		FrameTreeNode<OntologyEntityFrame> dataPropertyTree = parentWizard.getDataPropertyTree();
		FrameTreeNode<OntologyEntityFrame> filteredDataPropertyTree = filterPropertyTree(dataPropertyTree, null, restrictionPropertyIris, clonedClassIris);
		Map<OntologyEntityFrame, Integer> dataPropertyDepthMap = TreeMethods.getFrameTreeAsIndentMap(filteredDataPropertyTree);
		if (dataPropertyDepthMap.size()>0) {
			dpHeader.setVisible(true);
		}
		else {
			dpHeader.setVisible(false);
		}
		for (Map.Entry<OntologyEntityFrame, Integer> entry : dataPropertyDepthMap.entrySet()) {
			String prefix = buildPrefix(entry.getValue());
			this.dpHolderPanel.add(new EntityCloningWidget(this.parentWizard, entry.getKey(), prefix));
		}
	}
	
	private String buildPrefix(Integer length) {
		return "|" + new String(new char[length]).replace('\0', '-') + " ";
	}
	
	private DesignPatternInstantiationWizard parentWizard;

}
