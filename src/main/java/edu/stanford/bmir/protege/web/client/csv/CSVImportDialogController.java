package edu.stanford.bmir.protege.web.client.csv;

import com.google.common.base.Optional;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallback;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallbackWithProgressDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.rpc.data.DocumentId;
import edu.stanford.bmir.protege.web.client.ui.library.dlg.DialogButton;
import edu.stanford.bmir.protege.web.client.ui.library.dlg.WebProtegeDialogButtonHandler;
import edu.stanford.bmir.protege.web.client.ui.library.dlg.WebProtegeDialogCloser;
import edu.stanford.bmir.protege.web.client.ui.library.dlg.WebProtegeOKCancelDialogController;
import edu.stanford.bmir.protege.web.client.ui.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.shared.csv.*;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.semanticweb.owlapi.model.OWLClass;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/05/2013
 */
public class CSVImportDialogController extends WebProtegeOKCancelDialogController<CSVImportDescriptor> {

    private static final int ROW_LIMIT = 50;

    private CSVImportViewImpl csvImportView;

    private ProjectId projectId;

    private DocumentId csvDocumentId;

    private OWLClass importRoot;

    public CSVImportDialogController(ProjectId projId, DocumentId documentId, OWLClass importRootClass) {
        super("Import CSV File");
        this.projectId = projId;
        this.csvDocumentId = documentId;
        this.importRoot = importRootClass;
        csvImportView = new CSVImportViewImpl();

        DispatchServiceManager.get().execute(new GetCSVGridAction(documentId, ROW_LIMIT), new DispatchServiceCallback<GetCSVGridResult>() {
            @Override
            public void handleSuccess(GetCSVGridResult result) {
                csvImportView.setCSVGrid(result.getCSVGrid());
            }
        });

        setDialogButtonHandler(DialogButton.OK, new WebProtegeDialogButtonHandler<CSVImportDescriptor>() {
            @Override
            public void handleHide(CSVImportDescriptor data, WebProtegeDialogCloser closer) {
                DispatchServiceManager.get().execute(new ImportCSVFileAction(projectId, csvDocumentId, importRoot, data), new DispatchServiceCallbackWithProgressDisplay<ImportCSVFileResult>() {
                    @Override
                    protected String getErrorMessage(Throwable throwable) {
                        return "There was a problem importing the csv file.  Please try again.";
                    }

                    @Override
                    public void handleSuccess(ImportCSVFileResult result) {
                        MessageBox.showMessage("CSV import succeeded", result.getRowCount() + " rows were imported");
                    }

                    @Override
                    public String getProgressDisplayTitle() {
                        return "Importing CSV file";
                    }

                    @Override
                    public String getProgressDisplayMessage() {
                        return "Please wait.";
                    }
                });
                closer.hide();
            }
        });
    }

    @Override
    public Widget getWidget() {
        return csvImportView;
    }

    @Override
    public Optional<Focusable> getInitialFocusable() {
        return Optional.absent();
    }

    @Override
    public CSVImportDescriptor getData() {
        return csvImportView.getImportDescriptor().get();
    }
}
