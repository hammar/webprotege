package edu.stanford.bmir.protege.web.client.ui.visualization.loading;

public class GraphLoadedEvent {
	public static final int GRAPH_LOADED = 1;
	private Loadable loadable;
	
	public GraphLoadedEvent(Loadable loadable) {
    	this.loadable = loadable;
    }

    public Loadable getLoadable() {
        return loadable;
    }
}
