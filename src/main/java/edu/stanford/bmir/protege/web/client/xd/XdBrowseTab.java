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
	private XdPatternDetailsPortlet patternDetailsPortlet;
	
    public XdBrowseTab(Project project) {
        super(project);
    }

	@Override
    public void setup() {
        super.setup();
        
        patternBrowsePortlet = (XdBrowsePortlet) getPortletByClassName(XdBrowsePortlet.class.getName());
        patternDetailsPortlet = (XdPatternDetailsPortlet) getPortletByClassName(XdPatternDetailsPortlet.class.getName());
        
        // Hook up pattern details portlet to listen to changes in search portlet
        patternBrowsePortlet.addSelectionListener(patternDetailsPortlet);
    }
}
