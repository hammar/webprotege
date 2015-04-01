package edu.stanford.bmir.protege.web.client.xd;

import com.gwtext.client.core.EventObject; 
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
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
        first.setHtml("<h1>Welcome to the Wizard!</h1><p>Step 1 of 3</p>");
        return first;
	}
	
	private Panel makeSecondCard() {
        Panel second = new Panel();  
        second.setBorder(false);  
        second.setId("card-2");  
        second.setHtml("<p>Step 2 of 3</p>");
        return second;
	}
	
	private Panel makeThirdCard() {
        Panel third = new Panel();  
        third.setBorder(false);  
        third.setId("card-3");  
        third.setHtml("<h1>Congratulations!</h1><p>Step 3 of 3 - Complete</p>");  
        return third;
	}
}
