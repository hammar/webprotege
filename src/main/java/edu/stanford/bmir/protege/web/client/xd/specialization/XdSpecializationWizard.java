package edu.stanford.bmir.protege.web.client.xd.specialization;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.EventObject; 
import com.gwtext.client.core.Position;
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
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;

import edu.stanford.bmir.protege.web.client.xd.XdServiceManager;

public class XdSpecializationWizard extends com.gwtext.client.widgets.Window {

	//private Panel wizardPanel;
	private ToolbarButton backButton;
	private ToolbarButton nextButton;
	private ToolbarButton finishButton;
	private Toolbar navigationBar;
	private ButtonListenerAdapter navigationButtonsListener;
	private CardLayout cardLayout;
	
	// Lists of user-selected subclass/subproperty axioms
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

	public XdSpecializationWizard() {
		subClassAxioms = new ArrayList<OWLSubClassOfAxiom>();
		subDataPropertyAxioms = new ArrayList<OWLSubDataPropertyOfAxiom>();
		subObjectPropertyAxioms = new ArrayList<OWLSubObjectPropertyOfAxiom>();
		
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
	}
	
	private void saveAndClose() {
		// TODO: implement this, wrap the below in an async service call that stores wizard results
		this.hide();
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
        classTreePanel.setRootVisible(true);
        final TreeNode root2 = new TreeNode("A superclass");
        root2.appendChild(new TreeNode("A subclass"));
        root2.appendChild(new TreeNode("Another subclass"));
        root2.appendChild(new TreeNode("A third subclass"));
        classTreePanel.setRootNode(root2);
        classTreePanel.expandAll();
        classSpecialisationPanel.add(classTreePanel, new ColumnLayoutData(.9));;
        // Controls
        Panel classModificationControls = new Panel();
        classModificationControls.add(new Button("Add"));
        classModificationControls.add(new Button("Remove"));
        classModificationControls.add(new Button("Modify"));
        classSpecialisationPanel.add(classModificationControls, new ColumnLayoutData(.1));
        
        // The tab where we set object properties
        Panel objPropertySpecialisationPanel = new Panel();  
        objPropertySpecialisationPanel.setTitle("Obj properties");  
        objPropertySpecialisationPanel.setLayout(new ColumnLayout());
        // Tree panel
        objectPropertyTreePanel = new TreePanel();
        objectPropertyTreePanel.setRootVisible(true);
        final TreeNode root = new TreeNode("A superproperty");
        root.appendChild(new TreeNode("A subproperty"));
        root.appendChild(new TreeNode("Another subproperty"));
        root.appendChild(new TreeNode("A third subproperty"));
        objectPropertyTreePanel.setRootNode(root);
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
