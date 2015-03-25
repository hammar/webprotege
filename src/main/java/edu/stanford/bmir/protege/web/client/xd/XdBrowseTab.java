package edu.stanford.bmir.protege.web.client.xd;

import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.ui.tab.AbstractTab;

/**
 * XD ODP Browser tab view.
 *
 * @author Karl Hammar <karl@karlhammar.com>
 */
@SuppressWarnings("unchecked")
public class XdBrowseTab extends AbstractTab {

	private XdBrowsePortlet patternBrowsePortlet;
	
    public XdBrowseTab(Project project) {
        super(project);
    }

	@Override
    public void setup() {
        super.setup();
        
        // Set controlling portlet: this automatically sets other portlets in this tab
        // as listeners to selection events from patternSearchPortlet.
        patternBrowsePortlet = (XdBrowsePortlet) getPortletByClassName(XdBrowsePortlet.class.getName());
        setControllingPortlet(patternBrowsePortlet);
    }
}
