package edu.stanford.bmir.protege.web.client.ui.frame;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import edu.stanford.bmir.protege.web.client.primitive.PrimitiveDataListEditor;
import edu.stanford.bmir.protege.web.client.renderer.RenderingManager;
import edu.stanford.bmir.protege.web.client.ui.editor.EditorView;
import edu.stanford.bmir.protege.web.resources.WebProtegeClientBundle;
import edu.stanford.bmir.protege.web.shared.DataFactory;
import edu.stanford.bmir.protege.web.shared.DirtyChangedEvent;
import edu.stanford.bmir.protege.web.shared.DirtyChangedHandler;
import edu.stanford.bmir.protege.web.shared.PrimitiveType;
import edu.stanford.bmir.protege.web.shared.entity.OWLClassData;
import edu.stanford.bmir.protege.web.shared.entity.OWLDatatypeData;
import edu.stanford.bmir.protege.web.shared.entity.OWLEntityData;
import edu.stanford.bmir.protege.web.shared.entity.OWLPrimitiveData;
import edu.stanford.bmir.protege.web.shared.frame.DataPropertyFrame;
import edu.stanford.bmir.protege.web.shared.frame.PropertyValueList;
import edu.stanford.bmir.protege.web.shared.mail.GetEmailAddressResult;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.renderer.GetEntityDataAction;
import edu.stanford.bmir.protege.web.shared.renderer.GetEntityDataResult;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 23/04/2013
 */
public class DataPropertyFrameEditor extends Composite implements EditorView<LabelledFrame<DataPropertyFrame>>, HasEnabled {

    interface DataPropertyFrameEditorUiBinder extends UiBinder<HTMLPanel, DataPropertyFrameEditor> {

    }

    private static DataPropertyFrameEditorUiBinder ourUiBinder = GWT.create(DataPropertyFrameEditorUiBinder.class);

    @UiField
    protected TextBox displayNameField;

    @UiField
    protected TextBox iriField;

    @UiField(provided = true)
    protected final PropertyValueListEditor annotations;

    @UiField(provided = true)
    final PrimitiveDataListEditor domains;

    @UiField(provided = true)
    final PrimitiveDataListEditor ranges;

    @UiField
    protected CheckBox functionalCheckBox;

    private boolean enabled;

    private boolean dirty = false;

    private ProjectId projectId;

    public DataPropertyFrameEditor(ProjectId projectId) {
        this.projectId = projectId;

        annotations = new PropertyValueListEditor(projectId);
        annotations.setGrammar(PropertyValueGridGrammar.getAnnotationsGrammar());
        domains = new PrimitiveDataListEditor(PrimitiveType.CLASS);
        ranges = new PrimitiveDataListEditor(PrimitiveType.DATA_TYPE);
        WebProtegeClientBundle.BUNDLE.style().ensureInjected();
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
        iriField.setEnabled(false);
        setEnabled(false);
    }

    @UiHandler("displayNameField")
    protected void handleDisplayNameChanged(ValueChangeEvent<String> event) {
        fireValueChangedIfWellFormed();
    }



    @UiHandler("annotations")
    protected void handleAnnotationsChanged(ValueChangeEvent<Optional<PropertyValueList>> event) {
        fireValueChangedIfWellFormed();
    }

    @UiHandler("domains")
    protected void handleDomainsChanged(ValueChangeEvent<Optional<List<OWLPrimitiveData>>> event) {
        fireValueChangedIfWellFormed();
    }


    @UiHandler("ranges")
    protected void handleRangesChanged(ValueChangeEvent<Optional<List<OWLPrimitiveData>>> event) {
        fireValueChangedIfWellFormed();
    }

    @UiHandler("functionalCheckBox")
    protected void handleFunctionalCheckBoxChanged(ValueChangeEvent<Boolean> event) {
        fireValueChangedIfWellFormed();
    }





    private void fireValueChangedIfWellFormed() {
        if(isWellFormed()) {
            dirty = true;
            ValueChangeEvent.fire(this, getValue());
        }
    }

