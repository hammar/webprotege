package edu.stanford.bmir.protege.web.client.xd.specialization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

import com.google.common.base.Optional;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.EventObject; 
import com.gwtext.client.core.Position;
import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.CardLayout;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.layout.RowLayout;
import com.gwtext.client.widgets.tree.DefaultSelectionModel;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;

import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.dispatch.actions.CreateClassAction;
import edu.stanford.bmir.protege.web.client.dispatch.actions.CreateClassResult;
import edu.stanford.bmir.protege.web.client.dispatch.actions.CreateDataPropertiesAction;
import edu.stanford.bmir.protege.web.client.dispatch.actions.CreateDataPropertiesResult;
import edu.stanford.bmir.protege.web.client.dispatch.actions.CreateObjectPropertiesAction;
import edu.stanford.bmir.protege.web.client.dispatch.actions.CreateObjectPropertiesResult;
import edu.stanford.bmir.protege.web.client.dispatch.actions.UpdateClassFrameAction;
import edu.stanford.bmir.protege.web.client.ui.frame.LabelledFrame;
import edu.stanford.bmir.protege.web.client.xd.XdPatternDetailsPortlet;
import edu.stanford.bmir.protege.web.client.xd.specialization.classdetails.EditClassDetailsWindow;
import edu.stanford.bmir.protege.web.client.xd.specialization.classdetails.NewClassDetailsWindow;
import edu.stanford.bmir.protege.web.client.xd.specialization.propertydetails.EditDatatypePropertyDetailsWindow;
import edu.stanford.bmir.protege.web.client.xd.specialization.propertydetails.EditObjectPropertyDetailsWindow;
import edu.stanford.bmir.protege.web.client.xd.specialization.propertydetails.NewDatatypePropertyDetailsWindow;
import edu.stanford.bmir.protege.web.client.xd.specialization.propertydetails.NewObjectPropertyDetailsWindow;
import edu.stanford.bmir.protege.web.shared.DataFactory;
import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.dispatch.UpdateObjectAction;
import edu.stanford.bmir.protege.web.shared.frame.ClassFrame;
import edu.stanford.bmir.protege.web.shared.frame.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.frame.ObjectPropertyCharacteristic;
import edu.stanford.bmir.protege.web.shared.frame.ObjectPropertyFrame;
import edu.stanford.bmir.protege.web.shared.frame.PropertyAnnotationValue;
import edu.stanford.bmir.protege.web.shared.frame.PropertyValue;
import edu.stanford.bmir.protege.web.shared.frame.PropertyValueList;
import edu.stanford.bmir.protege.web.shared.frame.PropertyValueState;
import edu.stanford.bmir.protege.web.shared.frame.UpdateDataPropertyFrameAction;
import edu.stanford.bmir.protege.web.shared.frame.UpdateObjectPropertyFrameAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetOdpContentsAction;
import edu.stanford.bmir.protege.web.shared.xd.data.EntityMetadata;
import edu.stanford.bmir.protege.web.shared.xd.data.XdTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpContentsResult;

public class XdSpecializationWizard extends com.gwtext.client.widgets.Window {
	
	//private Panel wizardPanel;
	private ToolbarButton backButton;
	private ToolbarButton nextButton;
	private ToolbarButton finishButton;
	private Toolbar navigationBar;
	private ButtonListenerAdapter navigationButtonsListener;
	private CardLayout cardLayout;
	
	// Strategy selection radio buttons
	private RadioButton propStrategyButton;
	private RadioButton classStrategyButton;
	private RadioButton hybridStrategyButton;
	
	// TreePanels for customizing classes and properties
	private TreePanel classTreePanel;
	private TreePanel objectPropertyTreePanel;
	private TreePanel datatypePropertyTreePanel;
	
	// Visualization and text representation fields
	private Panel visualizationPanel;
	private TextArea instantationAxiomsPreview;

	// Entity specialization windows
	private NewClassDetailsWindow ncdw;
	private EditClassDetailsWindow ecdw;
	private NewObjectPropertyDetailsWindow nopdw;
	private EditObjectPropertyDetailsWindow eopdw;
	private NewDatatypePropertyDetailsWindow ndpdw;
	private EditDatatypePropertyDetailsWindow edpdw;
	
	// Progress window when performing instantiation
	//private MessageBoxConfig instantiationProgressConf;
	
	// Reference to project ID required for service calls
	private ProjectId projectId;
	
	// Used to synchronize GWT-RPC-calls so that frame updates don't occur prior to entity creation
	private Integer requiredCreationServiceCalls;
	private Integer requiredUpdateServiceCalls;
	private Integer completedCreationServiceCalls;
	private Integer completedUpdateServiceCalls;
	
	// Keeps track of created entities from the first half of instantiation
	private Map<String,OWLClass> createdClasses;
	private Map<String, OWLObjectProperty> createdObjectProperties;
	private Map<String, OWLDataProperty> createdDataProperties;
	
