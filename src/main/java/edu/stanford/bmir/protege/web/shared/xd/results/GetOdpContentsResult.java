package edu.stanford.bmir.protege.web.shared.xd.results;

import java.util.List;

import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.xd.data.EntityMetadata;
import edu.stanford.bmir.protege.web.shared.xd.data.XdTreeNode;

/**
 * This is a data carrier class to be used with the dispatch service and the corresponding
 * Action and ActionHandler classes. It sends prepopulated TreePanels containing the contents
 * of an Ontology Design Pattern over the wire to the client, to be used by the ODP 
 * specialization wizard there.
 * 
 * This should probably be a more efficient / pretty data structure (sending renderable
 * panels over the wire seems inefficient, not to mention the fact that we use key/value attributes
 * on the nodes to pass semantic meaning, ouch), but we'll have to refactor that when more time
 * is available. For now this works :)
 * 
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
public class GetOdpContentsResult implements Result {


	/**
	 * No-arg constructor, for GWT-RPC serialization purpose only
	 */
	@SuppressWarnings("unused")
	private GetOdpContentsResult() {
	}
	
	/**
	 * Constructor
	 * @param classFrames
	 * @param objectPropertyFrames
	 * @param dataPropertyFrames
	 */
	public GetOdpContentsResult(XdTreeNode<EntityMetadata> classes,
			XdTreeNode<EntityMetadata> objectProperties,
			XdTreeNode<EntityMetadata> dataProperties) {
		this.classes = classes;
		this.objectProperties = objectProperties;
		this.dataProperties = dataProperties;
	}
	
	// Data fields
	private XdTreeNode<EntityMetadata> classes;
	private XdTreeNode<EntityMetadata> objectProperties;
	private XdTreeNode<EntityMetadata> dataProperties;
	
	// Access methods
	public XdTreeNode<EntityMetadata> getClasses() {
		return classes;
	}

	public XdTreeNode<EntityMetadata> getObjectProperties() {
		return objectProperties;
	}

	public XdTreeNode<EntityMetadata> getDataProperties() {
		return dataProperties;
	}
}