    @Override
    public Widget getWidget() {
        return this;
    }

    @Override
    public void setValue(LabelledFrame<DataPropertyFrame> object) {
        dirty = false;
        displayNameField.setText(object.getDisplayName());
        final DataPropertyFrame frame = object.getFrame();
        iriField.setText(frame.getSubject().getIRI().toString());
        annotations.setValue(frame.getPropertyValueList());
        RenderingManager.getManager().execute(new GetEntityDataAction(projectId, ImmutableSet.<OWLEntity>copyOf(frame.getDomains())), new AsyncCallback<GetEntityDataResult>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(GetEntityDataResult result) {
                List<OWLPrimitiveData> primitiveDatas = new ArrayList<OWLPrimitiveData>();
                for (OWLClass cls : frame.getDomains()) {
                    final Optional<OWLEntityData> entityData = Optional.fromNullable(result.getEntityDataMap().get(cls));
                    if (entityData.isPresent()) {
                        primitiveDatas.add(entityData.get());
                    }
                }
                domains.setValue(primitiveDatas);
            }
        });
        RenderingManager.getManager().execute(new GetEntityDataAction(projectId, ImmutableSet.<OWLEntity>copyOf(frame.getRanges())), new AsyncCallback<GetEntityDataResult>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(GetEntityDataResult result) {
                List<OWLPrimitiveData> primitiveDatas = new ArrayList<OWLPrimitiveData>();
                for (OWLDatatype dt : frame.getRanges()) {
                    final Optional<OWLEntityData> entityData = Optional.of(result.getEntityDataMap().get(dt));
                    if (entityData.isPresent()) {
                        primitiveDatas.add(entityData.get());
                    }
                }
                ranges.setValue(primitiveDatas);
            }
        });
        functionalCheckBox.setValue(frame.isFunctional());


    }

    @Override
    public void clearValue() {
        displayNameField.setText("");
        iriField.setText("");
        annotations.clearValue();
        domains.clearValue();
        ranges.clearValue();
    }

    @Override
    public Optional<LabelledFrame<DataPropertyFrame>> getValue() {
        OWLDataProperty property = DataFactory.getOWLDataProperty(getIRIString());
        final Set<OWLClass> domainsClasses = Sets.newHashSet();
        if (domains.getValue().isPresent()) {
            for(OWLPrimitiveData primitiveData : domains.getValue().get()) {
                domainsClasses.add(((OWLClassData) primitiveData).getEntity());
            }
        }
        final Set<OWLDatatype> rangeTypes = Sets.newHashSet();
        if (ranges.getValue().isPresent()) {
            for(OWLPrimitiveData primitiveData : ranges.getValue().get()) {
                rangeTypes.add(((OWLDatatypeData) primitiveData).getEntity());
            }
        }
        DataPropertyFrame frame = new DataPropertyFrame(property, annotations.getValue().get(), domainsClasses, rangeTypes, functionalCheckBox.getValue());
        return Optional.of(new LabelledFrame<DataPropertyFrame>(getDisplayName(), frame));
    }

    @Override
    public boolean isDirty() {
        return dirty || annotations.isDirty() || domains.isDirty() || ranges.isDirty();
    }

    @Override
    public HandlerRegistration addDirtyChangedHandler(DirtyChangedHandler handler) {
        return addHandler(handler, DirtyChangedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Optional<LabelledFrame<DataPropertyFrame>>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public boolean isWellFormed() {
        return !getDisplayName().isEmpty() && !getIRIString().isEmpty() && annotations.isWellFormed() && domains.isWellFormed() && ranges.isWellFormed();
    }

    private String getIRIString() {
        return iriField.getText().trim();
    }

    private String getDisplayName() {
        return displayNameField.getText().trim();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        displayNameField.setEnabled(enabled);
        annotations.setEnabled(enabled);
        domains.setEnabled(enabled);
        ranges.setEnabled(enabled);
        functionalCheckBox.setEnabled(enabled);
    }
}
