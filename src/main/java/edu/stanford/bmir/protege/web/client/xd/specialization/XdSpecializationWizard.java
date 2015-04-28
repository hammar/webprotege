package edu.stanford.bmir.protege.web.client.xd.specialization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import com.google.common.base.Optional;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.EventObject; 
import com.gwtext.client.core.Position;
import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.CardLayout;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.layout.RowLayout;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;

import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.dispatch.actions.CreateClassAction;
import edu.stanford.bmir.protege.web.client.dispatch.actions.CreateClassResult;
import edu.stanford.bmir.protege.web.client.dispatch.actions.CreateObjectPropertiesAction;
import edu.stanford.bmir.protege.web.client.dispatch.actions.CreateObjectPropertiesResult;
import edu.stanford.bmir.protege.web.client.dispatch.actions.UpdateClassFrameAction;
import edu.stanford.bmir.protege.web.client.ui.frame.LabelledFrame;
import edu.stanford.bmir.protege.web.client.xd.XdPatternDetailsPortlet;
import edu.stanford.bmir.protege.web.client.xd.XdServiceManager;
import edu.stanford.bmir.protege.web.shared.DataFactory;
import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.dispatch.UpdateObjectAction;
import edu.stanford.bmir.protege.web.shared.frame.ClassFrame;
import edu.stanford.bmir.protege.web.shared.frame.PropertyAnnotationValue;
import edu.stanford.bmir.protege.web.shared.frame.PropertyValue;
import edu.stanford.bmir.protege.web.shared.frame.PropertyValueState;

public class XdSpecializationWizard extends com.gwtext.client.widgets.Window {

	// Calling portlet
	private XdPatternDetailsPortlet parent;
	
	//private Panel wizardPanel;
	private ToolbarButton backButton;
	private ToolbarButton nextButton;
	private ToolbarButton finishButton;
	private Toolbar navigationBar;
	private ButtonListenerAdapter navigationButtonsListener;
	private CardLayout cardLayout;
	
	// Lists of user-selected subclass/subproperty axioms
	//private OWLClass owlThing;
	private List<OWLSubClassOfAxiom> subClassAxioms;
	private List<OWLSubDataPropertyOfAxiom> subDataPropertyAxioms;
	private List<OWLSubObjectPropertyOfAxiom> subObjectPropertyAxioms;
	
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
	private ClassDetailsWindow cdw;
	
	// Progress window when performing instantiation
	private MessageBoxConfig instantiationProgressConf;
	
	// Used to synchroneize GWT-RPC-calls so that frame updates don't occur prior to entity creation
	private Integer requiredCreationServiceCalls;
	private Integer requiredUpdateServiceCalls;
	private Integer completedCreationServiceCalls;
	private Integer completedUpdateServiceCalls;
	
