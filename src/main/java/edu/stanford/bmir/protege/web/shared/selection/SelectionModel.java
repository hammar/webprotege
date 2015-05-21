package edu.stanford.bmir.protege.web.shared.selection;

import com.google.common.base.Optional;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;
import edu.stanford.bmir.protege.web.shared.entity.*;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 15/05/15
 */
public class SelectionModel {

    public static final Void VOID = null;

    private final EventBus eventBus;

    private final SelectedEntityDataManager<OWLClassData> selectedClassDataManager;

    private final SelectedEntityDataManager<OWLObjectPropertyData> selectedObjectPropertyDataManager;

    private final SelectedEntityDataManager<OWLDataPropertyData> selectedDataPropertyDataManager;

    private final SelectedEntityDataManager<OWLAnnotationPropertyData> selectedAnnotationPropertyDataManager;

    private final SelectedEntityDataManager<OWLDatatypeData> selectedDatatypeDataManager;

    private final SelectedEntityDataManager<OWLNamedIndividualData> selectedIndividualDataManager;

    private Optional<OWLEntityData> selection = Optional.absent();

    @Inject
    public SelectionModel(EventBus eventBus,
                          SelectedEntityDataManager<OWLClassData> selectedClassDataManager,
                          SelectedEntityDataManager<OWLObjectPropertyData> selectedObjectPropertyDataManager,
                          SelectedEntityDataManager<OWLDataPropertyData> selectedDataPropertyDataManager,
                          SelectedEntityDataManager<OWLAnnotationPropertyData> selectedAnnotationPropertyDataManager,
                          SelectedEntityDataManager<OWLDatatypeData> selectedDatatypeDataManager,
                          SelectedEntityDataManager<OWLNamedIndividualData> selectedIndividualDataManager) {
        this.eventBus = eventBus;
        this.selectedClassDataManager = checkNotNull(selectedClassDataManager);
        this.selectedObjectPropertyDataManager = checkNotNull(selectedObjectPropertyDataManager);
        this.selectedDataPropertyDataManager = checkNotNull(selectedDataPropertyDataManager);
        this.selectedAnnotationPropertyDataManager = checkNotNull(selectedAnnotationPropertyDataManager);
        this.selectedDatatypeDataManager = checkNotNull(selectedDatatypeDataManager);
        this.selectedIndividualDataManager = checkNotNull(selectedIndividualDataManager);
    }

    public HandlerRegistration addSelectionChangedHandler(EntityDataSelectionChangedHandler handler) {
        return eventBus.addHandler(EntityDataSelectionChangedEvent.getType(), handler);
    }

    public Optional<OWLEntityData> getSelection() {
        return selection;
    }

    public Optional<OWLClassData> getLastSelectedClassData() {
        return selectedClassDataManager.getLastSelection();
    }

    public Optional<OWLObjectPropertyData> getLastSelectedObjectPropertyData() {
        return selectedObjectPropertyDataManager.getLastSelection();
    }

    public Optional<OWLDataPropertyData> getLastSelectedDataPropertyData() {
        return selectedDataPropertyDataManager.getLastSelection();
    }

    public Optional<OWLAnnotationPropertyData> getLastSelectedAnnotationPropertyData() {
        return selectedAnnotationPropertyDataManager.getLastSelection();
    }

    public Optional<OWLDatatypeData> getLastSelectedDatatypeData() {
        return selectedDatatypeDataManager.getLastSelection();
    }

    public Optional<OWLNamedIndividualData> getLastSelectedNamedIndividualData() {
        return selectedIndividualDataManager.getLastSelection();
    }

    public void setSelection(OWLEntityData entityData) {
        GWT.log("Request to set selection in selection model: " + entityData);
        Optional<OWLEntityData> previousSelection = selection;
        selection = Optional.<OWLEntityData>of(entityData);
        entityData.accept(new OWLEntityDataVisitorEx<Void>() {
            @Override
            public Void visit(OWLClassData data) {
                selectedClassDataManager.setSelection(data);
                return VOID;
            }

            @Override
            public Void visit(OWLObjectPropertyData data) {
                selectedObjectPropertyDataManager.setSelection(data);
                return VOID;
            }

            @Override
            public Void visit(OWLDataPropertyData data) {
                selectedDataPropertyDataManager.setSelection(data);
                return VOID;
            }

            @Override
            public Void visit(OWLAnnotationPropertyData data) {
                selectedAnnotationPropertyDataManager.setSelection(data);
                return VOID;
            }

            @Override
            public Void visit(OWLNamedIndividualData data) {
                selectedIndividualDataManager.setSelection(data);
                return VOID;
            }

            @Override
            public Void visit(OWLDatatypeData data) {
                selectedDatatypeDataManager.setSelection(data);
                return VOID;
            }
        });
        if (!previousSelection.equals(selection)) {
            fireEvent(previousSelection);
        }
    }


    private void fireEvent(Optional<OWLEntityData> previousLastSelection) {
        eventBus.fireEvent(new EntityDataSelectionChangedEvent(previousLastSelection, selection));
    }


    public static SelectionModel create() {
        EventBus selectionEventBus = new SimpleEventBus();
        return new SelectionModel(
                selectionEventBus,
                new SelectedEntityDataManager<OWLClassData>(),
                new SelectedEntityDataManager<OWLObjectPropertyData>(),
                new SelectedEntityDataManager<OWLDataPropertyData>(),
                new SelectedEntityDataManager<OWLAnnotationPropertyData>(),
                new SelectedEntityDataManager<OWLDatatypeData>(),
                new SelectedEntityDataManager<OWLNamedIndividualData>()
        );
    }
}
