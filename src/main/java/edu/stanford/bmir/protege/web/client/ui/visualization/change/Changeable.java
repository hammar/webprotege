package edu.stanford.bmir.protege.web.client.ui.visualization.change;

import java.util.Collection;

import com.google.gwt.event.dom.client.ChangeEvent;



public interface Changeable {
    void addChangeListener(ChangeListener listener);
    void notifyChangeListeners(ChangedEvent changeEvent);
    void removeChangeListener(ChangeListener listener);
    Collection<? extends Object> getChange();
    //VerticalPanel getPanel();
    void setChange(Collection<? extends Object> selection);
}
