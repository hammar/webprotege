package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.gwtext.client.core.SortDir;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.GroupingStore;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SortState;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxSelectionModel;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GroupingView;
import com.gwtext.client.widgets.layout.FitLayout;

import edu.stanford.bmir.protege.web.client.xd.specialization.XdSpecializationWizard;
import edu.stanford.bmir.protege.web.client.xd.specialization.restriction.DomainRestriction;
import edu.stanford.bmir.protege.web.client.xd.specialization.restriction.ObjectPropertyRangeRestriction;
import edu.stanford.bmir.protege.web.client.xd.specialization.restriction.Restriction;
import edu.stanford.bmir.protege.web.client.xd.util.UUID;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.LabelOrIri;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpSpecializationStrategy;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.PropertyFrame;

public class PropertyRestrictionPanel extends Panel {
	
	private Map<String,Restriction> restrictionsMap;
	private Map<String,OntologyEntityFrame> classLookupMap;
	private FrameTreeNode<OntologyEntityFrame> classes;
	private FrameTreeNode<OntologyEntityFrame> objectProperties;
	private FrameTreeNode<OntologyEntityFrame> dataProperties;
	
	private XdSpecializationWizard parentWizard; 
	
	// Infrastructure for restrictions presentation grid view
	private GroupingStore store;
	private RecordDef recordDef;
	private CheckboxSelectionModel cbSelectionModel;
	
	public PropertyRestrictionPanel(XdSpecializationWizard parent) {
		super();
		
		this.parentWizard = parent;
		
        this.setBorder(false);  
        this.setId("card-2");
        this.setTitle("Property Restriction");
        this.setLayout(new FitLayout());
        
        this.classLookupMap = new HashMap<String, OntologyEntityFrame>();
        this.restrictionsMap = new HashMap<String,Restriction>();
        this.cbSelectionModel = new CheckboxSelectionModel();
        
        MemoryProxy proxy = new MemoryProxy(new Object[0][3]);
        recordDef = new RecordDef(  
                new FieldDef[]{  
                        new StringFieldDef("restriction"),
                        new StringFieldDef("entity"),
                        new StringFieldDef("restrictionId")
                }  
        );  
  
        ArrayReader reader = new ArrayReader(recordDef);  
  
        store = new GroupingStore();  
        store.setReader(reader);  
        store.setDataProxy(proxy);  
        store.setSortInfo(new SortState("restriction", SortDir.ASC));  
        store.setGroupField("entity");  
        store.load();  
  
        BaseColumnConfig[] columns = new BaseColumnConfig[]{  
        		new CheckboxColumnConfig(cbSelectionModel),
                new ColumnConfig("Restriction", "restriction", 160, true, null, "restriction"),  
                new ColumnConfig("OWL Entity", "entity", 60, true)  
        };  
  
        ColumnModel columnModel = new ColumnModel(columns);
        GridPanel grid = new GridPanel();  
        grid.setStore(store);  
        grid.setColumnModel(columnModel);  
        grid.setFrame(true);  
        grid.setStripeRows(true);  
        grid.setAutoExpandColumn("restriction"); 
        grid.setSelectionModel(cbSelectionModel);
  
        GroupingView gridView = new GroupingView();  
        gridView.setForceFit(true);  
        gridView.setGroupTextTpl("{text} ({[values.rs.length]} {[values.rs.length > 1 ? \"Candidate Restrictions\" : \"Candidate Restriction\"]})");  
        grid.setView(gridView);  
        grid.setFrame(true);   
        grid.setAnimCollapse(false);
        grid.setAutoScroll(true);
        
        this.add(grid);
	}
	
	
	/**
	 * Get the entity frames of a given tree of owl entities, flattened to a set.
	 * @param entityTree Tree to search over
	 * @param onlySpecialized Only return specialized (e.g., non-IRI-carrying) frames
	 * @return Specialized class frames from the input set
	 */
	private Set<OntologyEntityFrame> getFramesAsSet(FrameTreeNode<OntologyEntityFrame> entityTree, Boolean onlySpecialized) {
		Set<OntologyEntityFrame> specializedEntities = new HashSet<OntologyEntityFrame>();
		
		// Add the node itself unless we are only looking for specialized frames and this already has an IRI 
		// (e.g., is not a specialized frame) 
		if (!(onlySpecialized && entityTree.getData().getIri().isPresent())) {
			specializedEntities.add(entityTree.getData());
		}
		// Recurse into child nodes
		for (FrameTreeNode<OntologyEntityFrame> childTree: entityTree.getChildren()) {
			specializedEntities.addAll(getFramesAsSet(childTree, onlySpecialized));
		}
		return specializedEntities;
	}

	
	// Load the entities from parent wizard (ODP entities + specialized entities)
	// and generate candidate restrictions from them for the user to accept or reject
	public void loadEntities() {
		
		this.classLookupMap.clear();
		this.restrictionsMap.clear();
		this.store.removeAll();
		this.store.commitChanges();
		
        this.classes = parentWizard.getAllClasses();
        this.objectProperties = parentWizard.getAllObjectProperties();
        this.dataProperties = parentWizard.getAllDataProperties();
		
        Set<Restriction> allRestrictions = new HashSet<Restriction>();
        
		// Render property-oriented restrictions
		if (parentWizard.getSpecializationStrategy() == OdpSpecializationStrategy.PROPERTY_ORIENTED ||
				parentWizard.getSpecializationStrategy() == OdpSpecializationStrategy.HYBRID) {
			
			// Build map of labels or IRIs to specialized classes, needed for looking up class frames from 
			// property ranges later on. Use strings so as to avoid having to overload equals() and hashCode()
			// in LabelOrIri class.
			Set<OntologyEntityFrame> classFramesSet = getFramesAsSet(this.classes, false);
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
			
			allRestrictions.addAll(generateObjectPropertyRangeRestrictions());
			allRestrictions.addAll(generatePropertyDomainRestrictions(this.objectProperties));
			allRestrictions.addAll(generatePropertyDomainRestrictions(this.dataProperties));
		}
		
		// Render class-oriented restrictions
		if (parentWizard.getSpecializationStrategy() == OdpSpecializationStrategy.CLASS_ORIENTED ||
				parentWizard.getSpecializationStrategy() == OdpSpecializationStrategy.HYBRID) {
			// TODO: Build Restrictions according to class-oriente sstrategy, add to allRestrictions set
		}
		
		for (Restriction restriction: allRestrictions) {
			String restrictionKey = UUID.uuid();
			// Put into map for later reference
			restrictionsMap.put(restrictionKey, restriction);
			// Create record and put in store for display to user
			Record record = recordDef.createRecord(new Object[]{restriction.toString(), restriction.getEntityLabel(), restrictionKey});
			store.add(record);
		}
		
		store.groupBy("entity", true);
		store.commitChanges();
	}
	
