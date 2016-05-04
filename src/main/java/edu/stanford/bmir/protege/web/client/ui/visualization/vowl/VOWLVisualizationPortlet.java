package edu.stanford.bmir.protege.web.client.ui.visualization.vowl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;

import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;
import edu.stanford.bmir.protege.web.client.ui.visualization.change.ChangeListener;
import edu.stanford.bmir.protege.web.client.ui.visualization.change.ChangedEvent;
import edu.stanford.bmir.protege.web.client.ui.visualization.loading.GraphListener;
import edu.stanford.bmir.protege.web.client.ui.visualization.loading.GraphLoadedEvent;
import edu.stanford.bmir.protege.web.client.ui.visualization.loading.Loadable;
import edu.stanford.bmir.protege.web.client.ui.visualization.selection.Selectable;
import edu.stanford.bmir.protege.web.client.ui.visualization.selection.SelectionEvent;
import edu.stanford.bmir.protege.web.client.ui.visualization.selection.SelectionListener;
import edu.stanford.bmir.protege.web.shared.selection.SelectionModel;
import edu.stanford.bmir.protege.web.shared.visualization.vowl.ConvertOntologyAction;
import edu.stanford.bmir.protege.web.shared.visualization.vowl.ConvertOntologyResult;


/**
 * This renders the graph visualization of the selected ontology.
 * @author Maria Michou
 *
 */
@SuppressWarnings("unchecked")
public class VOWLVisualizationPortlet extends AbstractOWLEntityPortlet implements Selectable, Loadable, ChangeListener, SelectionListener {

	private static final String VOWL_TITLE = "WebVOWL 0.4.0";
	private VOWLVisualizationJso visualizationJso;
	public static String ontologyAsJSONStr;
	public static JSONValue jsonValue;
	private static Optional<String> selectedElement;
	private static Optional<String> elementType;

	// Listeners to selection events in this portlet
	private Collection<SelectionListener> listeners;

	// Listeners to graph loaded events in this portlet
	private Collection<GraphListener> graphListeners;

	private Widget graphContainer;
	private VerticalPanel detailsDynamicPanel;
	private boolean initialized = false;

	public VOWLVisualizationPortlet(SelectionModel selectionModel, Project project) {
		super(selectionModel, project);
		this.listeners = new ArrayList<SelectionListener>();
		this.graphListeners = new ArrayList<GraphListener>();
	}

	@Override 
	public void initialize() {
		setTitle(VOWL_TITLE);
		//Widget graphContainer = new HTML();
		graphContainer = new HTML();

		graphContainer.getElement().setId(getContainerId());

		add(graphContainer);
	}

	@Override
	public void handleActivated() {
		super.handleActivated();
		GWT.log("[VOWL] Visualization portlet status: Activated.");
		if(!initialized)
			convertOntology();
	}

	public void convertOntology() {
		DispatchServiceManager.get().execute(new ConvertOntologyAction(getProjectId()), new DispatchServiceCallback<ConvertOntologyResult>() {
			@Override
			public void handleSuccess(ConvertOntologyResult result) {
				ontologyAsJSONStr = result.getOntologyasJSONStr();
				initializeVisualization(ontologyAsJSONStr);
				setOntologyAsJSONString(ontologyAsJSONStr);
				jsonValue = JSONParser.parseStrict(ontologyAsJSONStr);
			}
		});
		initialized = true;
	}

	private void setOntologyAsJSONString(String ontologyAsJSONStr) {
		VOWLVisualizationPortlet.ontologyAsJSONStr = ontologyAsJSONStr;
	}


	private void initializeVisualization(final String convertedOntology) {

		if (Document.get().getElementById(getContainerId()) != null) {

			if (VOWLVisualizationJso.isBrowserCompatible(getContainerId()))
				visualizationJso = VOWLVisualizationJso.initialize(getContainerId(), convertedOntology);

			graphContainer.addDomHandler(new MyClickHandler(), ClickEvent.getType());

			notifyGraphListeners(new GraphLoadedEvent(VOWLVisualizationPortlet.this));

		}
	}


	@Override
	protected void onRefresh() {

		DispatchServiceManager.get().execute(new ConvertOntologyAction(getProjectId()), new DispatchServiceCallback<ConvertOntologyResult>() {
			@Override
			public void handleSuccess(ConvertOntologyResult result) {
				ontologyAsJSONStr = result.getOntologyasJSONStr();
				visualizationJso.setData(ontologyAsJSONStr);
				setOntologyAsJSONString(ontologyAsJSONStr);
				jsonValue = JSONParser.parseStrict(ontologyAsJSONStr);

			}
		});
	}

