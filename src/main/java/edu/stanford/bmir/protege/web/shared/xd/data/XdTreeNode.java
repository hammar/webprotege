package edu.stanford.bmir.protege.web.shared.xd.data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class XdTreeNode<T> implements Serializable {

	private static final long serialVersionUID = -854633460435488865L;
	
	private T data;
	private XdTreeNode<T> parent;
	private List<XdTreeNode<T>> children;

    /**
	 * GWT-RPC-required constructor
	 */
    public XdTreeNode() {
    }
    
    public XdTreeNode(T data) {
        this.data = data;
        this.children = new LinkedList<XdTreeNode<T>>();
    }

    public XdTreeNode<T> addChild(T child) {
        XdTreeNode<T> childNode = new XdTreeNode<T>(child);
        childNode.parent = this;
        this.children.add(childNode);
        return childNode;
    }

	public T getData() {
		return data;
	}

	public XdTreeNode<T> getParent() {
		return parent;
	}

	public List<XdTreeNode<T>> getChildren() {
		return children;
	}
}