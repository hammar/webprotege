package edu.stanford.bmir.protege.web.client.xd.instantiation.old.panels;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.widgets.Panel;
import edu.stanford.bmir.protege.web.shared.xd.data.CodpSpecializationStrategy;

public class StrategySelectionPanel extends Panel {
	
	private RadioButton propStrategyButton;
	private RadioButton classStrategyButton;
	private RadioButton hybridStrategyButton;
	
	public StrategySelectionPanel() {
		super();
		
        this.setBorder(false);
        this.setId("card-0");
        this.setTitle("Specialisation Strategy");
        this.addClass("specializationStrategySelectionPanel");
        
        propStrategyButton = new RadioButton("strategyGroup","Property-oriented strategy");
        classStrategyButton = new RadioButton("strategyGroup", "Class-oriented strategy");
        hybridStrategyButton = new RadioButton("strategyGroup", "Hybrid strategy");
        propStrategyButton.setValue(true);

       // Add toggle button to the root panel.
       VerticalPanel strategyPanel = new VerticalPanel();
       //FormPanel strategyFormPanel = new FormPanel();
       strategyPanel.setSpacing(10);
       HTML instruction = new HTML("<strong>Select the appropriate Ontology Design Pattern specialization strategy "
          		+ "from the choices below. For a discussion on their respective attributes and effects, see "
          		+ "<a href=\"http://goo.gl/1y6XE6\" target=\"_blank\">http://goo.gl/1y6XE6</a></strong>");
       strategyPanel.add(instruction);
       strategyPanel.add(propStrategyButton);
       strategyPanel.add(new Label("The property-oriented strategy generates subproperties of the object and "
       		+ "datatype properties defined in the Ontology Design Pattern, with asserted domains and ranges "
       		+ "based on ODP classes, or specialized subclasses thereof. This is the most common and most "
       		+ "intuitive way of specializing an Ontology Design Pattern."));
       strategyPanel.add(classStrategyButton);
       strategyPanel.add(new Label("The class-oriented strategy specialises properties in the ontology "
       		+ "by generating property restriction axioms constraining how individuals of specialised classes "
       		+ "may be connected via the existing Ontology Design Pattern properties. Using this strategy no "
       		+ "new properties are created, but the existing ones are reused - potentially making datasets "
       		+ "that use the same Ontology Design Patterns RDF-level compatible. However, this use of property "
       		+ "restriction axioms is known to have poor effects on reasoning performance efficiency."));
       strategyPanel.add(hybridStrategyButton);
       strategyPanel.add(new Label("The hybrid strategy combines the two approaches above. It creates new "
       		+ "sub-properties, defines domains and ranges of those properties, and then also generates property "
       		+ "restriction axioms on the involved classes. This is the most logically complete specialisation "
       		+ "strategy, but may generate redundant axioms."));
       this.add(strategyPanel);
	}

	public CodpSpecializationStrategy getSelectedStrategy() {
		if (propStrategyButton.getValue()) {
			return CodpSpecializationStrategy.PROPERTY_ORIENTED;
		}
		else if (classStrategyButton.getValue()) {
			return CodpSpecializationStrategy.CLASS_ORIENTED;
		}
		else if (hybridStrategyButton.getValue()) {
			return CodpSpecializationStrategy.HYBRID;
		}
		return CodpSpecializationStrategy.PROPERTY_ORIENTED;
	}
}
