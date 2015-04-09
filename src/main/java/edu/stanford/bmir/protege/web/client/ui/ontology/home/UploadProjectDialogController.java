package edu.stanford.bmir.protege.web.client.ui.ontology.home;

import com.google.common.base.Optional;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Widget;
import edu.stanford.bmir.protege.web.client.Application;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallbackWithProgressDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.rpc.data.DocumentId;
import edu.stanford.bmir.protege.web.client.rpc.data.NewProjectSettings;
import edu.stanford.bmir.protege.web.client.rpc.data.NotSignedInException;
import edu.stanford.bmir.protege.web.client.rpc.data.ProjectType;
import edu.stanford.bmir.protege.web.client.ui.library.dlg.*;
import edu.stanford.bmir.protege.web.client.ui.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.ui.projectmanager.ProjectCreatedEvent;
import edu.stanford.bmir.protege.web.client.ui.upload.FileUploadResponse;
import edu.stanford.bmir.protege.web.client.ui.util.UIUtil;
import edu.stanford.bmir.protege.web.shared.event.EventBusManager;
import edu.stanford.bmir.protege.web.shared.project.*;
import edu.stanford.bmir.protege.web.shared.user.UserId;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 19/01/2012
 */
public class UploadProjectDialogController extends WebProtegeOKCancelDialogController<UploadFileInfo> {

    private static final String TITLE = "Upload ontology";

    private UploadFileWidget uploadFileWidget = new UploadFileWidget();

    public static final String PROGRESS_DIALOG_TITLE = "Uploading project";

    public UploadProjectDialogController() {
        super(TITLE);
        for (WebProtegeDialogValidator validator : uploadFileWidget.getDialogValidators()) {
            addDialogValidator(validator);
        }
        setDialogButtonHandler(DialogButton.OK, new WebProtegeDialogButtonHandler<UploadFileInfo>() {
            public void handleHide(final UploadFileInfo data, final WebProtegeDialogCloser closer) {
                UIUtil.showLoadProgessBar(PROGRESS_DIALOG_TITLE, "Uploading file");
                data.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
                    public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
                        UIUtil.hideLoadProgessBar();
                        handleSubmissionComplete(data, event);
                        closer.hide();

                    }
                });
                data.submit();
            }
        });
    }


    private void handleSubmissionComplete(UploadFileInfo data, FormPanel.SubmitCompleteEvent event) {
        FileUploadResponse result = new FileUploadResponse(event.getResults());
        if (result.wasUploadAccepted()) {
            createProjectFromUpload(data, result);
        }
        else {
            displayProjectUploadError(result);
        }
    }

    private void displayProjectUploadError(FileUploadResponse result) {
        MessageBox.showAlert("Upload failed", result.getUploadRejectedMessage());
    }

    private void createProjectFromUpload(UploadFileInfo data, FileUploadResponse result) {
        UIUtil.showLoadProgessBar(PROGRESS_DIALOG_TITLE, "Creating project");
        UserId userId = Application.get().getUserId();
        DocumentId documentId = result.getDocumentId();
        String projectName = data.getProjectSettings().getProjectName();
        String projectDescription = data.getProjectSettings().getProjectDescription();
        ProjectType projectType = data.getProjectSettings().getProjectType();
        NewProjectSettings newProjectSettings = new NewProjectSettings(userId, projectName, projectDescription, projectType, documentId);

        DispatchServiceManager.get().execute(new CreateNewProjectAction(newProjectSettings), new DispatchServiceCallbackWithProgressDisplay<CreateNewProjectResult>() {
            @Override
            public String getProgressDisplayTitle() {
                return "Creating project";
            }

            @Override
            public String getProgressDisplayMessage() {
                return "Please wait";
            }

            @Override
            public void handleExecutionException(Throwable caught) {
                if (caught instanceof NotSignedInException) {
                    MessageBox.showAlert("You must be signed in to create new projects.", "Please sign in.");
                } else if (caught instanceof ProjectAlreadyRegisteredException) {
                    ProjectAlreadyRegisteredException ex = (ProjectAlreadyRegisteredException) caught;
                    String projectName = ex.getProjectId().getId();
                    MessageBox.showMessage("The project name " + projectName + " is already taken.  Please try a different name.");
                } else if (caught instanceof ProjectDocumentExistsException) {
                    ProjectDocumentExistsException ex = (ProjectDocumentExistsException) caught;
                    String projectName = ex.getProjectId().getId();
                    MessageBox.showAlert("Project already exists", "There is already a non-empty project on the server which is named " + projectName + ".  This project has NOT been overwritten.  Please contact the administrator to resolve this issue.");
                } else {
                    MessageBox.showAlert(caught.getMessage());
                }
            }

            @Override
            public void handleSuccess(CreateNewProjectResult result) {
                MessageBox.showMessage("Project uploaded", "Your ontology was uploaded and the project was successfully created.");
                EventBusManager.getManager().postEvent(new ProjectCreatedEvent(result.getProjectDetails()));
            }
        });
    }

    @Override
    public Widget getWidget() {
        return uploadFileWidget;
    }

    @Override
    public Optional<Focusable> getInitialFocusable() {
        return uploadFileWidget.getInitialFocusable();
    }

    @Override
    public UploadFileInfo getData() {
        return uploadFileWidget.getUploadFileInfo();
    }
}
