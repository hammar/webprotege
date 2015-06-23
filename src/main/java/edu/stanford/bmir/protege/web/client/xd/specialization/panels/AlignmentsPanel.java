package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.GroupingStore;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SortState;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxSelectionModel;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GroupingView;
import com.gwtext.client.widgets.layout.RowLayout;
import com.gwtext.client.widgets.layout.RowLayoutData;

import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.xd.specialization.XdSpecializationWizard;
import edu.stanford.bmir.protege.web.client.xd.util.UUID;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetSpecializationAlignmentSuggestionsAction;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;
import edu.stanford.bmir.protege.web.shared.xd.results.GetSpecializationAlignmentSuggestionsResult;

public class AlignmentsPanel extends Panel {
	
	// Infrastructure for restrictions presentation grid view
	private GroupingStore store;
	private RecordDef recordDef;
	private CheckboxSelectionModel cbSelectionModel;
	private XdSpecializationWizard parentWizard;
	private Map<String,Alignment> alignmentsMap;
	
	public AlignmentsPanel(XdSpecializationWizard parent) {
		super();
		
		this.parentWizard = parent;
		this.alignmentsMap = new HashMap<String,Alignment>();
		
        this.setBorder(false);  
        this.setId("card-3");
        this.setTitle("ODP Specialization Alignment");
        this.setLayout(new RowLayout());
        
        Panel instructionPanel = new Panel();
        instructionPanel.setPaddings(5);
        Label instruction = new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean in rhoncus sapien. "
        		+ "Phasellus vitae magna at enim molestie auctor et at est. Nullam consequat odio eu varius venenatis. "
        		+ "Etiam aliquet ligula ac posuere lacinia. Curabitur aliquet elit a viverra fringilla.");
        instructionPanel.add(instruction);
        this.add(instructionPanel, new RowLayoutData(50));
        
        this.cbSelectionModel = new CheckboxSelectionModel();
        
        MemoryProxy proxy = new MemoryProxy(new Object[0][3]);
        recordDef = new RecordDef(  
                new FieldDef[]{  
                        new StringFieldDef("alignment"),
                        new StringFieldDef("entity"),
                        new StringFieldDef("alignmentId")
                }  
        );  
  
        ArrayReader reader = new ArrayReader(recordDef);  
  
        store = new GroupingStore(proxy, reader);  
        store.setSortInfo(new SortState("alignment", SortDir.ASC));
        store.setGroupField("entity");
        store.load();  
  
        BaseColumnConfig[] columns = new BaseColumnConfig[]{  
        		new CheckboxColumnConfig(cbSelectionModel),
                new ColumnConfig("Alignment", "alignment", 160, true, null, "alignment"),  
                new ColumnConfig("OWL Entity", "entity", 60, true)  
        };  
  
        ColumnModel columnModel = new ColumnModel(columns);
        GridPanel grid = new GridPanel();  
        grid.setStore(store);  
        grid.setColumnModel(columnModel);  
        grid.setFrame(true);  
        grid.setStripeRows(true);  
        grid.setAutoExpandColumn("alignment"); 
        grid.setSelectionModel(cbSelectionModel);
        
        GroupingView gridView = new GroupingView();  
        gridView.setForceFit(true);  
        gridView.setGroupTextTpl("{text} ({[values.rs.length]} {[values.rs.length > 1 ? \"Candidate Alignments\" : \"Candidate Alignment\"]})");  
        grid.setView(gridView);  
        grid.setAnimCollapse(false);
        grid.setAutoScroll(true);
        
        this.add(grid);
	}
	
	public void loadCandidateAlignments() {
		MessageBox.show(new MessageBoxConfig() {  
            {  
                setMsg("Retrieving alignment suggestions, please wait...");  
                setProgressText("Retrieving alignments...");  
                setWidth(300);  
                setWait(true);  
                setWaitConfig(new WaitConfig() {  
                    {  
                        setInterval(200);  
                    }  
                });    
            }  
        }); 
		
		GetSpecializationAlignmentSuggestionsAction action = new GetSpecializationAlignmentSuggestionsAction(parentWizard.getProjectId(), 
				parentWizard.getAllClasses(), 
				parentWizard.getAllDataProperties(), 
				parentWizard.getAllObjectProperties());
		DispatchServiceManager.get().execute(action, new DispatchServiceCallback<GetSpecializationAlignmentSuggestionsResult>() {
        	@Override
            public void handleSuccess(GetSpecializationAlignmentSuggestionsResult result) {
        		MessageBox.hide();
        		
        		for (Alignment alignment: result.getAlignments()) {
        			String alignmentKey = UUID.uuid();
        			// Put into map for later retrieval reference
        			alignmentsMap.put(alignmentKey, alignment);
        			// Create record and add to store
        			Record record = recordDef.createRecord(new Object[]{alignment.toString(), alignment.getSortableEntityLabel(), alignmentKey});
        			store.add(record);
        		}
        		store.groupBy("entity", true);
    			store.commitChanges();
            }
        });
	}
	
	public void resetAlignments() {
		this.alignmentsMap.clear();
		this.cbSelectionModel.clearSelections();
		this.store.removeAll();
		this.store.commitChanges();
	}
	
	public Set<Alignment> getSelectedAlignments() {
		Set<Alignment> alignments = new HashSet<Alignment>();
		Record[] records = cbSelectionModel.getSelections();
		for (Record record: records) {
			String alignmentKey = record.getAsString("alignmentId");
			if (alignmentsMap.containsKey(alignmentKey)) {
				alignments.add(alignmentsMap.get(alignmentKey));
			}
		}
		return alignments;
	}
}
