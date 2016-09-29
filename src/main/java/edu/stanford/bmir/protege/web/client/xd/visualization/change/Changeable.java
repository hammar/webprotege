package edu.stanford.bmir.protege.web.client.xd.visualization.change;

import java.util.Collection;

public interface Changeable {
    void addChangeListener(ChangeListener listener);
    void notifyChangeListeners(ChangedEvent changeEvent);
    void removeChangeListener(ChangeListener listener);
    Collection<? extends Object> getChange();
    //VerticalPanel getPanel();
    void setChange(Collection<? extends Object> selection);
}
