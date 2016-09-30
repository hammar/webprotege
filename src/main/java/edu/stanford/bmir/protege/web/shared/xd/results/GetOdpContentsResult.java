package edu.stanford.bmir.protege.web.shared.xd.results;

import java.io.Serializable;

import org.semanticweb.owlapi.model.IRI;

import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

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
	public GetOdpContentsResult(IRI odpIri,
			String odpAsJsonString,
			FrameTreeNode<OntologyEntityFrame> classes,
			FrameTreeNode<OntologyEntityFrame> objectProperties,
			FrameTreeNode<OntologyEntityFrame> dataProperties) {
		this.odpIri = odpIri;
		this.odpAsJsonString = odpAsJsonString;
		this.classes = classes;
		this.objectProperties = objectProperties;
		this.dataProperties = dataProperties;
	}
	
	// Data fields
	private IRI odpIri;
	private String odpAsJsonString;
	private FrameTreeNode<OntologyEntityFrame> classes;
	private FrameTreeNode<OntologyEntityFrame> objectProperties;
	private FrameTreeNode<OntologyEntityFrame> dataProperties;
	
	// Access methods
	public IRI getOdpIri() {
		return this.odpIri;
	}
	
	public FrameTreeNode<OntologyEntityFrame> getClasses() {
		return classes;
	}

	public FrameTreeNode<OntologyEntityFrame> getObjectProperties() {
		return objectProperties;
	}

	public FrameTreeNode<OntologyEntityFrame> getDataProperties() {
		return dataProperties;
	}
	
	public String getOdpAsJsonString() {
		return odpAsJsonString;
	}
}
