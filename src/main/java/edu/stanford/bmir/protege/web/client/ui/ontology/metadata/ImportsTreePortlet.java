package edu.stanford.bmir.protege.web.client.ui.ontology.metadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import edu.stanford.bmir.protege.web.client.project.Project;

import edu.stanford.bmir.protege.web.client.rpc.OntologyServiceManager;
import edu.stanford.bmir.protege.web.client.rpc.data.EntityData;
import edu.stanford.bmir.protege.web.client.rpc.data.ImportsData;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;
import edu.stanford.bmir.protege.web.shared.selection.SelectionModel;

import java.util.ArrayList;

/**
 * @author Jennifer Vendetti <vendetti@stanford.edu>
 */
public class ImportsTreePortlet extends AbstractOWLEntityPortlet {

	protected TreePanel importsTree;
	protected ArrayList<EntityData> currentSelection;

	public ImportsTreePortlet(SelectionModel selectionModel, Project project) {
		super(selectionModel, project);
	}

	public void initialize() {
		setLayout(new FitLayout());
		setTitle("Imported Ontologies");

		importsTree = new TreePanel();
		importsTree.setHeight(400);
		importsTree.setAnimate(true);
		importsTree.setAutoWidth(true);		
		importsTree.setAutoScroll(true);

		TreeNode root = new TreeNode();
		root.setText(getProject().getDisplayName());
		root.setId(getProjectId().getId());
		root.setUserObject(new EntityData(getProjectId().getId(), getProject().getDisplayName()));

		importsTree.setRootNode(root);
		importsTree.setRootVisible(true);

		add(importsTree);
	}

	protected void afterRender() {
		getImportedOntologies();
		super.afterRender();
	}

	public ArrayList<EntityData> getSelection() {
		return currentSelection;
	}

	public void getImportedOntologies() {
		OntologyServiceManager.getInstance().getImportedOntologies(getProjectId(),
				new GetImportedOntologies());
	}
	
	/*
	 * Remote calls
	 */

	class GetImportedOntologies implements AsyncCallback<ImportsData> {

		public void onFailure(Throwable caught) {
			GWT.log("RPC error getting imported ontologies ", caught);
		}

		public void onSuccess(ImportsData result) {
			TreeNode root = importsTree.getRootNode();
			addImports(result, root);
			root.select();
			root.expand();
		}
	}
	
	/*
	 * Helper methods
	 */
	
	private void addImports(ImportsData data, TreeNode node) {
		String nodeName = data.getName();
		node.setText(nodeName);
		node.setId(nodeName);
		node.setUserObject(new EntityData(data.getName(), data.getName()));
		
		ArrayList<ImportsData> imports = data.getImports();
		for (ImportsData importData : imports) {
			TreeNode childNode = new TreeNode();
			addImports(importData, childNode);
			node.appendChild(childNode);
		}
	}
}
