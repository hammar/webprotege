package edu.stanford.bmir.protege.web.server.auth;

import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.NullValidator;
import edu.stanford.bmir.protege.web.server.logging.WebProtegeLogger;
import edu.stanford.bmir.protege.web.server.metaproject.AuthenticationManager;
import edu.stanford.bmir.protege.web.server.session.WebProtegeSession;
import edu.stanford.bmir.protege.web.shared.auth.*;

import javax.inject.Inject;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14/02/15
 */
public class PerformLoginActionHandler extends AuthenticatedActionHandler<PerformLoginAction, PerformLoginResult> {

    @Inject
    public PerformLoginActionHandler(ChapSessionManager chapSessionManager, AuthenticationManager authenticationManager, ChapResponseChecker chapResponseChecker, WebProtegeLogger logger) {
        super(chapSessionManager, authenticationManager, chapResponseChecker, logger);
    }

    @Override
    public Class<PerformLoginAction> getActionClass() {
        return PerformLoginAction.class;
    }

    @Override
    public RequestValidator<PerformLoginAction> getRequestValidator(PerformLoginAction action, RequestContext requestContext) {
        return NullValidator.get();
    }

    @Override
    protected PerformLoginResult executeAuthenticatedAction(PerformLoginAction action, ExecutionContext executionContext) {
        WebProtegeSession session = executionContext.getSession();
        session.setUserInSession(action.getUserId());
        return new PerformLoginResult(AuthenticationResponse.SUCCESS);
    }

    @Override
    protected PerformLoginResult createAuthenticationFailedResult() {
        return new PerformLoginResult(AuthenticationResponse.FAIL);
    }
}