	/**
	 * Generates candidate restrictions for the user to accept/reject based on the input frame tree 
	 * (expected to hold property nodes). Restrictions are also added to the restrictionsMap for future
	 * reference when persisting selected references, and are added to the gridview datastore so
	 * that users may select them.
	 * @param frameTree tree of PropertyFrame objects
	 */
	private Set<Restriction> generatePropertyDomainRestrictions(FrameTreeNode<OntologyEntityFrame> frameTree) {
		Set<Restriction> domainRestrictions = new HashSet<Restriction>();
		
		// Iterate over all specialized object properties
		Set<OntologyEntityFrame> specializedProperties = new HashSet<OntologyEntityFrame>();
		specializedProperties.addAll(getFramesAsSet(frameTree, true));
		for (OntologyEntityFrame property: specializedProperties) {
			
			// Get parent domains and translate from LabelOrIri objects to class frames
			// using lookup map established earlier
			Set<LabelOrIri> candidateDomains = getParentDomains(property, frameTree);
			Set<OntologyEntityFrame> candidateDomainFrames = new HashSet<OntologyEntityFrame>();
			for (LabelOrIri loi: candidateDomains) {
				if (classLookupMap.containsKey(loi.toString())) {
					OntologyEntityFrame lookedUpFrame = classLookupMap.get(loi.toString());
					candidateDomainFrames.add(lookedUpFrame);
				}
			}
			
			// For each candidate domain class, also get all subclasses (potential domains for subproperty)
			Set<OntologyEntityFrame> additionalCandidates = new HashSet<OntologyEntityFrame>();
			for (OntologyEntityFrame classFrame: candidateDomainFrames) {
				Set<OntologyEntityFrame> subClassFrames = getSpecializedSubEntitiesOf(classFrame, this.classes);
				additionalCandidates.addAll(subClassFrames);
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
	 * Generates candidate restrictions for the user to accept/reject based on the specialized object
	 * properties from the last screen. Restrictions are also added to the restrictionsMap for future
	 * reference when persisting selected references, and are added to the gridview datastore so
	 * that users may select them.
	 */
	private Set<Restriction> generateObjectPropertyRangeRestrictions() {
		Set<Restriction> rangeRestrictions = new HashSet<Restriction>();
		
		// Iterate over all specialized object properties
		Set<OntologyEntityFrame> specializedObjectProperties = new HashSet<OntologyEntityFrame>();
		specializedObjectProperties.addAll(getFramesAsSet(this.objectProperties, true));
		for (OntologyEntityFrame property: specializedObjectProperties) {
			
			// Get parent ranges and translate from LabelOrIri objects to class frames
			// using lookup map established earlier
			Set<LabelOrIri> candidateRanges = getParentRanges(property, this.objectProperties);
			Set<OntologyEntityFrame> candidateRangeFrames = new HashSet<OntologyEntityFrame>();
			for (LabelOrIri loi: candidateRanges) {
				if (classLookupMap.containsKey(loi.toString())) {
					OntologyEntityFrame lookedUpFrame = classLookupMap.get(loi.toString());
					candidateRangeFrames.add(lookedUpFrame);
				}
			}
			
			// For each candidate range class, also get all subclasses (potential ranges for subproperty)
			Set<OntologyEntityFrame> additionalCandidates = new HashSet<OntologyEntityFrame>();
			for (OntologyEntityFrame classFrame: candidateRangeFrames) {
				Set<OntologyEntityFrame> subClassFrames = getSpecializedSubEntitiesOf(classFrame, this.classes);
				additionalCandidates.addAll(subClassFrames);
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
	
	/**
	 * Get the specialized entity frames of a given frame tree of owl entities, starting from a known 
	 * frame, and flattened to a set. 
	 * @param entity The parent entity frame
	 * @param tree Tree to search over
	 * @return Set of frames that are in the given tree defined as children of input frame
	 */
	private Set<OntologyEntityFrame> getSpecializedSubEntitiesOf(OntologyEntityFrame entity, FrameTreeNode<OntologyEntityFrame> tree) {
		FrameTreeNode<OntologyEntityFrame> startingNode = findNodeForFrame(entity, tree);
		return getFramesAsSet(startingNode, true);
	}
	
	/**
	 * Find the FrameTreeNode in a tree that hosts a given OntologyEntityFrame. 
	 * @param entity The ontology entity frame to look for
	 * @param treeNode The tree in which to look
	 * @return Matching FrameTreeNode if one can be found, otherwise null
	 */
	private FrameTreeNode<OntologyEntityFrame> findNodeForFrame(OntologyEntityFrame entity, FrameTreeNode<OntologyEntityFrame> treeNode) {
		if (treeNode.getData() == entity) {
			return treeNode;
		}
		else {
			for (FrameTreeNode<OntologyEntityFrame> childNode: treeNode.getChildren()) {
				FrameTreeNode<OntologyEntityFrame> retVal = findNodeForFrame(entity, childNode);
				if (retVal != null) {
					return retVal;
				}
			}
			return null;
		}
	}
	
	/**
	 * Wraps {@link getParentRanges(FrameTreeNode<OntologyEntityFrame>)} for when we don't have a tree node to begin 
	 * with but have to find it first from an input object property frame. If we cannot find the tree node, returns
	 * an empty set.
	 * @param Object property frame whose parents ranges we are looking for
	 * @param Tree of object property frames
	 * @return Set of LabelOrIri objects representing ranges of all parent object properties. If 
	 */
	private Set<LabelOrIri> getParentRanges(OntologyEntityFrame property, FrameTreeNode<OntologyEntityFrame> tree) {
		FrameTreeNode<OntologyEntityFrame> startingNode = findNodeForFrame(property, tree);
		if (startingNode == null) {
			return Collections.emptySet();
		}
		return getParentRanges(startingNode);
	}
	
	/**
	 * Return any ranges defined in object property frames held by parent tree nodes to this one recursively.
	 * @param propertyNode starting node
	 * @return set of labelOrIri objects that are ranges of parent object properties
	 */
	private Set<LabelOrIri> getParentRanges(FrameTreeNode<OntologyEntityFrame> propertyNode) {
		Set<LabelOrIri> retVal = new HashSet<LabelOrIri>();
		if (propertyNode.getParent() != null) {
			if (propertyNode.getParent().getData() instanceof ObjectPropertyFrame) {
				ObjectPropertyFrame parentPropertyFrame = (ObjectPropertyFrame)propertyNode.getParent().getData();				
				retVal.addAll(parentPropertyFrame.getRanges());
				
			}
			// recurse
			retVal.addAll(getParentRanges(propertyNode.getParent()));
		}
		return retVal;
	}
	
	
	/**
	 * Wraps {@link getParentDomains(FrameTreeNode<OntologyEntityFrame>)} for when we don't have a tree node to begin 
	 * with but have to find it first from an input object property frame. If we cannot find the tree node, returns
	 * an empty set.
	 * @param Property frame whose parents ranges we are looking for
	 * @param Tree of property frames
	 * @return Set of LabelOrIri objects representing ranges of all parent object properties. If 
	 */
	private Set<LabelOrIri> getParentDomains(OntologyEntityFrame property, FrameTreeNode<OntologyEntityFrame> tree) {
		FrameTreeNode<OntologyEntityFrame> startingNode = findNodeForFrame(property, tree);
		if (startingNode == null) {
			return Collections.emptySet();
		}
		return getParentDomains(startingNode);
	}
	
	/**
	 * Return any domains defined in property frames held by parent tree nodes to this one recursively.
	 * @param propertyNode starting node
	 * @return set of labelOrIri objects that are ranges of parent properties
	 */
	private Set<LabelOrIri> getParentDomains(FrameTreeNode<OntologyEntityFrame> propertyNode) {
		Set<LabelOrIri> retVal = new HashSet<LabelOrIri>();
		if (propertyNode.getParent() != null) {
			if (propertyNode.getParent().getData() instanceof PropertyFrame) {
				PropertyFrame parentPropertyFrame = (PropertyFrame)propertyNode.getParent().getData();				
				retVal.addAll(parentPropertyFrame.getDomains());
				
			}
			// recurse
			retVal.addAll(getParentDomains(propertyNode.getParent()));
		}
		return retVal;
	}
	
	
	
	// Store user-selected restrictions to the classes, dataproperties, and object properties
	// trees (which are references to the trees held in the parent specialization wizard).
	public void persistRestrictions() {
		Record[] records = cbSelectionModel.getSelections();
		for (Record record: records) {
			String restrictionKey = record.getAsString("restrictionId");
			Restriction restriction = restrictionsMap.get(restrictionKey);
			if (restriction != null) {
				
				// persist rdfs domain restrictions
				if (restriction instanceof DomainRestriction) {
					PropertyFrame propertyFrame = ((DomainRestriction) restriction).getProperty();
					ClassFrame domainFrame = ((DomainRestriction) restriction).getDomain();
					LabelOrIri domain;
					if (domainFrame.getIri().isPresent()) {
						domain = new LabelOrIri(domainFrame.getIri().get());
					}
					else {
						domain = new LabelOrIri(domainFrame.getLabel());
					}
					propertyFrame.getDomains().add(domain);
				}
				
				// persist rdfs object property range restrictions 
				else if (restriction instanceof ObjectPropertyRangeRestriction) {
					ObjectPropertyFrame propertyFrame = ((ObjectPropertyRangeRestriction) restriction).getProperty();
					ClassFrame rangeFrame = ((ObjectPropertyRangeRestriction) restriction).getRange();
					LabelOrIri range;
					if (rangeFrame.getIri().isPresent()) {
						range = new LabelOrIri(rangeFrame.getIri().get());
					}
					else {
						range = new LabelOrIri(rangeFrame.getLabel());
					}
					propertyFrame.getRanges().add(range);
				}
				
				// TODO: Support persistence of class-oriented restrictions!
			}
		}
	}
	
	// Reset any restrictions or other modifications on entities
	// to the state from last screen, e.g., entity specialization panel
	public void resetRestrictions() {
		// TODO: Remove class-oriented restrictions
		/*for (OntologyEntityFrame classFrame: getFramesAsSet(classes,true)) {
			
		}*/
		for (OntologyEntityFrame dataPropertyFrame: getFramesAsSet(dataProperties, true)) {
			if (dataPropertyFrame instanceof DataPropertyFrame) {
				((DataPropertyFrame) dataPropertyFrame).getDomains().clear();
				((DataPropertyFrame) dataPropertyFrame).getRanges().clear();
			}
		}
		for (OntologyEntityFrame objectPropertyFrame: getFramesAsSet(objectProperties, true)) {
			if (objectPropertyFrame instanceof ObjectPropertyFrame) {
				((ObjectPropertyFrame) objectPropertyFrame).getDomains().clear();
				((ObjectPropertyFrame) objectPropertyFrame).getRanges().clear();
			}
		}
	}
}
