package edu.stanford.bmir.protege.web.shared.xd.data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class FrameTreeNode<T> implements Serializable {

	private static final long serialVersionUID = -854633460435488865L;
	
	private T data;
	private FrameTreeNode<T> parent;
	private List<FrameTreeNode<T>> children;

    /**
	 * GWT-RPC-required constructor
	 */
    @SuppressWarnings("unused")
	private FrameTreeNode() {
    }
    
    public FrameTreeNode(T data) {
        this.data = data;
        this.children = new LinkedList<FrameTreeNode<T>>();
    }

    public FrameTreeNode<T> addChild(T child) {
        FrameTreeNode<T> childNode = new FrameTreeNode<T>(child);
        childNode.parent = this;
        this.children.add(childNode);
        return childNode;
    }

	public T getData() {
		return data;
	}

	public FrameTreeNode<T> getParent() {
		return parent;
	}

	public List<FrameTreeNode<T>> getChildren() {
		return children;
	}
}