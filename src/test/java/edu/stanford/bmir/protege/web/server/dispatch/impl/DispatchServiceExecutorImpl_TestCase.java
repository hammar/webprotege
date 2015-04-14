package edu.stanford.bmir.protege.web.server.dispatch.impl;

import edu.stanford.bmir.protege.web.client.dispatch.ActionExecutionException;
import edu.stanford.bmir.protege.web.server.dispatch.*;
import edu.stanford.bmir.protege.web.server.dispatch.validators.UserHasProjectWritePermissionValidator;
import edu.stanford.bmir.protege.web.shared.HasProjectId;
import edu.stanford.bmir.protege.web.shared.dispatch.Action;
import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.permissions.PermissionDeniedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20/02/15
 */
@RunWith(MockitoJUnitRunner.class)
public class DispatchServiceExecutorImpl_TestCase<A extends Action<R>, R extends Result> {

    @Mock
    private ActionHandlerRegistry registry;

    @Mock
    private ActionHandler<A, R> actionHandler;

    @Mock
    private A action;

    @Mock
    private RequestContext requestContext;

    @Mock
    private ExecutionContext executionContext;

    @Mock
    private RequestValidator<A> requestValidator;

    private DispatchServiceExecutorImpl executor;

    @Before
    public void setUp() throws Exception {
        executor = new DispatchServiceExecutorImpl(registry);
        when(registry.getActionHandler(action)).thenReturn(actionHandler);
        when(actionHandler.getRequestValidator(action, requestContext)).thenReturn(requestValidator);
        when(requestValidator.validateAction(action, requestContext)).thenReturn(RequestValidationResult.getValid());
    }

    @Test(expected = ActionExecutionException.class)
    public void shouldThrowActionExecutionException() {
        ExecutionContext executionContext = this.executionContext;
        when(actionHandler.execute(action, executionContext)).thenThrow(new RuntimeException());
        executor.execute(action, requestContext, executionContext);
    }

    @Test(expected = ActionHandlerNotFoundException.class)
    public void shouldThrowActionHandlerNotFoundException() {
        when(registry.getActionHandler(action)).thenThrow(new ActionHandlerNotFoundException(action));
        executor.execute(action, requestContext, executionContext);
    }

    @Test(expected = PermissionDeniedException.class)
    public void shouldThrowPermissionDeniedException() {
        when(requestValidator.validateAction(action, requestContext)).thenReturn(RequestValidationResult.getInvalid("Denied"));
        executor.execute(action, requestContext, executionContext);
    }

    @Test
    public void shouldExecuteAction() {
        executor.execute(action, requestContext, executionContext);
        verify(actionHandler, times(1)).execute(action, executionContext);
    }
}