	private String getContainerId() {
		/* The value has to begin with a letter to be valid for selecting the element.
		 * It is also important to use not only the project id, because this class is instantiated
		 * sometimes multiple times for the same project.
		 */
		return "project-id-" + getProjectId().getId() + "-hash-code-" + hashCode();
	}

	@Override
	protected TabPanel getConfigurationPanel() {
		TabPanel configurationPanel = super.getConfigurationPanel();
		// Add new tab to gear window.
		configurationPanel.add(createVOWLPanel());
		return configurationPanel;
	}

	/**
	 * @return New panel for the information tab in the gear window.
	 */
	protected Panel createVOWLPanel() {
		Panel infoPanel = new Panel();
		//infoPanel.setTitle("Info");
		infoPanel.setPaddings(10);
		infoPanel.add(new Label("Visualized with WebVOWL 0.4.0 ("));
		infoPanel.add(new Anchor("VOWL homepage", "http://vowl.visualdataweb.org/", "_blank"));
		infoPanel.add(new Label(")"));

		return infoPanel;
	}

	/* ---- Selectable implementation methods by Karl Hammar ----*/
	@Override
	public void notifySelectionListeners(final SelectionEvent selectionEvent) {
		for (SelectionListener listener: listeners) {
			listener.selectionChanged(new SelectionEvent(this));
		}
	}

	@Override
	public void addSelectionListener(SelectionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeSelectionListener(SelectionListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void setSelection(Collection<? extends Object> selection) {
		// We don't allow external sources to modify the selection of this portlet.
	}

	@Override
	public Collection<? extends Object> getSelection() {
		if (selectedElement.isPresent())
			return Arrays.asList(selectedElement.get());
		else return Collections.emptyList();
	}

	/* ---- Loadable implementation methods ----*/
	@Override
	public void addGraphListener(GraphListener listener) {
		graphListeners.add(listener);
	}

	@Override
	public void notifyGraphListeners(GraphLoadedEvent event) {
		for (GraphListener listener: graphListeners) {
			listener.graphLoaded(new GraphLoadedEvent(this));
		}

	}

	@Override
	public void removeGraphListener(GraphListener listener) {
		graphListeners.remove(listener);

	}

	@Override
	public VOWLVisualizationJso getVisualizationObject() {
		return visualizationJso;
	}

	private void setDetailsContent(VOWLNodeJso node) {
		detailsDynamicPanel = new VerticalPanel();
		detailsDynamicPanel.setSpacing(4);


		detailsDynamicPanel.add(new HTML("Name: <a href=\""+node.getIri()+"\">"+node.getLabel()+"</a>"));
		detailsDynamicPanel.add(new Label("Type: "+node.getType()));

		if(node.getIndividuals() != null) {
			String indStr = "";

			for(int i=0; i<node.getIndividuals().length(); i++)  {

				indStr += "<a href=\""+node.getIndividuals().get(i).getIri()+"\">"+node.getIndividuals().get(i).getLabel()+"</a> ";
			}
			if(!indStr.isEmpty())
				detailsDynamicPanel.add(new HTML("Individuals: "+ indStr));
		}

		String charStr = node.getCharacteristics();
		if(!charStr.isEmpty())
			detailsDynamicPanel.add(new Label("Char.: "+ charStr));
		String comment = node.getComment();
		if(!comment.isEmpty())
			detailsDynamicPanel.add(new Label("Comment: "+comment));
		if(node.getAnnotations("term_status") != null) {
			String termStr = node.getAnnotations("term_status").getAnnotationProperty("value");
			if(!termStr.isEmpty())
				detailsDynamicPanel.add(new Label("term_status: "+ termStr));
		}


	}

	private void setDetailsContent(VOWLLabelJso label) {
		detailsDynamicPanel = new VerticalPanel();
		detailsDynamicPanel.setSpacing(4);

		detailsDynamicPanel.add(new HTML("Name: <a href=\""+label.getIri()+"\">"+label.getLabel()+"</a>"));
		detailsDynamicPanel.add(new Label("Type: "+label.getType()));

		if(label.getInverse() != null)
			detailsDynamicPanel.add(new HTML("Inverse: <a href=\""+label.getInverse().getIri() + "\">" + label.getInverse().getLabel()+"</a>"));

		detailsDynamicPanel.add(new HTML("Domain: <a href=\""+label.getDomain().getIri()+"\">"+label.getDomain().getLabel()+"</a>"));
		detailsDynamicPanel.add(new HTML("Range: <a href=\""+label.getRange().getIri()+"\">"+label.getRange().getLabel()+"</a>"));

		if(label.getCardinality() != null)
			detailsDynamicPanel.add(new Label("Cardinality: "+label.getCardinality()));


		if(label.getSubproperties() != null) {
			String subs="";
			for(int i=0; i<label.getSubproperties().length(); i++) {
				subs += "<a href=\""+label.getSubproperties().get(i).getIri() + "\">" + label.getSubproperties().get(i).getLabel()+"</a> ";
			}
			if(!subs.isEmpty())
				detailsDynamicPanel.add(new HTML("Subprop.:"+subs));
		}
		if(label.getSuperproperties() != null) {
			String sups="";
			for(int i=0; i<label.getSuperproperties().length(); i++) {
				sups += "<a href=\""+label.getSuperproperties().get(i).getIri() + "\">" + label.getSuperproperties().get(i).getLabel()+"</a> ";
			}
			if(!sups.isEmpty())
				detailsDynamicPanel.add(new HTML("Superprop.:"+sups));
		}

		String charStr = label.getCharacteristics();
		if(!charStr.isEmpty())
			detailsDynamicPanel.add(new Label("Char.: "+ charStr));
		String comment = label.getComment();
		if(!comment.isEmpty())
			detailsDynamicPanel.add(new Label("Comment: "+comment));
		if(label.getAnnotations("term_status") != null) {
			String termStr = label.getAnnotations("term_status").getAnnotationProperty("value");
			if(!termStr.isEmpty())
				detailsDynamicPanel.add(new Label("term_status: "+ termStr));
		}
	}

	@Override
	public VerticalPanel getPanel() {
		return detailsDynamicPanel;
	}

	class MyClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			{
				Element element=  event.getNativeEvent().getEventTarget().cast();
				if(element.getTagName().equals("circle") || element.getTagName().equals("rect")) {
					com.google.gwt.user.client.Element gElement = (com.google.gwt.user.client.Element)element.cast();
					selectedElement = Optional.of(gElement.getParentElement().getId());
					// classes have value 'node', while properties have value 'property' 
					elementType = Optional.of(gElement.getParentElement().getAttribute("class"));

					if(elementType.get().equals("node")) {
						if(visualizationJso.getSelectedNode()!=null) {
							setDetailsContent(visualizationJso.getSelectedNode());
							notifySelectionListeners(new SelectionEvent(VOWLVisualizationPortlet.this));
						}
					}
					else {//if(elementType.get().equals("label") && visualizationJso.getSelectedLabel()!=null) 
						if(visualizationJso.getSelectedLabel()!=null) {
							setDetailsContent(visualizationJso.getSelectedLabel());
							notifySelectionListeners(new SelectionEvent(VOWLVisualizationPortlet.this));
						}
					}
				}
				//event.stopPropagation();
			}

		}

	}

