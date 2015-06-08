package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.DateFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.FloatFieldDef;
import com.gwtext.client.data.GroupingStore;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SortState;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxSelectionModel;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GroupingView;
import com.gwtext.client.widgets.grid.RowSelectionModel;

import edu.stanford.bmir.protege.web.client.xd.specialization.XdSpecializationWizard;
import edu.stanford.bmir.protege.web.client.xd.specialization.restriction.Restriction;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpSpecializationStrategy;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class PropertyRestrictionPanel extends Panel {
	
	private Map<String,Restriction> restrictionsMap;
	
	private XdSpecializationWizard parentWizard; 
	//private Panel restrictionsPanel;
	//private FormPanel restrictionsFormPanel;
	//private Label testLabel;
	private CheckboxSelectionModel cbSelectionModel; 
	
	public PropertyRestrictionPanel(XdSpecializationWizard parent) {
		super();
		
		this.parentWizard = parent;
		
        this.setBorder(false);  
        this.setId("card-2");
        this.setTitle("Property Restriction");
        
        this.restrictionsMap = new HashMap<String,Restriction>();
        this.cbSelectionModel = new CheckboxSelectionModel();
        
        MemoryProxy proxy = new MemoryProxy(getCompanyData());  
        RecordDef recordDef = new RecordDef(  
                new FieldDef[]{  
                        new StringFieldDef("company"),
                        new StringFieldDef("entity"),
                        new StringFieldDef("restrictionId")
                }  
        );  
  
        ArrayReader reader = new ArrayReader(recordDef);  
  
        GroupingStore store = new GroupingStore();  
        store.setReader(reader);  
        store.setDataProxy(proxy);  
        store.setSortInfo(new SortState("company", SortDir.ASC));  
        store.setGroupField("entity");  
        store.load();  
  
        BaseColumnConfig[] columns = new BaseColumnConfig[]{  
                //column ID is company which is later used in setAutoExpandColumn
        		new CheckboxColumnConfig(cbSelectionModel),
                new ColumnConfig("Company", "company", 160, true, null, "company"),  
                new ColumnConfig("OWL Entity", "entity", 60, true)  
        };  
  
        ColumnModel columnModel = new ColumnModel(columns);
        GridPanel grid = new GridPanel();  
        grid.setStore(store);  
        grid.setColumnModel(columnModel);  
        grid.setFrame(true);  
        grid.setStripeRows(true);  
        grid.setAutoExpandColumn("company"); 
        //grid.setSelectionModel(new RowSelectionModel());
        grid.setSelectionModel(cbSelectionModel);
        //grid.setTitle("Grid Events");  
        //grid.setHeight(350);  
        //grid.setWidth(600);  
  
        GroupingView gridView = new GroupingView();  
        gridView.setForceFit(true);  
        gridView.setGroupTextTpl("{text} ({[values.rs.length]} {[values.rs.length > 1 ? \"Candidate Restrictions\" : \"Candidate Restriction\"]})");  
  
        grid.setView(gridView);  
        grid.setFrame(true);  
        
        //grid.setWidth(520);  
        //grid.setHeight(400);  
        //grid.setCollapsible(true);  
        grid.setAnimCollapse(false);
        //grid.setMargins(10);
        //grid.add(new Label("An instructive label!"));
        //grid.setTitle("Grouping Example");  
        //grid.setIconCls("grid-icon");
        
        this.add(grid);
	}
	
	// Load the entities from parent wizard (ODP entities + specialized entities)
	// and generate candidate restrictions from them for the user to accept or reject
	public void loadEntities() {
		
		// Render property-oriented restrictions
		if (parentWizard.getSpecializationStrategy() == OdpSpecializationStrategy.PROPERTY_ORIENTED ||
				parentWizard.getSpecializationStrategy() == OdpSpecializationStrategy.HYBRID) {
			
			// TODO: Build Restrictions according to strategy, add to map and grid store, reload grid
			
			//restrictionsFormPanel.add(new Checkbox("asdf","asdfasdf2"));
			//for (FrameTreeNode<OntologyEntityFrame> fn: parentWizard.getAllClasses()) {
			//	// TODO ACTUALLY BUILD
			//	//this.add(new Label("oy!"));
			//	//this.add(new Label(fn.getData().getLabel()));
			//}
		}
		
		// Render class-oriented restrictions
		if (parentWizard.getSpecializationStrategy() == OdpSpecializationStrategy.CLASS_ORIENTED ||
				parentWizard.getSpecializationStrategy() == OdpSpecializationStrategy.HYBRID) {
			// TODO: Build Restrictions according to strategy, add to map and grid store, reload grid
		}
		
		
	}
	
	// DEBUG code can be removed
	private String printFrameTree(FrameTreeNode<OntologyEntityFrame> classes, String prefix) {
		String ret = prefix + " " + classes.getData().getLabel();
		for (FrameTreeNode<OntologyEntityFrame> childFrameTreeNode: classes.getChildren()) {
			ret += ("\n" +  printFrameTree(childFrameTreeNode, prefix + "|"));
		}
		return ret;
	}
	
	// Store user-selected restrictions to the classes, dataproperties, and object properties
	// trees (which are references to the trees held in the parent specialization wizard).
	public void persistRestrictions() {
		
	}
	
	// Reset any restrictions or other modifications on entities
	// to the state from last screen, e.g., entity specialization panel
	public void resetRestrictions() {
		
	}
	
	private Object[][] getCompanyData() {  
        return new Object[][]{  
                new Object[]{"3m Co", "Manufacturing", "random123456"},  
                new Object[]{"Alcoa Inc", "Manufacturing", "random123456"},  
                new Object[]{"Altria Group Inc", "Manufacturing", "random123456"},  
                new Object[]{"American Express Company", "Finance", "random123456"},  
                new Object[]{"American International Group, Inc.", "Services", "random123456"},  
                new Object[]{"AT&T Inc.", "Services", "random123456"},  
                new Object[]{"Boeing Co.", "Manufacturing", "random123456"},  
                new Object[]{"Caterpillar Inc.", "Services", "random123456"},  
                new Object[]{"Citigroup, Inc.", "Finance", "random123456"},  
                new Object[]{"E.I. du Pont de Nemours and Company", "Manufacturing", "random123456"}  
        };  
    }  
}
