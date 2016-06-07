package edu.stanford.bmir.protege.web.client.xd.specialization.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class TreeMethods {
	public static Set<OntologyEntityFrame> getFrameTreeAsSet(FrameTreeNode<OntologyEntityFrame> startNode) {
		Set<OntologyEntityFrame> retVal = new HashSet<OntologyEntityFrame>();
		retVal.add(startNode.getData());
		for (FrameTreeNode<OntologyEntityFrame> childNode: startNode.getChildren()) {
			retVal.addAll(getFrameTreeAsSet(childNode));
		}
		return retVal;
	}
	
	public static Map<OntologyEntityFrame,Integer> getFrameTreeAsIndentMap(FrameTreeNode<OntologyEntityFrame> startNode, Integer depth) {
		Map<OntologyEntityFrame,Integer> retVal = new HashMap<OntologyEntityFrame, Integer>();
		retVal.put(startNode.getData(), depth);
		for (FrameTreeNode<OntologyEntityFrame> childNode: startNode.getChildren()) {
			retVal.putAll(getFrameTreeAsIndentMap(childNode, depth+1));
		}
		return retVal;
	}
}
