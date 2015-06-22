package edu.stanford.bmir.protege.web.shared.xd.results;

import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.event.HasEventList;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.events.EventList;

/**
 * The response to a ODP specialization persistence action. Contains a reference to the new revision
 * numer this specialization caused. 
 * 
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
public class PersistSpecializationResult implements Result, HasEventList<ProjectEvent<?>> {

	/**
	 * No-arg constructor, for GWT-RPC serialization purpose only
	 */
	@SuppressWarnings("unused")
	private PersistSpecializationResult() {
	}
	
	/**
	 * Constructor
	 * @param specializationPreview
	 */
	public PersistSpecializationResult(EventList<ProjectEvent<?>> events) {
		this.events = events;
	}
	
	// Data fields
	private EventList<ProjectEvent<?>> events;

	@Override
	public EventList<ProjectEvent<?>> getEventList() {
		return this.events;
	}
}
