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
import edu.stanford.bmir.protege.web.shared.xd.actions.PersistInstantiationAction;
import edu.stanford.bmir.protege.web.shared.xd.results.PersistInstantiationResult;

public class PersistInstantiationHandler extends AbstractProjectChangeHandler<OWLEntity, PersistInstantiationAction,PersistInstantiationResult> {
	
	private final XdpLogger xdpLog;
	
	@Inject
	public PersistInstantiationHandler(OWLAPIProjectManager projectManager) {
		super(projectManager);
		this.xdpLog = XdpLogger.getInstance();
	}

	@Override
	public Class<PersistInstantiationAction> getActionClass() {
		return PersistInstantiationAction.class;
	}

	@Override
	protected ChangeListGenerator<OWLEntity> getChangeListGenerator(PersistInstantiationAction action, 
			OWLAPIProject project,ExecutionContext executionContext) {
		return new OdpInstantiationChangeListGenerator(action.getOdpInstantiation());
	}

	@Override
	protected ChangeDescriptionGenerator<OWLEntity> getChangeDescription(PersistInstantiationAction action, 
			OWLAPIProject project,ExecutionContext executionContext) {
		return new FixedMessageChangeDescriptionGenerator<OWLEntity>(OWLMessageFormatter.formatMessage("Instantiated the Ontology Design Pattern {0}", project, action.getOdpInstantiation().getOdpIri().toString()));
	}

	@Override
	protected PersistInstantiationResult createActionResult(ChangeApplicationResult<OWLEntity> changeApplicationResult,
			PersistInstantiationAction action, OWLAPIProject project, ExecutionContext executionContext,
			EventList<ProjectEvent<?>> eventList) {
		
		// Log alignments used, for later analysis
		xdpLog.logUsedOdpAlignments(executionContext.getUserId(), project, action.getOdpInstantiation().getAlignments());
		
		return new PersistInstantiationResult(eventList);
	}

	@Override
	protected RequestValidator<PersistInstantiationAction> getAdditionalRequestValidator(PersistInstantiationAction action, RequestContext requestContext) {
		return new UserHasProjectWritePermissionValidator<PersistInstantiationAction>();
	}
}
