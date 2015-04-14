package edu.stanford.bmir.protege.web.client.ui.portlet;

import edu.stanford.bmir.protege.web.client.rpc.data.EntityData;
import edu.stanford.bmir.protege.web.client.ui.selection.Selectable;

/**
 * Interface that should be implemented by all portlets available in user
 * interface of a tab.<br />
 * <br />
 * 
 * It is recommended that all portlets extend the {@link AbstractOWLEntityPortlet}
 * class.
 * 
 * @author Tania Tudorache <tudorache@stanford.edu>
 */
public interface EntityPortlet extends Selectable {

    /**
     * Called when the portlet should display the content of
     * <code>newEntity</code>.
     * 
     * @param newEntity
     *            - the new entity to render
     */
    public void setEntity(EntityData newEntity);

    /**
     * @return The current entity being rendered.
     */
    public EntityData getEntity();

    /**
     * Called in the constructor of the portlet to initialize any needed GUI.
     */
    public void initialize();
}
