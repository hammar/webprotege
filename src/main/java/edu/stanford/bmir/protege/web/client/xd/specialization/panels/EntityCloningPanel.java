package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import java.util.Map;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.stanford.bmir.protege.web.client.xd.specialization.DesignPatternInstantiationWizard;
import edu.stanford.bmir.protege.web.client.xd.specialization.util.TreeMethods;
import edu.stanford.bmir.protege.web.client.xd.specialization.widgets.EntityCloningWidget;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class EntityCloningPanel extends VerticalPanel implements InstantiationWizardPanel {
	
	VerticalPanel classHolderPanel;
	VerticalPanel opHolderPanel;
	VerticalPanel dpHolderPanel;
	
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
		
		Label classesHeader = new Label("Classes");
		classesHeader.addStyleName("entityCloningPanelSubheader");
		this.add(classesHeader);
		this.add(this.classHolderPanel);

		Label opHeader = new Label("Object Properties");
		opHeader.addStyleName("entityCloningPanelSubheader");
		this.add(opHeader);
		this.add(this.opHolderPanel);
		
		Label dpHeader = new Label("Datatype Properties");
		dpHeader.addStyleName("entityCloningPanelSubheader");
		this.add(dpHeader);
		this.add(this.dpHolderPanel);
	}
	
	@Override
	public void renderPanel() {
		// Clear out existing cloning widgets
		this.classHolderPanel.clear();
		this.opHolderPanel.clear();
		this.dpHolderPanel.clear();
		
		// Render new class cloning widgets
		Map<OntologyEntityFrame, Integer> classDepthMap = TreeMethods.getFrameTreeAsIndentMap(parentWizard.getOdpClasses(), 0);
		for (Map.Entry<OntologyEntityFrame, Integer> entry : classDepthMap.entrySet()) {
			String prefix = buildPrefix(entry.getValue());
			this.classHolderPanel.add(new EntityCloningWidget(this.parentWizard, entry.getKey(), prefix));
		}
		
		// Render new object property cloning widgets
		Map<OntologyEntityFrame, Integer> objectPropertyDepthMap = TreeMethods.getFrameTreeAsIndentMap(parentWizard.getOdpObjectProperties(), 0);
		for (Map.Entry<OntologyEntityFrame, Integer> entry : objectPropertyDepthMap.entrySet()) {
			String prefix = buildPrefix(entry.getValue());
			this.opHolderPanel.add(new EntityCloningWidget(this.parentWizard, entry.getKey(), prefix));
		}
		
		// Render new datatype property cloning widgets
		Map<OntologyEntityFrame, Integer> dataPropertyDepthMap = TreeMethods.getFrameTreeAsIndentMap(parentWizard.getOdpDataProperties(), 0);
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
