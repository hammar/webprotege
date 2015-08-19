package edu.stanford.bmir.protege.web.server.xd;

import javax.inject.Inject;

import org.semanticweb.owlapi.model.OWLEntity;

import edu.stanford.bmir.protege.web.server.change.ChangeApplicationResult;
import edu.stanford.bmir.protege.web.server.change.ChangeDescriptionGenerator;
import edu.stanford.bmir.protege.web.server.change.ChangeListGenerator;
import edu.stanford.bmir.protege.web.server.change.FixedMessageChangeDescriptionGenerator;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectChangeHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.UserHasProjectWritePermissionValidator;
import edu.stanford.bmir.protege.web.server.msg.OWLMessageFormatter;
import edu.stanford.bmir.protege.web.server.owlapi.OWLAPIProject;
import edu.stanford.bmir.protege.web.server.owlapi.OWLAPIProjectManager;
import edu.stanford.bmir.protege.web.server.xd.log.XdpLogger;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.events.EventList;
import edu.stanford.bmir.protege.web.shared.xd.actions.PersistSpecializationAction;
import edu.stanford.bmir.protege.web.shared.xd.results.PersistSpecializationResult;

public class PersistSpecializationHandler extends AbstractProjectChangeHandler<OWLEntity, PersistSpecializationAction,PersistSpecializationResult> {
	
	private final XdpLogger xdpLog;
	
	@Inject
	public PersistSpecializationHandler(OWLAPIProjectManager projectManager) {
		super(projectManager);
		this.xdpLog = XdpLogger.getInstance();
	}

	@Override
	public Class<PersistSpecializationAction> getActionClass() {
		return PersistSpecializationAction.class;
	}

	@Override
	protected ChangeListGenerator<OWLEntity> getChangeListGenerator(PersistSpecializationAction action, 
			OWLAPIProject project,ExecutionContext executionContext) {
		return new OdpSpecializationChangeListGenerator(action.getOdpSpecialization());
	}

	@Override
	protected ChangeDescriptionGenerator<OWLEntity> getChangeDescription(PersistSpecializationAction action, 
			OWLAPIProject project,ExecutionContext executionContext) {
		return new FixedMessageChangeDescriptionGenerator<OWLEntity>(OWLMessageFormatter.formatMessage("Specialised the Ontology Design Pattern {0}", project, action.getOdpSpecialization().getOdpIri().toString()));
	}

	@Override
	protected PersistSpecializationResult createActionResult(ChangeApplicationResult<OWLEntity> changeApplicationResult,
			PersistSpecializationAction action, OWLAPIProject project, ExecutionContext executionContext,
			EventList<ProjectEvent<?>> eventList) {
		
		// Log alignments used, for later analysis
		xdpLog.logUsedOdpAlignments(executionContext.getUserId(), project, action.getOdpSpecialization().getAlignments());
		
		return new PersistSpecializationResult(eventList);
	}

	@Override
	protected RequestValidator<PersistSpecializationAction> getAdditionalRequestValidator(PersistSpecializationAction action, RequestContext requestContext) {
		return new UserHasProjectWritePermissionValidator<PersistSpecializationAction>();
	}
}
