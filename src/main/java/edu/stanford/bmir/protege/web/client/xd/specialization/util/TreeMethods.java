package edu.stanford.bmir.protege.web.client.xd.specialization.util;

import java.util.HashMap;
import java.util.Map;
import edu.stanford.bmir.protege.web.shared.xd.data.FrameTreeNode;
import edu.stanford.bmir.protege.web.shared.xd.data.entityframes.OntologyEntityFrame;

public class TreeMethods {
	
	/**
	 * Generate from a tree a map of nodes (as keys) and their depth in the tree (as values), excluding
	 * the top-level node.
	 * @param topNode
	 * @return
	 */
	public static Map<OntologyEntityFrame,Integer> getFrameTreeAsIndentMap(FrameTreeNode<OntologyEntityFrame> topNode) {
		Map<OntologyEntityFrame,Integer> retVal = new HashMap<OntologyEntityFrame, Integer>();
		for (FrameTreeNode<OntologyEntityFrame> childNode: topNode.getChildren()) {
			retVal.putAll(getFrameTreeAsIndentMap(childNode, 0));
		}
		return retVal;
	}
	
	/**
	 * Recurse through a tree of ontology entity frames, building and returning a map of tree nodes (as keys)
	 * and their depth in the tree (as values). 
	 * @param startNode
	 * @param depth
	 * @return
	 */
	public static Map<OntologyEntityFrame,Integer> getFrameTreeAsIndentMap(FrameTreeNode<OntologyEntityFrame> startNode, Integer depth) {
		Map<OntologyEntityFrame,Integer> retVal = new HashMap<OntologyEntityFrame, Integer>();
		retVal.put(startNode.getData(), depth);
		for (FrameTreeNode<OntologyEntityFrame> childNode: startNode.getChildren()) {
			retVal.putAll(getFrameTreeAsIndentMap(childNode, depth+1));
		}
		return retVal;
	}
	
	/**
	 * Copies a tree of ontology entity frames, pruning all single-child nodes from the tree.
	 * @param startNode
	 * @return
	 */
	public static FrameTreeNode<OntologyEntityFrame> tightenTree(FrameTreeNode<OntologyEntityFrame> startNode) {
		FrameTreeNode<OntologyEntityFrame> retVal = new FrameTreeNode<OntologyEntityFrame>(startNode.getData());
		int nrOfChildren = startNode.getChildren().size();
		if (nrOfChildren == 0) {
			// This is a leaf node, do nothing, just return the newly minted node.
			return retVal;
		}
		else if (nrOfChildren == 1) {
			// This node should not be copied into the target tree - recurse directly into
			// its only child.
			FrameTreeNode<OntologyEntityFrame> childNode = startNode.getChildren().get(0);
			return tightenTree(childNode);
		}
		else if (nrOfChildren > 1) {
			// Proceed by recursing into each child node and copying it to the new return tree.
			for (FrameTreeNode<OntologyEntityFrame> childNode: startNode.getChildren()) {
				FrameTreeNode<OntologyEntityFrame> tightenedSubTree = tightenTree(childNode);
				retVal.addChildTree(tightenedSubTree);
			}
		}
		return retVal;
	}
}
