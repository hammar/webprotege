package edu.stanford.bmir.protege.web.client.ui.visualization.selection;

import java.util.Collection;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Re-implementation of the {@link edu.stanford.bmir.protege.web.client.ui.selection.Selectable} interface
 * supporting more general selections (essentially any Collection of Objects).<br><br>
 * 
 * A class which implements this interface allows classes that implement {@link SelectionListener} to register 
 * their interest in being informed about selection updates on this class. Each such listener must be
 * added using the {@link #addSelectionListener(SelectionListener)} method, and may be removed using the
 * {@link #removeSelectionListener(SelectionListener)} method. When a selection change takes place, the Selectable 
 * implementation must notify all current listeners by calling 
 * {@link SelectionListener#selectionChanged(SelectionEvent)}, passing along a {@link SelectionEvent}
 * object referring back to this Selectable. Those listeners may then call back to the implementation's 
 * {@link #getSelection()} method to actually get the selection contents.
 * 
 * @author Karl Hammar (karl@karlhammar.com)
 * @version 1
 */
public interface Selectable {
    void addSelectionListener(SelectionListener listener);
    void notifySelectionListeners(SelectionEvent clickEvent);
    void removeSelectionListener(SelectionListener listener);
    Collection<? extends Object> getSelection();
    VerticalPanel getPanel();
    void setSelection(Collection<? extends Object> selection);
}
