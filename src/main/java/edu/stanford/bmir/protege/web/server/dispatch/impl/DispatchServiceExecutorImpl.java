package edu.stanford.bmir.protege.web.server.dispatch.impl;

import edu.stanford.bmir.protege.web.client.dispatch.ActionExecutionException;
import edu.stanford.bmir.protege.web.server.dispatch.*;
import edu.stanford.bmir.protege.web.server.dispatch.validators.UserHasProjectWritePermissionValidator;
import edu.stanford.bmir.protege.web.shared.dispatch.Action;
import edu.stanford.bmir.protege.web.shared.dispatch.DispatchServiceResultContainer;
import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.permissions.PermissionDeniedException;

import javax.inject.Inject;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 19/02/2013
 */
public class DispatchServiceExecutorImpl implements DispatchServiceExecutor {

    private final ActionHandlerRegistry handlerRegistry;

    @Inject
    public DispatchServiceExecutorImpl(ActionHandlerRegistry handlerRegistry) {
        this.handlerRegistry = handlerRegistry;
    }

    @Override
    public <A extends Action<R>, R extends Result> DispatchServiceResultContainer execute(A action, RequestContext requestContext, ExecutionContext executionContext) throws ActionExecutionException, PermissionDeniedException {
        ActionHandler<A, R> actionHandler = handlerRegistry.getActionHandler(action);
        RequestValidator<A> validator = actionHandler.getRequestValidator(action, requestContext);
        RequestValidationResult validationResult = validator.validateAction(action, requestContext);
        if(!validationResult.isValid()) {
            throw new PermissionDeniedException(validationResult.getInvalidMessage());
        }

        try {
            R result = actionHandler.execute(action, executionContext);
            return new DispatchServiceResultContainer(result);
        } catch (Exception e) {
            throw new ActionExecutionException(e);
        }
    }
}