	@SuppressWarnings("deprecation")
	public XdSpecializationWizard(XdPatternDetailsPortlet parent) {
		this.projectId = parent.getProjectId();
		
		this.ncdw = new NewClassDetailsWindow(this);
		this.ecdw = new EditClassDetailsWindow(this);
		this.nopdw = new NewObjectPropertyDetailsWindow(this);
		this.eopdw = new EditObjectPropertyDetailsWindow(this);
		this.ndpdw = new NewDatatypePropertyDetailsWindow(this);
		this.edpdw = new EditDatatypePropertyDetailsWindow(this);
		
		this.createdClasses = new HashMap<String,OWLClass>();
		this.createdObjectProperties = new HashMap<String,OWLObjectProperty>();
		this.createdDataProperties = new HashMap<String,OWLDataProperty>();
		
		this.requiredCreationServiceCalls = 0;
		this.requiredUpdateServiceCalls = 0;
		this.completedCreationServiceCalls = 0;
		this.completedUpdateServiceCalls = 0;
		
		this.setTitle("ODP Specialisation Wizard");
		this.setWidth(640);
		this.setHeight(480);
		this.setModal(true);
		this.setResizable(false);
		 
		cardLayout = new CardLayout();
		this.setLayout(cardLayout);
		this.setActiveItem(0);
        
        // This is the toolbar at the bottom of the wizard that handles back/forward navigation
        // through the wizard interface
        navigationBar = new Toolbar();  
        navigationButtonsListener = makeNavigationButtonsListenerAdapter();
        
        backButton = new ToolbarButton("Back", navigationButtonsListener);  
        backButton.setId("move-prev");  
        navigationBar.addButton(backButton);  
        navigationBar.addFill();  
  
        nextButton = new ToolbarButton("Next", navigationButtonsListener);  
        nextButton.setId("move-next");  
        navigationBar.addButton(nextButton);
        
        finishButton = new ToolbarButton("Finish", new ButtonListenerAdapter(){
        	public void onClick(Button button, EventObject e) {
        		saveAndClose();
        	}
        });  
        finishButton.setId("move-finish");  
        navigationBar.addButton(finishButton);
        finishButton.setVisible(false);
        this.setBottomToolbar(navigationBar);  

        
        // These are the individual cards/screens of the wizard interface.
        this.add(makeFirstCard());  
        this.add(makeSecondCard());
        this.add(makeThirdCard()); 
        
        // Configure the progress window shown when finishing
        /*instantiationProgressConf = new MessageBoxConfig() {  
            {  
                setTitle("Instantiating ODP...");  
                setMsg("Creating entities...");  
                setWidth(240);  
                setProgress(true);  
                setClosable(false);  
            }  
        };*/
        
        // TODO: Make class/property windows modal
        
	}
	
	/**
	 * Returns the currently selected class node in class hierarchy tree widget.
	 * @return A TreeNode that is currently selected.
	 */
	public TreeNode getSelectedClass() {
		DefaultSelectionModel dsm = (DefaultSelectionModel)classTreePanel.getSelectionModel();
		return dsm.getSelectedNode();
	}
	
	/**
	 * This method stores the customized ODP instantiation into the ontology on the server side
	 * and then closes down the wizard. 
	 */
	private void saveAndClose() {		
		// 1. Calculate the total number of async service calls required
		requiredCreationServiceCalls = calculateRequiredServiceCalls();
		requiredUpdateServiceCalls = calculateRequiredServiceCalls();
		
		// 2. Create class, object property, and datatype property hierarchies on server
		// Note that each of these methods on the leaf level execute proceedIfAllEntitiesCreated()
		// which will not proceed until all entities have been created.
		createChildClasses(classTreePanel.getRootNode());
		createChildObjectProperties(objectPropertyTreePanel.getRootNode());
		createChildDatatypeProperties(datatypePropertyTreePanel.getRootNode());
		
		// TODO: Implement messagebox with pgogress bar (this does not currently work).
		//MessageBox.show(instantiationProgressConf);
		//this.hide();
	}
	
	/**
	 * Gets the number of child elements (e.g., classes or properties to create) in
	 * the class, object property, and datatype property trees. That is to say, how
	 * many GWT-RPC calls will be required to create this structure in the ontology.
	 * Note the -3: this is because we don't want to include the three root nodes.
	 * Note also that we don't account for classes that may already exist in the
	 * ontology - this is future work to look for/fix.
	 * @return Number of service 
	 */
	private Integer calculateRequiredServiceCalls() {
		return getInclusiveChildNodeCount(classTreePanel.getRootNode()) +
				getInclusiveChildNodeCount(objectPropertyTreePanel.getRootNode()) + 
				getInclusiveChildNodeCount(datatypePropertyTreePanel.getRootNode()) - 3;
	}

	/**
	 * Recursive method to calculate the total number of child nodes (including the
	 * starting node) of a given subtree.
	 * @param parentNode - the starting node
	 * @return number of child nodes including starting node
	 */
	private int getInclusiveChildNodeCount(TreeNode parentNode) {
		Integer i = 0;
		for (final Node childNode: parentNode.getChildNodes()) {
			i += getInclusiveChildNodeCount((TreeNode)childNode);
		}
		return 1 + i;
	}

	/**
	 *  Gate-keeper method that only proceeds with updating frames (e.g., annotations, domains and ranges)
	 *  of created OWL entities if all of those entities have been created successfully (e.g., if every estimated
	 *  service call has been successfully executed).
	 *  Thus we force GWT to act synchronously, which is perhaps not a very nice solution architecturally - but is
	 *  required if we want to reuse existing WebProtégé dispatch APIs rather than create new ones.
	 */
	private void proceedIfAllEntitiesCreated() {
		if (requiredCreationServiceCalls == completedCreationServiceCalls) {
			// If the above is true, then all classes, object properties, and datatype properties in the respective
			// trees have been created successfully using GWT-RPC calls, and we can now proceed to update their
			// frames.
			//MessageBox.updateText("Updating entity frames...");
			updateChildClassFrames(classTreePanel.getRootNode());
			updateChildObjectPropertyFrames(objectPropertyTreePanel.getRootNode());
			updateChildDatatypePropertyFrames(datatypePropertyTreePanel.getRootNode());
		} 
	}
	
	/**
	 * Gate-keeper method that only closes down specialization wizard once all frame update service
	 * calls have returned succesfully.
	 */
	private void proceedIfAllEntitiesUpdated() {
		if (requiredUpdateServiceCalls == completedUpdateServiceCalls) {
			this.hide();
		}
	}
	
