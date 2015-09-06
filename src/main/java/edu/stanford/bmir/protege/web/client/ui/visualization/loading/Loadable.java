package edu.stanford.bmir.protege.web.client.ui.visualization.loading;

import java.util.Collection;

import edu.stanford.bmir.protege.web.client.ui.visualization.vowl.VOWLVisualizationJso;

public interface Loadable {
	void addGraphListener(GraphListener listener);
    void notifyGraphListeners(GraphLoadedEvent event);
    void removeGraphListener(GraphListener listener);
    Collection<? extends Object> getSelection();
    VOWLVisualizationJso getVisualizationObject();
    void setSelection(Collection<? extends Object> selection);
}
