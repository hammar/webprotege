package edu.stanford.bmir.protege.web.client.change;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.ui.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.ui.library.msgbox.YesNoHandler;
import edu.stanford.bmir.protege.web.shared.TimeUtil;
import edu.stanford.bmir.protege.web.shared.change.*;
import edu.stanford.bmir.protege.web.shared.diff.DiffElement;
import edu.stanford.bmir.protege.web.shared.entity.OWLEntityData;
import edu.stanford.bmir.protege.web.shared.pagination.Page;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.revision.RevisionNumber;
import edu.stanford.bmir.protege.web.shared.user.UserId;
import org.semanticweb.owlapi.model.OWLEntity;

import java.util.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 26/02/15
 */
public class ChangeListViewPresenter {

    private ChangeListView changeListView;

    private DispatchServiceManager dispatchServiceManager;

    private boolean revertChangesVisible = false;

    private Optional<ProjectId> projectId = Optional.absent();

    public ChangeListViewPresenter(ChangeListView changeListView, DispatchServiceManager dispatchServiceManager, boolean revertChangesVisible) {
        this.changeListView = changeListView;
        this.dispatchServiceManager = dispatchServiceManager;
        this.revertChangesVisible = revertChangesVisible;
    }

    public void setChangesForProject(ProjectId projectId) {
        this.projectId = Optional.of(projectId);
        changeListView.clear();
        dispatchServiceManager.execute(new GetProjectChangesAction(projectId, Optional.<OWLEntity>absent()), new DispatchServiceCallback<GetProjectChangesResult>() {
            @Override
            public void handleSuccess(GetProjectChangesResult result) {
                fillView(result.getChanges(), SubjectDisplay.DISPLAY_SUBJECT);
            }
        });
    }

    public void setChangesForEntity(ProjectId projectId, OWLEntity entity) {
        this.projectId = Optional.of(projectId);
        changeListView.clear();
        dispatchServiceManager.execute(new GetProjectChangesAction(projectId, Optional.of(entity)), new DispatchServiceCallback<GetProjectChangesResult>() {
            @Override
            public void handleSuccess(GetProjectChangesResult result) {
                fillView(result.getChanges(), SubjectDisplay.DO_NOT_DISPLAY_SUBJECT);
            }
        });
    }

    public void setChangesForWatches(ProjectId projectId, UserId userId) {
        this.projectId = Optional.of(projectId);
        changeListView.clear();
        dispatchServiceManager.execute(new GetWatchedEntityChangesAction(projectId, userId), new DispatchServiceCallback<GetWatchedEntityChangesResult>() {
            @Override
            public void handleSuccess(GetWatchedEntityChangesResult result) {
                fillView(result.getChanges(), SubjectDisplay.DISPLAY_SUBJECT);
            }
        });
    }

    private void fillView(ImmutableList<ProjectChange> changes, SubjectDisplay subjectDisplay) {
        changeListView.clear();
        List<ProjectChange> projectChanges = new ArrayList<>(changes);
        Collections.sort(projectChanges, Ordering.compound(Arrays.asList(
//                new ProjectChangeSubjectsComparator(),
                Ordering.from(new ProjectChangeTimestampComparator()).reverse())));
        long previousTimeStamp = 0;
        for(final ProjectChange projectChange : projectChanges) {
            long changeTimeStamp = projectChange.getTimestamp();
            if(!TimeUtil.isSameCalendarDay(previousTimeStamp, changeTimeStamp)) {
                previousTimeStamp = changeTimeStamp;
                Date date = new Date(changeTimeStamp);
                changeListView.addSeparator("\u25C9   Changes on " + DateTimeFormat.getFormat("EEE, d MMM yyyy").format(date));
            }

            ChangeDetailsView view = new ChangeDetailsViewImpl();
            if (subjectDisplay == SubjectDisplay.DISPLAY_SUBJECT) {
                GWT.log(projectChange.getSubjects().toString());
                List<OWLEntityData> subjects = new ArrayList<>(projectChange.getSubjects());
                Collections.sort(subjects, new Comparator<OWLEntityData>() {
                    @Override
                    public int compare(OWLEntityData o1, OWLEntityData o2) {
                        return o1.compareToIgnoreCase(o2);
                    }
                });
                view.setSubjects(subjects);
            }
            view.setRevision(projectChange.getRevisionNumber());
            view.setAuthor(projectChange.getAuthor());
            view.setHighLevelDescription(projectChange.getSummary());
            view.setRevertRevisionVisible(revertChangesVisible);
            view.setRevertRevisionHandler(new RevertRevisionHandler() {
                @Override
                public void handleRevertRevision() {
                    ChangeListViewPresenter.this.handleRevertRevision(projectChange);
                }
            });
            Page<DiffElement<String, SafeHtml>> page = projectChange.getDiff();
            List<DiffElement<String, SafeHtml>> pageElements = page.getPageElements();
            if (page.getPageCount() == 1) {
                view.setDiff(pageElements);
            }
            view.setChangeCount(projectChange.getChangeCount());
            view.setTimestamp(changeTimeStamp);
            changeListView.addChangeDetailsView(view);
        }
    }

    private void handleRevertRevision(final ProjectChange projectChange) {
        startRevertChangesWorkflow(projectChange);
    }

    private void startRevertChangesWorkflow(final ProjectChange projectChange) {
        MessageBox.showYesNoConfirmBox("Revert changes?", "Are you sure that you want to revert the changes in Revision " + projectChange.getRevisionNumber().getValue() + "?", new YesNoHandler() {
            @Override
            public void handleYes() {
                revertChanges(projectChange);
            }

            @Override
            public void handleNo() {

            }
        });
    }

    private void revertChanges(ProjectChange projectChange) {
        GWT.log("Reverting revision " + projectChange.getRevisionNumber().getValue());
        if(!projectId.isPresent()) {
            return;
        }
        final RevisionNumber revisionNumber = projectChange.getRevisionNumber();
        dispatchServiceManager.execute(new RevertRevisionAction(projectId.get(), revisionNumber), new DispatchServiceCallback<RevertRevisionResult>() {
            @Override
            public void handleSuccess(RevertRevisionResult revertRevisionResult) {
                MessageBox.showMessage("Changes in revision " + revisionNumber.getValue() + " have been reverted");
            }
        });
    }

}