	/**
	 * Recursive method that via GWT-RPC calls creates OWL subclasses to match the subtree
	 * of which the input starting node is the parent.
	 * @param parentNode - Starting node.
	 */
	private void createChildClasses(TreeNode parentNode) {
		OWLClass parentClass = (OWLClass)parentNode.getAttributeAsObject("owlClass");
		final Node[] childNodes = parentNode.getChildNodes();
		
		for (final Node childNode: childNodes) {
			final TreeNode childTreeNode = (TreeNode)childNode;
			CreateClassAction cca = new CreateClassAction(projectId,childTreeNode.getText(),parentClass);
			
			DispatchServiceManager.get().execute(cca, new DispatchServiceCallback<CreateClassResult>() {
		        public void handleSuccess(final CreateClassResult result) {
		        	OWLClass resultingClass = result.getObject();
		        	completedCreationServiceCalls += 1;
		        	createdClasses.put(result.getBrowserText(resultingClass).get(), resultingClass);
		        	childTreeNode.setAttribute("owlClass", resultingClass);
		        	//updateProgressBarAndClose();
		        	createChildClasses(childTreeNode);
		        	proceedIfAllEntitiesCreated();
		        }
			});
		}
	}
	
	/**
	 * This method updates the progress bar and is called by all returning GWT-RPC handlers.
	 * If we find that all service calls are finished executing, we hide the progress bar and 
	 * the specialization wizard window.
	 */
	/*
	private void updateProgressBarAndClose() {
		Integer percentage = (100*completedCreationServiceCalls)/(100*requiredCreationServiceCalls);
    	MessageBox.updateProgress(percentage);
    	if (completedUpdateServiceCalls == requiredUpdateServiceCalls)
    	{
    		MessageBox.hide();
    	}
	}*/
	
	private void createChildObjectProperties(TreeNode parentNode) {
		OWLObjectProperty parentProperty = (OWLObjectProperty)parentNode.getAttributeAsObject("owlObjectProperty");
		final Node[] childNodes = parentNode.getChildNodes();
		
		for (final Node childNode: childNodes) {
			final TreeNode childTreeNode = (TreeNode)childNode;
			String objectPropertyName = childTreeNode.getText();
			Set<String> objectPropertyNames = new HashSet<String>(Arrays.asList(objectPropertyName));
			CreateObjectPropertiesAction copa = new CreateObjectPropertiesAction(projectId, objectPropertyNames, Optional.of(parentProperty));
			
			DispatchServiceManager.get().execute(copa, new DispatchServiceCallback<CreateObjectPropertiesResult>() {
		        public void handleSuccess(final CreateObjectPropertiesResult result) {
		        	completedCreationServiceCalls += 1;
		        	OWLObjectProperty resultingProperty = result.getEntities().iterator().next();
		        	createdObjectProperties.put(result.getBrowserText(resultingProperty).get(), resultingProperty);
		        	childTreeNode.setAttribute("owlObjectProperty", resultingProperty);
		        	//updateProgressBarAndClose();
		        	createChildObjectProperties(childTreeNode);
		        	proceedIfAllEntitiesCreated();
		        }
			});
		}
	}
	
	private void createChildDatatypeProperties(TreeNode parentNode) {
		OWLDataProperty parentProperty = (OWLDataProperty)parentNode.getAttributeAsObject("owlDataProperty");
		final Node[] childNodes = parentNode.getChildNodes();
		
		for (final Node childNode: childNodes) {
			final TreeNode childTreeNode = (TreeNode)childNode;
			String dataPropertyName = childTreeNode.getText();
			Set<String> dataPropertyNames = new HashSet<String>(Arrays.asList(dataPropertyName));
			CreateDataPropertiesAction cdpa = new CreateDataPropertiesAction(projectId, dataPropertyNames, Optional.of(parentProperty));
			
			DispatchServiceManager.get().execute(cdpa, new DispatchServiceCallback<CreateDataPropertiesResult>() {
		        public void handleSuccess(final CreateDataPropertiesResult result) {
		        	completedCreationServiceCalls += 1;
		        	OWLDataProperty resultingProperty = result.getEntities().iterator().next();
		        	String resultingLabel = result.getBrowserText(resultingProperty).get();
		        	createdDataProperties.put(resultingLabel, resultingProperty);
		        	childTreeNode.setAttribute("owlDataProperty", resultingProperty);
		        	//updateProgressBarAndClose();
		        	createChildDatatypeProperties(childTreeNode);
		        	proceedIfAllEntitiesCreated();
		        }
			});
		}
	}
	
