package edu.stanford.bmir.protege.web.client.metrics;

import com.google.gwt.core.client.GWT;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;
import edu.stanford.bmir.protege.web.shared.selection.SelectionModel;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 26/04/2014
 */
public class MetricsPortlet extends AbstractOWLEntityPortlet {

    private MetricsPresenter metricsPresenter;

    private MetricsView view;

    public MetricsPortlet(SelectionModel selectionModel, Project project) {
        super(selectionModel, project);
    }

    @Override
    public void initialize() {
        view = new MetricsViewImpl();
        add(view.asWidget());
        metricsPresenter = new MetricsPresenter(getProjectId(), view, DispatchServiceManager.get());
        metricsPresenter.bind(this);
        setHeight(500);
        updateDisplay();
    }

    private void updateDisplay() {
        if(metricsPresenter == null) {
            GWT.log("MetricsPresenter is not initialized");
        }
        setTitle("Metrics");
        metricsPresenter.reload();
    }

}
