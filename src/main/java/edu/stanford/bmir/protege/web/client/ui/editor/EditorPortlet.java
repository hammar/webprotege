package edu.stanford.bmir.protege.web.client.ui.editor;

import com.google.common.base.Optional;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;
import edu.stanford.bmir.protege.web.shared.entity.OWLEntityData;
import edu.stanford.bmir.protege.web.shared.selection.SelectionModel;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 23/04/2013
 */
public class EditorPortlet extends AbstractOWLEntityPortlet {

    private EditorPresenter editorPresenter;

    public EditorPortlet(SelectionModel selectionModel, Project project) {
        super(selectionModel, project);
    }

    @Override
    public void initialize() {
        setTitle("Nothing selected");
        EditorContextMapper contextMapper = new EditorContextMapper();
        contextMapper.registerSelector(new EntityDataContextSelector());

        editorPresenter = new EditorPresenter(getProjectId(), contextMapper);
        editorPresenter.addEditorContextChangedHandler(new EditorContextChangedHandler() {
            @Override
            public void handleEditorContextChanged(EditorContextChangedEvent editorContextChangedEvent) {
                setTitle(editorContextChangedEvent.getContextDescription());
            }
        });

        final Widget editorHolder = editorPresenter.getEditorHolder();
//        editorHolder.setSize("100%", "100%");
        ScrollPanel sp = new ScrollPanel(editorHolder);
        sp.setSize("100%", "100%");
        add(sp);
    }

    @Override
    protected void handleAfterSetEntity(Optional<OWLEntityData> entityData) {
        GWT.log("EditorPortlet: setEntity " + entityData);
        if(!entityData.isPresent()) {
            // TODO: Show nothing selected
            return;
        }
        final Optional<EditorCtx> editorContext = getEditorContext(entityData);
        editorPresenter.setEditorContext(editorContext);
    }

    public Optional<EditorCtx> getEditorContext(Optional<OWLEntityData> sel) {
        if(!sel.isPresent()) {
            return Optional.absent();
        }
        return Optional.<EditorCtx>of(new OWLEntityDataContext(getProjectId(), sel.get()));
    }

    @Override
    protected void onDestroy() {
        editorPresenter.dispose();
    }
}