	private void updateChildClassFrames(TreeNode parentNode) {
		final Node[] childNodes = parentNode.getChildNodes();
		
		for (final Node childNode: childNodes) {
			final TreeNode childTreeNode = (TreeNode)childNode;
			OWLClass childClass = (OWLClass)childTreeNode.getAttributeAsObject("owlClass");
			
			// Create (empty) source class frame
			LabelledFrame<ClassFrame> fromFrame = new LabelledFrame<ClassFrame>(childTreeNode.getText(),new ClassFrame(childClass));
			
			// Extract property values that we support from node
			Set<PropertyValue> propertyValues = new HashSet<PropertyValue>();
			
			if (childTreeNode.getAttribute("rdfsComment") != null) {
				OWLAnnotationProperty rdfsCommentProperty = DataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());
				OWLLiteral rdfsCommentValue = DataFactory.getOWLLiteral(childTreeNode.getAttribute("rdfsComment"));
				PropertyAnnotationValue rdfsCommentPropertyValue = new PropertyAnnotationValue(rdfsCommentProperty,rdfsCommentValue, PropertyValueState.ASSERTED);
				propertyValues.add(rdfsCommentPropertyValue);
			}
			
			// Create target (after update) frame for class
			// Note that the Set<OWLClass> parameter which used to represent superclasses of 
			// the subject class is actually not used on the server-side at the moment
			// but is still required for the ClassFrame constructor.
			LabelledFrame<ClassFrame> toFrame = new LabelledFrame<ClassFrame>(childTreeNode.getText(),
					new ClassFrame(childClass, new HashSet<OWLClass>(), propertyValues));;
			
			// Create and execute update action against dispatch service
			UpdateObjectAction<LabelledFrame<ClassFrame>> uoa = new UpdateClassFrameAction(projectId,fromFrame,toFrame);
			DispatchServiceManager.get().execute(uoa, new DispatchServiceCallback<Result>() {
		        public void handleSuccess(final Result result) {
		        	completedUpdateServiceCalls += 1;
		        	updateChildClassFrames(childTreeNode);
		        	proceedIfAllEntitiesUpdated();
		        }
			});
		}
	}
	
	private void updateChildObjectPropertyFrames(TreeNode parentNode) {
		final Node[] childNodes = parentNode.getChildNodes();
		
		for (final Node childNode: childNodes) {
			final TreeNode childTreeNode = (TreeNode)childNode;
			OWLObjectProperty childProperty = (OWLObjectProperty)childTreeNode.getAttributeAsObject("owlObjectProperty");
			
			// Create (empty) source object property frame
			LabelledFrame<ObjectPropertyFrame> fromFrame = new LabelledFrame<ObjectPropertyFrame>(childTreeNode.getText(),new ObjectPropertyFrame.Builder(childProperty).build());
			
			// Extract property values that we support from node
			Set<PropertyAnnotationValue> propertyValues = new HashSet<PropertyAnnotationValue>();
			
			// Extract rdfs:comment
			if (childTreeNode.getAttribute("rdfsComment") != null) {
				OWLAnnotationProperty rdfsCommentProperty = DataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());
				OWLLiteral rdfsCommentValue = DataFactory.getOWLLiteral(childTreeNode.getAttribute("rdfsComment"));
				PropertyAnnotationValue rdfsCommentPropertyValue = new PropertyAnnotationValue(rdfsCommentProperty,rdfsCommentValue, PropertyValueState.ASSERTED);
				propertyValues.add(rdfsCommentPropertyValue);
			}
			
			// Extract rdfs:domain
			Set<OWLClass> domains = new HashSet<OWLClass>();
			if (childTreeNode.getAttribute("rdfsDomain") != null) {
				String domainLabel = childTreeNode.getAttribute("rdfsDomain");
				OWLClass domainClass = createdClasses.get(domainLabel);
				domains.add(domainClass);
			}
			
			// Extract rdfs:range
			Set<OWLClass> ranges = new HashSet<OWLClass>();
			if (childTreeNode.getAttribute("rdfsRange") != null) {
				String rangeLabel = childTreeNode.getAttribute("rdfsRange");
				OWLClass rangeClass = createdClasses.get(rangeLabel);
				ranges.add(rangeClass);
			}
			
			// Extract owl:inverseOf
			// TODO: this presently does not work on server-side :(
			Set<OWLObjectProperty> inverses = new HashSet<OWLObjectProperty>();
			if (childTreeNode.getAttribute("owlInverseOf") != null) {
				String inverseLabel = childTreeNode.getAttribute("owlInverseOf");
				OWLObjectProperty inverseProperty = createdObjectProperties.get(inverseLabel);
				inverses.add(inverseProperty);
			}
			
			// Extract some more property characteristics
			// TODO: Support more characteristics (reflexive, asymmetric, etc)
			Set<ObjectPropertyCharacteristic> characteristics = new HashSet<ObjectPropertyCharacteristic>();
			if (childTreeNode.getAttribute("owlTransitiveProperty") != null) {
				characteristics.add(ObjectPropertyCharacteristic.TRANSITIVE);
			}
			if (childTreeNode.getAttribute("owlSymmetricProperty") != null) {
				characteristics.add(ObjectPropertyCharacteristic.SYMMETRIC);
			}
			if (childTreeNode.getAttribute("owlFunctionalProperty") != null) {
				characteristics.add(ObjectPropertyCharacteristic.FUNCTIONAL);
			}
			
			// Create target (after update) frame for class
			// Note that the Set<OWLClass> parameter which used to represent superclasses of 
			// the subject class is actually not used on the server-side at the moment
			// but is still required for the ClassFrame constructor.
			LabelledFrame<ObjectPropertyFrame> toFrame = new LabelledFrame<ObjectPropertyFrame>(childTreeNode.getText(),
					new ObjectPropertyFrame(childProperty, propertyValues, domains, ranges, inverses, characteristics));
			
			// Create and execute update action against dispatch service
			UpdateObjectAction<LabelledFrame<ObjectPropertyFrame>> uoa = new UpdateObjectPropertyFrameAction(projectId,fromFrame,toFrame);
			DispatchServiceManager.get().execute(uoa, new DispatchServiceCallback<Result>() {
		        public void handleSuccess(final Result result) {
		        	completedUpdateServiceCalls += 1;
		        	updateChildObjectPropertyFrames(childTreeNode);
		        	proceedIfAllEntitiesUpdated();
		        }
			});
		}
	}
	
	private void updateChildDatatypePropertyFrames(TreeNode parentNode) {
		final Node[] childNodes = parentNode.getChildNodes();
		
		for (final Node childNode: childNodes) {
			final TreeNode childTreeNode = (TreeNode)childNode;
			OWLDataProperty childProperty = (OWLDataProperty)childTreeNode.getAttributeAsObject("owlDataProperty");
			
			// Create (empty) source object property frame
			DataPropertyFrame fromFrame = new DataPropertyFrame(childProperty,new PropertyValueList(new ArrayList<PropertyValue>()),new HashSet<OWLClass>(),new HashSet<OWLDatatype>(),false);
			LabelledFrame<DataPropertyFrame> fromLabelledFrame = new LabelledFrame<DataPropertyFrame>(childTreeNode.getText(),fromFrame);
			
			// Extract property values that we support from node
			Set<PropertyAnnotationValue> propertyValues = new HashSet<PropertyAnnotationValue>();
			
			// Extract rdfs:comment
			if (childTreeNode.getAttribute("rdfsComment") != null) {
				OWLAnnotationProperty rdfsCommentProperty = DataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI());
				OWLLiteral rdfsCommentValue = DataFactory.getOWLLiteral(childTreeNode.getAttribute("rdfsComment"));
				PropertyAnnotationValue rdfsCommentPropertyValue = new PropertyAnnotationValue(rdfsCommentProperty,rdfsCommentValue, PropertyValueState.ASSERTED);
				propertyValues.add(rdfsCommentPropertyValue);
			}
			
			// Extract rdfs:domain
			Set<OWLClass> domains = new HashSet<OWLClass>();
			if (childTreeNode.getAttribute("rdfsDomain") != null) {
				String domainLabel = childTreeNode.getAttribute("rdfsDomain");
				OWLClass domainClass = createdClasses.get(domainLabel);
				domains.add(domainClass);
			}
			
			// Extract rdfs:range
			Set<OWLDatatype> ranges = new HashSet<OWLDatatype>();
			if (childTreeNode.getAttribute("rdfsRange") != null) {
				String rangeLabel = childTreeNode.getAttribute("rdfsRange");
				IRI rangeIri = DataFactory.getIRI(rangeLabel);
				OWLDatatype range = DataFactory.getOWLDatatype(rangeIri);
				ranges.add(range);
			}
			
			// Extract some more property characteristics
			Boolean owlFunctional = false;
			if (childTreeNode.getAttribute("owlFunctionalProperty") != null) {
				owlFunctional = true;
			}
			
			// Create target (after update) frame for class
			// Note that the Set<OWLClass> parameter which used to represent superclasses of 
			// the subject class is actually not used on the server-side at the moment
			// but is still required for the ClassFrame constructor.
			LabelledFrame<DataPropertyFrame> toFrame = new LabelledFrame<DataPropertyFrame>(childTreeNode.getText(),
					new DataPropertyFrame(childProperty, new PropertyValueList(new ArrayList<PropertyValue>(propertyValues)), domains, ranges, owlFunctional));
			
			// Create and execute update action against dispatch service
			UpdateObjectAction<LabelledFrame<DataPropertyFrame>> uoa = new UpdateDataPropertyFrameAction(projectId,fromLabelledFrame,toFrame);
			DispatchServiceManager.get().execute(uoa, new DispatchServiceCallback<Result>() {
		        public void handleSuccess(final Result result) {
		        	completedUpdateServiceCalls += 1;
		        	updateChildDatatypePropertyFrames(childTreeNode);
		        	proceedIfAllEntitiesUpdated();
		        }
			});
		}
	}
	
	// This is where we clear out old data, load new data required 
	// to run the wizard, prepare fields, etc etc
	public void loadOdp(String uri) {
		
		// Clear out class tree panel
		TreeNode rootClassNode = classTreePanel.getRootNode();
		for (Node child: rootClassNode.getChildNodes()) {
			rootClassNode.removeChild(child);
		}
		
		// Clear out object property tree panel
		TreeNode rootObjectPropertyNode = objectPropertyTreePanel.getRootNode();
		for (Node child: rootObjectPropertyNode.getChildNodes()) {
			rootObjectPropertyNode.removeChild(child);
		}
		
		// Clear out object property tree panel
		TreeNode rootDataPropertyNode = datatypePropertyTreePanel.getRootNode();
		for (Node child: rootDataPropertyNode.getChildNodes()) {
			rootDataPropertyNode.removeChild(child);
		}
		
		// TODO: clear out other fields
		
		// Get ODP implementation from server
        DispatchServiceManager.get().execute(new GetOdpContentsAction(uri), new DispatchServiceCallback<GetOdpContentsResult>() {
        	@Override
            public void handleSuccess(GetOdpContentsResult result) {
        		
        		// Render classes
        		XdTreeNode<EntityMetadata> classes = result.getClasses();
        		TreeNode rootClassNode = classTreePanel.getRootNode();
        		for (XdTreeNode<EntityMetadata> subClassFromServer: classes.getChildren()) {
        			recursivelyAddNode(subClassFromServer, rootClassNode);
        		}
        		classTreePanel.expandAll();
                
        		// Render object properties
        		XdTreeNode<EntityMetadata> objectProperties = result.getObjectProperties();
        		TreeNode rootObjectPropertyNode = objectPropertyTreePanel.getRootNode();
        		for (XdTreeNode<EntityMetadata> subPropertyFromServer: objectProperties.getChildren()) {
        			recursivelyAddNode(subPropertyFromServer, rootObjectPropertyNode);
        		}
        		objectPropertyTreePanel.expandAll();
        		
                // Render Object Properties
        		XdTreeNode<EntityMetadata> dataProperties = result.getDataProperties();
        		TreeNode rootDataPropertyNode = datatypePropertyTreePanel.getRootNode();
        		for (XdTreeNode<EntityMetadata> subPropertyFromServer: dataProperties.getChildren()) {
        			recursivelyAddNode(subPropertyFromServer, rootDataPropertyNode);
        		}
        		objectPropertyTreePanel.expandAll();
        		
                // TODO: Render Datatype properties
            }
        });
	}
	
	private TreeNode makeTreeNode(EntityMetadata em) {
		TreeNode tn = new TreeNode(em.getLabel());
		
		// Iterate over all the keys/values in the metadata map, and assign them as attributes on the TreeNode
		Map<String,String> metadata = em.getMetadata();
		Iterator<Entry<String,String>> it = metadata.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String,String> pair = (Map.Entry<String,String>)it.next();
			tn.setAttribute(pair.getKey(), pair.getValue());
		}
		
		return tn;
	}
	
	private void recursivelyAddNode(XdTreeNode<EntityMetadata> nodeFromServer, TreeNode localParentNode) {
		TreeNode newLocalChild = makeTreeNode(nodeFromServer.getData());
		localParentNode.appendChild(newLocalChild);
		for (XdTreeNode<EntityMetadata> nextChildFromServer: nodeFromServer.getChildren()) {
			//Window.alert("Second level recursion over " + nextChildFromServer.getData().getLabel());
			recursivelyAddNode(nextChildFromServer, newLocalChild);
		}
	}
	
	private ButtonListenerAdapter makeNavigationButtonsListenerAdapter() {
		return new ButtonListenerAdapter() {  
            public void onClick(Button button, EventObject e) {  
                String btnID = button.getId();    
                String panelID = cardLayout.getActiveItem().getId();  
  
                if (btnID.equals("move-prev")) {
                    if (panelID.equals("card-2")) {
                    	// From last to middle card
                        cardLayout.setActiveItem(1);
                        finishButton.setVisible(false);
                        nextButton.setVisible(true);
                    }
                    else {
                    	// From middle to first card
                        cardLayout.setActiveItem(0);
                    }  
                } 
                else {  
                    if (panelID.equals("card-0")) {
                    	// From first to second card
                        cardLayout.setActiveItem(1);
                        // TODO: Clear out old suggestions from UI
                        // TODO: Call service back-end to get suggested specialization axioms
                        // TODO: Populate UI with suggestions
                    }
                    else {
                    	// From second to third card
                        cardLayout.setActiveItem(2);
                        finishButton.setVisible(true);
                        nextButton.setVisible(false);
                        // TODO: Clear out old rendering from UI
                        // TODO: Render specialization graphically
                    }  
                }
            }
        };  
	}
	
	private Panel makeFirstCard() {
        Panel first = new Panel();  
        first.setBorder(false);
        first.setId("card-0");
        first.setTitle("Specialisation Strategy");
        
        propStrategyButton = new RadioButton("strategyGroup", "Property-oriented strategy");
        classStrategyButton = new RadioButton("strategyGroup", "Class-oriented strategy");
        hybridStrategyButton = new RadioButton("strategyGroup", "Hybrid strategy");

       // Add toggle button to the root panel.
       VerticalPanel strategyPanel = new VerticalPanel();
       strategyPanel.setSpacing(10);
       strategyPanel.add(new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean in rhoncus sapien. Phasellus vitae magna at enim molestie auctor et at est. Nullam consequat odio eu varius venenatis. Etiam aliquet ligula ac posuere lacinia. Curabitur aliquet elit a viverra fringilla. "));
       strategyPanel.add(propStrategyButton);
       strategyPanel.add(new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean in rhoncus sapien. Phasellus vitae magna at enim molestie auctor et at est. Nullam consequat odio eu varius venenatis. Etiam aliquet ligula ac posuere lacinia. Curabitur aliquet elit a viverra fringilla. "));
       strategyPanel.add(classStrategyButton);
       strategyPanel.add(new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean in rhoncus sapien. Phasellus vitae magna at enim molestie auctor et at est. Nullam consequat odio eu varius venenatis. Etiam aliquet ligula ac posuere lacinia. Curabitur aliquet elit a viverra fringilla. "));
       strategyPanel.add(hybridStrategyButton);
       first.add(strategyPanel);
       return first;
	}
	
	private Panel makeSecondCard() {
        Panel second = new Panel();
        second.setBorder(false);  
        second.setId("card-1");
        second.setTitle("Specialise Classes and Properties");
        second.add(new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean in rhoncus sapien. Phasellus vitae magna at enim molestie auctor et at est. Nullam consequat odio eu varius venenatis. Etiam aliquet ligula ac posuere lacinia. Curabitur aliquet elit a viverra fringilla. "));
        
        TabPanel tabPanel = new TabPanel();
        tabPanel.setTabPosition(Position.TOP);  
        tabPanel.setResizeTabs(true);  
        tabPanel.setMinTabWidth(115);
        tabPanel.setTabWidth(135);
        tabPanel.setActiveTab(0);
        
        // The tab where we set classes
        Panel classSpecialisationPanel = new Panel();  
        classSpecialisationPanel.setTitle("Classes");
        classSpecialisationPanel.setLayout(new ColumnLayout());
        // Class tree panel
        classTreePanel = new TreePanel();
        classTreePanel.setRootVisible(false);
        final TreeNode rootClassNode = new TreeNode();
        rootClassNode.setAttribute("owlClass", DataFactory.getOWLThing());
        TreeNode odpClassNode1 = new TreeNode("ODP Class 1");
        odpClassNode1.setAttribute("rdfsComment", "This is an ODP test class 1.");
        //odpClassNode1.appendChild(new TreeNode("Custom subclass 1"));
        rootClassNode.appendChild(odpClassNode1);
        
        TreeNode odpClassNode2 = new TreeNode("ODP Class 2");
        odpClassNode2.setAttribute("rdfsComment", "This is an ODP test class 2.");
        //odpClassNode2.appendChild(new TreeNode("Custom subclass 2a"));
        //TreeNode subClass2b = new TreeNode("Custom subclass 2b");
        //subClass2b.setAttribute("rdfsComment", "This is custom subclass 2b.");
        //odpClassNode2.appendChild(subClass2b);
        rootClassNode.appendChild(odpClassNode2);
        
        TreeNode odpClassNode3 = new TreeNode("ODP Class 3");
        //odpClassNode3.appendChild(new TreeNode("Custom subclass 3a"));
        //odpClassNode3.appendChild(new TreeNode("Custom subclass 3b"));
        //odpClassNode3.appendChild(new TreeNode("Custom subclass 3c"));
        rootClassNode.appendChild(odpClassNode3);
        classTreePanel.setRootNode(rootClassNode);
        classTreePanel.expandAll();
        classSpecialisationPanel.add(classTreePanel, new ColumnLayoutData(.9));;
        // Controls
        Panel classModificationControls = new Panel();
        Button addClassButton = new Button("Add new subclass");
        addClassButton.addListener(new ButtonListenerAdapter() {
        	@Override
        	public void onClick(final Button button, final EventObject e) {
        		ncdw.reset();
        		ncdw.show();
        	}
        });
        classModificationControls.add(addClassButton);
        Button removeClassButton = new Button("Remove");
        removeClassButton.setDisabled(true);
        classModificationControls.add(removeClassButton);
        Button modifyClassButton = new Button("Modify class");
        modifyClassButton.addListener(new ButtonListenerAdapter() {
        	@Override
        	public void onClick(final Button button, final EventObject e) {
        		ecdw.reset();
        		ecdw.loadClass();
        		ecdw.show();
        	}
        });
        classModificationControls.add(modifyClassButton);
        
        classSpecialisationPanel.add(classModificationControls, new ColumnLayoutData(.1));
        
        // The tab where we set object properties
        Panel objPropertySpecialisationPanel = new Panel();  
        objPropertySpecialisationPanel.setTitle("Obj properties");  
        objPropertySpecialisationPanel.setLayout(new ColumnLayout());
        // Tree panel
        objectPropertyTreePanel = new TreePanel();
        objectPropertyTreePanel.setRootVisible(false);
        final TreeNode rootObjectPropertyNode = new TreeNode("OWL Top Object Property");
        rootObjectPropertyNode.setAttribute("owlObjectProperty", DataFactory.getOWLObjectProperty(OWLRDFVocabulary.OWL_TOP_OBJECT_PROPERTY.getIRI()));
        
        final TreeNode firstSubProperty = new TreeNode("ODP Object Property 1");
        //firstSubProperty.setAttribute("rdfsDomain", "Custom subclass 3c");
        //firstSubProperty.setAttribute("rdfsRange", "Custom subclass 2b");
        //firstSubProperty.setAttribute("owlInverseOf", "A third subproperty");
        firstSubProperty.setAttribute("owlFunctionalProperty", true);
        rootObjectPropertyNode.appendChild(firstSubProperty);
        rootObjectPropertyNode.appendChild(new TreeNode("ODP Object Property 2"));
        final TreeNode thirdSubObjectProperty = new TreeNode("ODP Object Property 3");
        thirdSubObjectProperty.setAttribute("owlSymmetricProperty", true);
        thirdSubObjectProperty.setAttribute("owlTransitiveProperty", true);
        rootObjectPropertyNode.appendChild(thirdSubObjectProperty);
        
        objectPropertyTreePanel.setRootNode(rootObjectPropertyNode);
        objectPropertyTreePanel.expandAll();
        objPropertySpecialisationPanel.add(objectPropertyTreePanel, new ColumnLayoutData(.9));
        // Controls
        Panel objectPropertyModificationControls = new Panel();
        Button addObjectPropertyButton = new Button("Add");
        addObjectPropertyButton.addListener(new ButtonListenerAdapter() {
        	@Override
        	public void onClick(final Button button, final EventObject e) {
        		nopdw.reset();
        		nopdw.show();
        	}
        });
        objectPropertyModificationControls.add(addObjectPropertyButton);
        
        Button opRemoveButton = new Button("Remove");
        opRemoveButton.setDisabled(true);
        objectPropertyModificationControls.add(opRemoveButton);
        
        Button editObjectPropertyButton = new Button("Modify");
        editObjectPropertyButton.addListener(new ButtonListenerAdapter() {
        	@Override
        	public void onClick(final Button button, final EventObject e) {
        		eopdw.reset();
        		eopdw.loadProperty();
        		eopdw.show();
        	}
        });
        objectPropertyModificationControls.add(editObjectPropertyButton);
        
        
        objPropertySpecialisationPanel.add(objectPropertyModificationControls, new ColumnLayoutData(.1));
        
        // The tab where we set datatype properties
        Panel dataPropertySpecialisationPanel = new Panel();  
        dataPropertySpecialisationPanel.setTitle("Data properties");
        dataPropertySpecialisationPanel.setLayout(new ColumnLayout());
        // Tree panel
        datatypePropertyTreePanel = new TreePanel();
        datatypePropertyTreePanel.setRootVisible(false);
        final TreeNode rootDatatypePropertyNode = new TreeNode("OWL Top Data Property");
        rootDatatypePropertyNode.setAttribute("owlDataProperty", DataFactory.getOWLDataProperty(OWLRDFVocabulary.OWL_TOP_DATA_PROPERTY.getIRI()));
        rootDatatypePropertyNode.appendChild(new TreeNode("ODP Data Property 1"));
        final TreeNode secondChildDataPropertyNode = new TreeNode("ODP Data Property 2");
        secondChildDataPropertyNode.setAttribute("rdfsDomain", "ODP Class 2");
        secondChildDataPropertyNode.setAttribute("owlFunctionalProperty", true);
        secondChildDataPropertyNode.setAttribute("rdfsRange", XSDVocabulary.DATE_TIME.toString());
        rootDatatypePropertyNode.appendChild(secondChildDataPropertyNode);
        
        final TreeNode thirdChildDataPropertyNode = new TreeNode("ODP Data Property 3");
        thirdChildDataPropertyNode.setAttribute("rdfsComment", "This is a comment string on a datatype property!");
        rootDatatypePropertyNode.appendChild(thirdChildDataPropertyNode);
        datatypePropertyTreePanel.setRootNode(rootDatatypePropertyNode);
        datatypePropertyTreePanel.expandAll();
        dataPropertySpecialisationPanel.add(datatypePropertyTreePanel, new ColumnLayoutData(.9));
        // Controls
        Panel datatypePropertyModificationControls = new Panel();
        Button addDatatypePropertyButton = new Button("Add");
        addDatatypePropertyButton.addListener(new ButtonListenerAdapter() {
        	@Override
        	public void onClick(final Button button, final EventObject e) {
        		ndpdw.reset();
        		ndpdw.show();
        	}
        });
        datatypePropertyModificationControls.add(addDatatypePropertyButton);
        Button dpRemoveButton = new Button("Remove");
        dpRemoveButton.setDisabled(true);
        datatypePropertyModificationControls.add(dpRemoveButton);
        
        Button editDatatypePropertyButton = new Button("Modify");
        editDatatypePropertyButton.addListener(new ButtonListenerAdapter() {
        	@Override
        	public void onClick(final Button button, final EventObject e) {
        		edpdw.reset();
        		edpdw.loadProperty();
        		edpdw.show();
        	}
        });
        datatypePropertyModificationControls.add(editDatatypePropertyButton);
        dataPropertySpecialisationPanel.add(datatypePropertyModificationControls, new ColumnLayoutData(.1));
        
        
        tabPanel.add(classSpecialisationPanel);
        tabPanel.add(objPropertySpecialisationPanel);
        tabPanel.add(dataPropertySpecialisationPanel);
        second.add(tabPanel);
        
        return second;
	}
	
	private Panel makeThirdCard() {
        Panel third = new Panel();
        third.setLayout(new RowLayout());
        third.setBorder(false);  
        third.setId("card-2");
        third.setTitle("Specialisation Overview");
        
        // Visualization stuff
        visualizationPanel = new Panel();
        visualizationPanel.setTitle("Visualization");
        HTML circle = new HTML("<div style=\"font-size: 12px; line-height:50px; width: 100px; height: 50px; margin: 50px 20px; border-radius:50%; text-align:center; background: orange;\">My Class Name</div>");
        visualizationPanel.add(circle);
        third.add(visualizationPanel);
        
        // Axioms list
        Panel instantiationAxiomsPanel = new Panel();
        instantiationAxiomsPanel.setTitle("ODP Instantiation Axioms");
        instantiationAxiomsPanel.setLayout(new RowLayout());
        instantationAxiomsPreview = new TextArea();
        instantationAxiomsPreview.setEnabled(false);
        instantationAxiomsPreview.setText(getInstantiationAxioms());
        instantiationAxiomsPanel.add(instantationAxiomsPreview);
        third.add(instantiationAxiomsPanel);
          
        return third;
	}
	
	/**
	 * Return the class labels of all leaves in the class tree hierarchy of this
	 * specialization wizard. 
	 * @return Array of leaf class labels
	 */
	public String[] getLeafClasses() {
		Set<TreeNode> allLeaves = getChildLeafNodes(classTreePanel.getRootNode());
		String[] classLabels = new String[allLeaves.size()];
		int i = 0;
		for (TreeNode node: allLeaves) {
			classLabels[i] = node.getText();
			i++;
		}
		return classLabels;
	}
	
	/**
	 * Recursive method to walk the a tree panel and return leaf nodes.
	 * @param parentNode - starting node for each recursion.
	 * @return lowest level TreeNodes, e.g. with no children.
	 */
	private Set<TreeNode> getChildLeafNodes(TreeNode parentNode) {
		HashSet<TreeNode> leaves = new HashSet<TreeNode>();
		if (parentNode.getChildNodes().length == 0) {
			leaves.add(parentNode);
		}
		else {
			for (final Node childNode: parentNode.getChildNodes()) {
				leaves.addAll(getChildLeafNodes((TreeNode)childNode));
			}
		}
		return leaves;
	} 
	
	private String getInstantiationAxioms() {
		// TODO: Actually implement this
		return "<#green-goblin>\n" + 
				"    rel:enemyOf <#spiderman> ;\n" + 
				"    a foaf:Person ;    # in the context of the Marvel universe\n" +
				"    foaf:name \"Green Goblin\" .\n" + 
				"\n" +
				"<#spiderman>\n" +
				"    rel:enemyOf <#green-goblin> ;\n" + 
				"    a foaf:Person ;\n" +
				"    foaf:name \"Spiderman\", \"Человек-паук\"@ru .";
	}

	/**
	 * Returns the currently selected object property node in object property hierarchy tree widget.
	 * @return A TreeNode that is currently selected.
	 */
	public TreeNode getSelectedObjectProperty() {
		DefaultSelectionModel dsm = (DefaultSelectionModel)objectPropertyTreePanel.getSelectionModel();
		return dsm.getSelectedNode();
	}

	/**
	 * Returns the currently selected datatype property node in datatype property hierarchy tree widget.
	 * @return A TreeNode that is currently selected.
	 */
	public TreeNode getSelectedDatatypeProperty() {
		DefaultSelectionModel dsm = (DefaultSelectionModel)datatypePropertyTreePanel.getSelectionModel();
		return dsm.getSelectedNode();
	}
}
