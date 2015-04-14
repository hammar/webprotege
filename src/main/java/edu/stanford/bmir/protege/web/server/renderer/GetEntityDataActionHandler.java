package edu.stanford.bmir.protege.web.server.renderer;

import com.google.common.collect.ImmutableMap;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractHasProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.UserHasProjectReadPermissionValidator;
import edu.stanford.bmir.protege.web.server.owlapi.OWLAPIProject;
import edu.stanford.bmir.protege.web.server.owlapi.OWLAPIProjectManager;
import edu.stanford.bmir.protege.web.shared.entity.OWLEntityData;
import edu.stanford.bmir.protege.web.shared.renderer.GetEntityDataAction;
import edu.stanford.bmir.protege.web.shared.renderer.GetEntityDataResult;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.inject.Inject;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 22/02/15
 */
public class GetEntityDataActionHandler extends AbstractHasProjectActionHandler<GetEntityDataAction, GetEntityDataResult> {

    @Inject
    public GetEntityDataActionHandler(OWLAPIProjectManager projectManager) {
        super(projectManager);
    }

    @Override
    public Class<GetEntityDataAction> getActionClass() {
        return GetEntityDataAction.class;
    }

    @Override
    protected RequestValidator<GetEntityDataAction> getAdditionalRequestValidator(GetEntityDataAction action, RequestContext requestContext) {
        return UserHasProjectReadPermissionValidator.get();
    }

    @Override
    protected GetEntityDataResult execute(GetEntityDataAction action, OWLAPIProject project, ExecutionContext executionContext) {
        ImmutableMap.Builder<OWLEntity, OWLEntityData> builder = ImmutableMap.builder();
        builder.putAll(project.getRenderingManager().getRendering(action.getEntities()));
        return new GetEntityDataResult(builder.build());
    }
}
