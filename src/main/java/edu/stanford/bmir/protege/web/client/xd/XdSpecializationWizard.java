package edu.stanford.bmir.protege.web.client.xd;

import org.semanticweb.owlapi.model.OWLClass;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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

public class XdSpecializationWizard extends Panel {

	private Panel wizardPanel;
	private ToolbarButton backButton;
	private ToolbarButton nextButton;
	private ToolbarButton finishButton;
	private Toolbar navigationBar;
	private ButtonListenerAdapter navigationButtonsListener;
	
	// Test field for class transfer
	// TODO: remove this field
	private OWLClass odpImplementation;
	
	// Dummy field for testing
	// TODO: remove this field
	private TextArea odpUriField;
	
	public ToolbarButton getFinishButton() {
		return finishButton;
	}

	public XdSpecializationWizard() {
		super();
		this.setBorder(false);  
        this.setPaddings(15); 
		
        // General design stuff
		wizardPanel = new Panel();
		wizardPanel.setWidth(640);
        wizardPanel.setHeight(480);  
        wizardPanel.setTitle("ODP Specialisation Wizard");  
        wizardPanel.setLayout(new CardLayout());  
        wizardPanel.setActiveItem(0);  
        wizardPanel.setPaddings(15);  
        
  
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
        
        finishButton = new ToolbarButton("Finish");  
        finishButton.setId("move-finish");  
        navigationBar.addButton(finishButton);  
        wizardPanel.setBottomToolbar(navigationBar);  

        
        // These are the individual cards/screens of the wizard interface.
        wizardPanel.add(makeFirstCard());  
        wizardPanel.add(makeSecondCard());  
        wizardPanel.add(makeThirdCard()); 
        
        this.add(wizardPanel);
        
        XdServiceManager.getInstance().getOdpImplementation("http://doesnt.matter", new AsyncCallback<OWLClass>(){
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert("Something went boom: " + caught.getMessage());
			}

			@Override
			public void onSuccess(OWLClass result) {
				// TODO Auto-generated method stub
				odpImplementation = result;
				Window.alert("Fetched ODP " + odpImplementation.getIRI().toString());
				odpUriField.setText(odpImplementation.getIRI().toString());
			}
        });
	}
	
	
	// This is where we do things that reset the wizard GUI, preparing it to start over again.
	public void reset() {
		// TODO: Implement this
	}
	
	
	private ButtonListenerAdapter makeNavigationButtonsListenerAdapter() {
		return new ButtonListenerAdapter() {  
            public void onClick(Button button, EventObject e) {  
                String btnID = button.getId();  
                CardLayout cardLayout = (CardLayout) wizardPanel.getLayout();  
                String panelID = cardLayout.getActiveItem().getId();  
  
                if (btnID.equals("move-prev")) {  
                    if (panelID.equals("card-3")) {  
                        cardLayout.setActiveItem(1);  
                    } else {  
                        cardLayout.setActiveItem(0);  
                    }  
                } else {  
  
                    if (panelID.equals("card-1")) {  
                        cardLayout.setActiveItem(1);  
                    } else {  
                        cardLayout.setActiveItem(2);  
                    }  
                }  
            }  
        };  
	}
	
	private Panel makeFirstCard() {
        Panel first = new Panel();  
        first.setBorder(false);
        first.setId("card-1");
        first.setTitle("Specialisation Strategy");
        
        RadioButton strategyButton1 = new RadioButton("strategyGroup", "Property-oriented strategy");
        RadioButton strategyButton2 = new RadioButton("strategyGroup", "Class-oriented strategy");
        RadioButton strategyButton3 = new RadioButton("strategyGroup", "Hybrid strategy");

       // Add toggle button to the root panel.
       VerticalPanel strategyPanel = new VerticalPanel();
       strategyPanel.setSpacing(10);
       strategyPanel.add(new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean in rhoncus sapien. Phasellus vitae magna at enim molestie auctor et at est. Nullam consequat odio eu varius venenatis. Etiam aliquet ligula ac posuere lacinia. Curabitur aliquet elit a viverra fringilla. "));
       strategyPanel.add(strategyButton1);
       strategyPanel.add(new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean in rhoncus sapien. Phasellus vitae magna at enim molestie auctor et at est. Nullam consequat odio eu varius venenatis. Etiam aliquet ligula ac posuere lacinia. Curabitur aliquet elit a viverra fringilla. "));
       strategyPanel.add(strategyButton2);
       strategyPanel.add(new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean in rhoncus sapien. Phasellus vitae magna at enim molestie auctor et at est. Nullam consequat odio eu varius venenatis. Etiam aliquet ligula ac posuere lacinia. Curabitur aliquet elit a viverra fringilla. "));
       strategyPanel.add(strategyButton3);
       first.add(strategyPanel);
       return first;
	}
	
	private Panel makeSecondCard() {
        Panel second = new Panel();  
        second.setBorder(false);  
        second.setId("card-2");
        second.setTitle("Specialise Classes and Properties");
        second.add(new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean in rhoncus sapien. Phasellus vitae magna at enim molestie auctor et at est. Nullam consequat odio eu varius venenatis. Etiam aliquet ligula ac posuere lacinia. Curabitur aliquet elit a viverra fringilla. "));
        
        TabPanel tabPanel = new TabPanel();  
        tabPanel.setTabPosition(Position.TOP);  
        tabPanel.setResizeTabs(true);  
        tabPanel.setMinTabWidth(115);
        tabPanel.setTabWidth(135);
        tabPanel.setActiveTab(0);
        
        Panel classSpecialisationPanel = new Panel();  
        classSpecialisationPanel.setTitle("Classes");  
        classSpecialisationPanel.setAutoScroll(true);
        classSpecialisationPanel.setHtml("This is where we specialise classes.");
        
        Panel objPropertySpecialisationPanel = new Panel();  
        objPropertySpecialisationPanel.setTitle("Obj properties");  
        objPropertySpecialisationPanel.setAutoScroll(true);
        objPropertySpecialisationPanel.setHtml("This is where we specialise object properties.");
        
        Panel dataPropertySpecialisationPanel = new Panel();  
        dataPropertySpecialisationPanel.setTitle("Data properties");  
        dataPropertySpecialisationPanel.setAutoScroll(true);
        dataPropertySpecialisationPanel.setHtml("This is where we specialise datatype properties.");
        
        tabPanel.add(classSpecialisationPanel);
        tabPanel.add(objPropertySpecialisationPanel);
        tabPanel.add(dataPropertySpecialisationPanel);
        second.add(tabPanel);
        
        return second;
	}
	
	private Panel makeThirdCard() {
        Panel third = new Panel();  
        third.setBorder(false);  
        third.setId("card-3");
        third.setTitle("Specialisation Overview");
        third.setHtml("<h1>Congratulations!</h1><p>Step 3 of 3 - Complete</p>");  
        return third;
	}
}
