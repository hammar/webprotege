package edu.stanford.bmir.protege.web.server.xd;

import edu.stanford.bmir.protege.web.server.dispatch.ActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.NullValidator;
import edu.stanford.bmir.protege.web.shared.xd.actions.PersistSpecializationAction;
import edu.stanford.bmir.protege.web.shared.xd.results.PersistSpecializationResult;

public class PersistSpecializationHandler implements ActionHandler<PersistSpecializationAction,PersistSpecializationResult> {
	
	@Override
	public Class<PersistSpecializationAction> getActionClass() {
		return PersistSpecializationAction.class;
	}

	@Override
	public RequestValidator<PersistSpecializationAction> getRequestValidator(
			PersistSpecializationAction action, RequestContext requestContext) {
		return NullValidator.get();
	}

	@Override
	public PersistSpecializationResult execute(PersistSpecializationAction action,
			ExecutionContext executionContext) {
		
		// TODO: Actually implement this.
		PersistSpecializationResult result = new PersistSpecializationResult(1);
		return result;
	}
}
