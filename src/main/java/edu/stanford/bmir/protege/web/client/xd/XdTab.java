package edu.stanford.bmir.protege.web.client.xd;

import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.ui.tab.AbstractTab;

/**
 * eXtreme Design tab view.
 *
 * @author Karl Hammar <karl@karlhammar.com>
 */
@SuppressWarnings("unchecked")
public class XdTab extends AbstractTab {

	private XdSearchPortlet patternSearchPortlet;
	
    public XdTab(Project project) {
        super(project);
    }

	@Override
    public void setup() {
        super.setup();
        
        // Set controlling portlet: this automatically sets other portlets in this tab
        // as listeners to selection events from patternSearchPortlet.
        patternSearchPortlet = (XdSearchPortlet) getPortletByClassName(XdSearchPortlet.class.getName());
        setControllingPortlet(patternSearchPortlet);
    }
}
