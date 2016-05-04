package edu.stanford.bmir.protege.web.client.ui.visualization.change;

public class ChangedEvent {
	public static final int IS_CHANGED = 1;
	private Changeable changeable;

	public ChangedEvent(Changeable changeable) {
		this.changeable = changeable;
	}

	public Changeable getChangeable() {
		return changeable;
	}
}
