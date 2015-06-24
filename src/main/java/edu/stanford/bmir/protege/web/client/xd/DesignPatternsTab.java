package edu.stanford.bmir.protege.web.client.xd;

import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.ui.tab.AbstractTab;
import edu.stanford.bmir.protege.web.shared.selection.SelectionModel;

/**
 * eXtreme Design tab view.
 *
 * @author Karl Hammar <karl@karlhammar.com>
 */
@SuppressWarnings("unchecked")
public class DesignPatternsTab extends AbstractTab {

	private DesignPatternSelectorPortlet patternSearchPortlet;
	private DesignPatternDetailsPortlet patternDetailsPortlet;
	
    public DesignPatternsTab(SelectionModel selectionModel, Project project) {
        super(selectionModel, project);
    }

	@Override
    public void setup() {
        super.setup();    
        
        patternSearchPortlet = (DesignPatternSelectorPortlet) getPortletByClassName(DesignPatternSelectorPortlet.class.getName());
        patternDetailsPortlet = (DesignPatternDetailsPortlet) getPortletByClassName(DesignPatternDetailsPortlet.class.getName());
        
        // Hook up pattern details portlet to listen to changes in search portlet
        patternSearchPortlet.addSelectionListener(patternDetailsPortlet);
    }
}