	@Override
	public void isChanged(ChangedEvent event) {

		Collection<? extends Object> changedSelection = event.getChangeable().getChange();

		if (changedSelection.size() > 0) {
			Object selectionData = changedSelection.iterator().next();
			//GWT.log("[VOWL] Visualization Selection in the drop down list is changed");
			if (selectionData instanceof String) {
				String selectedEntity = (String) selectionData;
				GWT.log("[VOWL] Visualization Selection in the drop down list is changed: "+ selectedEntity);
				visualizationJso.setLanguage(selectedEntity);
				onRefresh();
				// TODO render the updated graph view.renderDetailsDynamicInfo(event.getSelectable().getPanel(), "<h3>Selection Details</h3>");
			}
		}
	}

	@Override
	public void selectionChanged(SelectionEvent event) {
		Collection<? extends Object> changedSelection = event.getSelectable().getSelection();

		if (changedSelection.size() == 1) {

			Object selection = changedSelection.iterator().next();
			if(selection instanceof String) {
				if(selection.equals("reset")) {
					visualizationJso.reset();
					//onRefresh();
				}
				else if(selection.equals("pickPin")) {
					visualizationJso.togglePickAndPin();
				}
				else {;}
			}
			else {;}
		}else {
			String[] array = (String[]) changedSelection.toArray();
			if(array[0].equals("classDistance")) {
				visualizationJso.classDistance(Integer.valueOf(array[1]));
			}
			else if(array[0].equals("datatypeDistance")) {
				visualizationJso.datatypeDistance(Integer.valueOf(array[1]));
			}
			else if(array[0].equals("collapseDegree")) {
				visualizationJso.collapsingDegree(Integer.valueOf(array[1]));
			}
			else if(array[0].equals("pause")) {
				if(array[1].equals("true"))
					visualizationJso.pause();
				else
					visualizationJso.unpause();
			}
			else {;}

		}


	}

}
