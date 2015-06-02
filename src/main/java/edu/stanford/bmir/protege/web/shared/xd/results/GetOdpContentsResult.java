package edu.stanford.bmir.protege.web.shared.xd.results;

import java.io.Serializable;

import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.xd.data.XdTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ClassFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.ObjectPropertyFrame;

/**
 * 
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
public class GetOdpContentsResult implements Result, Serializable {
	
	private static final long serialVersionUID = -1856446508517301589L;

	/**
	 * No-arg constructor, for GWT-RPC serialization purpose only
	 */
	public GetOdpContentsResult() {
	}
	
	/**
	 * Constructor
	 * @param classFrames
	 * @param objectPropertyFrames
	 * @param dataPropertyFrames
	 */
	public GetOdpContentsResult(XdTreeNode<ClassFrame> classes,
			XdTreeNode<ObjectPropertyFrame> objectProperties,
			XdTreeNode<DataPropertyFrame> dataProperties) {
		this.classes = classes;
		this.objectProperties = objectProperties;
		this.dataProperties = dataProperties;
	}
	
	// Data fields
	private XdTreeNode<ClassFrame> classes;
	private XdTreeNode<ObjectPropertyFrame> objectProperties;
	private XdTreeNode<DataPropertyFrame> dataProperties;
	
	// Access methods
	public XdTreeNode<ClassFrame> getClasses() {
		return classes;
	}

	public XdTreeNode<ObjectPropertyFrame> getObjectProperties() {
		return objectProperties;
	}

	public XdTreeNode<DataPropertyFrame> getDataProperties() {
		return dataProperties;
	}
}
