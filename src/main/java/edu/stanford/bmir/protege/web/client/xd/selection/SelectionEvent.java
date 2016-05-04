package edu.stanford.bmir.protege.web.client.xd.selection;

/**
 * 
 * Companion event to Selectable interface.
 * 
 * @author Karl Hammar (karl@karlhammar.com)
 * @version 1
 * @see Selectable
 */
public class SelectionEvent {
	private Selectable selectable;
	
	public SelectionEvent(Selectable selectable) {
    	this.selectable = selectable;
    }

    public Selectable getSelectable() {
        return selectable;
    }
}