	public XdSpecializationWizard(XdPatternDetailsPortlet parent) {
		this.parent = parent;
		cdw = new ClassDetailsWindow();
		
		// TODO: GET DEBUG ROOT CLASS
		//owlThing = DataFactory.getOWLThing();
		
		subClassAxioms = new ArrayList<OWLSubClassOfAxiom>();
		subDataPropertyAxioms = new ArrayList<OWLSubDataPropertyOfAxiom>();
		subObjectPropertyAxioms = new ArrayList<OWLSubObjectPropertyOfAxiom>();
		
		requiredCreationServiceCalls = 0;
		requiredUpdateServiceCalls = 0;
		completedCreationServiceCalls = 0;
		completedUpdateServiceCalls = 0;
		
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
        instantiationProgressConf = new MessageBoxConfig() {  
            {  
                setTitle("Instantiating ODP...");  
                setMsg("Creating entities...");  
                setWidth(240);  
                setProgress(true);  
                setClosable(false);  
            }  
        };
        
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
		
		// TODO: fixa varför den här inte funkar?
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
		// TODO: include also datatype properties once those creation
		// dispatch calls are implemented
		return getInclusiveChildNodeCount(classTreePanel.getRootNode()) +
				getInclusiveChildNodeCount(objectPropertyTreePanel.getRootNode()) - 2; /* + 
				getInclusiveChildNodeCount(datatypePropertyTreePanel.getRootNode()) - 3;*/
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
			updateObjectPropertyFrames();
			updateDatatypePropertyFrames();
			
			// TODO: Debug code, remove once done
			final Node tempNode = classTreePanel.getRootNode().getChildNodes()[0];
			OWLClass tempClass = (OWLClass)tempNode.getAttributeAsObject("owlClass");
			//Window.alert("Created " + tempClass.getIRI().toString());
		} 
	}
	
	// TODO: Write comment here 
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
			CreateClassAction cca = new CreateClassAction(parent.getProjectId(),childTreeNode.getText(),parentClass);
			
			DispatchServiceManager.get().execute(cca, new DispatchServiceCallback<CreateClassResult>() {
		        public void handleSuccess(final CreateClassResult result) {
		        	completedCreationServiceCalls += 1;
		        	childTreeNode.setAttribute("owlClass", result.getObject());
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
	private void updateProgressBarAndClose() {
		Integer percentage = (100*completedCreationServiceCalls)/(100*requiredCreationServiceCalls);
    	MessageBox.updateProgress(percentage);
    	if (completedUpdateServiceCalls == requiredUpdateServiceCalls)
    	{
    		MessageBox.hide();
    	}
	}
	
	private void createChildObjectProperties(TreeNode parentNode) {
		OWLObjectProperty parentProperty = (OWLObjectProperty)parentNode.getAttributeAsObject("owlObjectProperty");
		final Node[] childNodes = parentNode.getChildNodes();
		
		for (final Node childNode: childNodes) {
			final TreeNode childTreeNode = (TreeNode)childNode;
			String objectPropertyName = childTreeNode.getText();
			Set<String> objectPropertyNames = new HashSet<String>(Arrays.asList(objectPropertyName));
			CreateObjectPropertiesAction copa = new CreateObjectPropertiesAction(parent.getProjectId(), objectPropertyNames, Optional.of(parentProperty));
			
			DispatchServiceManager.get().execute(copa, new DispatchServiceCallback<CreateObjectPropertiesResult>() {
		        public void handleSuccess(final CreateObjectPropertiesResult result) {
		        	completedCreationServiceCalls += 1;
		        	childTreeNode.setAttribute("owlObjectProperty", result.getEntities().iterator().next());
		        	//updateProgressBarAndClose();
		        	createChildObjectProperties(childTreeNode);
		        	proceedIfAllEntitiesCreated();
		        }
			});
		}
		// TODO: Implement this
	}
	
	private void createChildDatatypeProperties(TreeNode parentNode) {
		// TODO: Implement this
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
			UpdateObjectAction<LabelledFrame<ClassFrame>> uoa = new UpdateClassFrameAction(parent.getProjectId(),fromFrame,toFrame);
			DispatchServiceManager.get().execute(uoa, new DispatchServiceCallback<Result>() {
		        public void handleSuccess(final Result result) {
		        	completedUpdateServiceCalls += 1;
		        	updateChildClassFrames(childTreeNode);
		        	proceedIfAllEntitiesUpdated();
		        }
			});
		}
	}
	
	private void updateObjectPropertyFrames() {
		// TODO: Implement this
	}
	
	private void updateDatatypePropertyFrames() {
		// TODO: Implement this
	}
	
	// This is where we clear out old data, load new data required 
	// to run the wizard, prepare fields, etc etc
	public void loadOdp(String uri) {
		subClassAxioms.clear();
		subDataPropertyAxioms.clear();
		subObjectPropertyAxioms.clear();
		
		XdServiceManager.getInstance().getOdpImplementation(uri, new AsyncCallback<OWLClass>(){
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("An error occured. ODP implementation could not be retrieved from GWT back-end. Error message: " + caught.getMessage());
			}

			@Override
			public void onSuccess(OWLClass result) {
				//odpImplementation = result;
			}
        });
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
        odpClassNode1.appendChild(new TreeNode("Custom subclass 1"));
        rootClassNode.appendChild(odpClassNode1);
        
        TreeNode odpClassNode2 = new TreeNode("ODP Class 2");
        odpClassNode2.setAttribute("rdfsComment", "This is an ODP test class 2.");
        odpClassNode2.appendChild(new TreeNode("Custom subclass 2a"));
        TreeNode subClass2b = new TreeNode("Custom subclass 2b");
        subClass2b.setAttribute("rdfsComment", "This is custom subclass 2b.");
        odpClassNode2.appendChild(subClass2b);
        rootClassNode.appendChild(odpClassNode2);
        
        TreeNode odpClassNode3 = new TreeNode("ODP Class 3");
        odpClassNode3.appendChild(new TreeNode("Custom subclass 3a"));
        odpClassNode3.appendChild(new TreeNode("Custom subclass 3b"));
        odpClassNode3.appendChild(new TreeNode("Custom subclass 3c"));
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
        		cdw.reset();
        		cdw.show();
        	}
        });
        classModificationControls.add(addClassButton);
        classModificationControls.add(new Button("Remove"));
        Button modifyClassButton = new Button("Modify class");
        modifyClassButton.addListener(new ButtonListenerAdapter() {
        	@Override
        	public void onClick(final Button button, final EventObject e) {
        		cdw.reset();
        		cdw.show();
        		// TODO: cdw.load()...
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
        objectPropertyTreePanel.setRootVisible(true);
        final TreeNode rootObjectPropertyNode = new TreeNode("A superproperty");
        rootObjectPropertyNode.setAttribute("owlObjectProperty", DataFactory.getOWLObjectProperty(OWLRDFVocabulary.OWL_TOP_OBJECT_PROPERTY.getIRI()));
        rootObjectPropertyNode.appendChild(new TreeNode("A subproperty"));
        rootObjectPropertyNode.appendChild(new TreeNode("Another subproperty"));
        rootObjectPropertyNode.appendChild(new TreeNode("A third subproperty"));
        objectPropertyTreePanel.setRootNode(rootObjectPropertyNode);
        objectPropertyTreePanel.expandAll();
        objPropertySpecialisationPanel.add(objectPropertyTreePanel, new ColumnLayoutData(.9));
        // Controls
        Panel objectPropertyModificationControls = new Panel();
        objectPropertyModificationControls.add(new Button("Add"));
        objectPropertyModificationControls.add(new Button("Remove"));
        objectPropertyModificationControls.add(new Button("Modify"));
        objPropertySpecialisationPanel.add(objectPropertyModificationControls, new ColumnLayoutData(.1));
        
        // The tab where we set datatype properties
        Panel dataPropertySpecialisationPanel = new Panel();  
        dataPropertySpecialisationPanel.setTitle("Data properties");
        dataPropertySpecialisationPanel.setLayout(new ColumnLayout());
        // Tree panel
        datatypePropertyTreePanel = new TreePanel();
        datatypePropertyTreePanel.setRootVisible(true);
        final TreeNode root3 = new TreeNode("A superdataproperty");
        root3.appendChild(new TreeNode("A subdataproperty"));
        root3.appendChild(new TreeNode("Another subdataproperty"));
        root3.appendChild(new TreeNode("A third subdataproperty"));
        datatypePropertyTreePanel.setRootNode(root3);
        datatypePropertyTreePanel.expandAll();
        dataPropertySpecialisationPanel.add(datatypePropertyTreePanel, new ColumnLayoutData(.9));
        // Controls
        Panel datatypePropertyModificationControls = new Panel();
        datatypePropertyModificationControls.add(new Button("Add"));
        datatypePropertyModificationControls.add(new Button("Remove"));
        datatypePropertyModificationControls.add(new Button("Modify"));
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
}
