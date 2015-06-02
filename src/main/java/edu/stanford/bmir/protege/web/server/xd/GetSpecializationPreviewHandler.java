package edu.stanford.bmir.protege.web.server.xd;

import edu.stanford.bmir.protege.web.server.dispatch.ActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.NullValidator;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetSpecializationPreviewAction;
import edu.stanford.bmir.protege.web.shared.xd.results.GetSpecializationPreviewResult;

public class GetSpecializationPreviewHandler implements ActionHandler<GetSpecializationPreviewAction,GetSpecializationPreviewResult> {
	
	@Override
	public Class<GetSpecializationPreviewAction> getActionClass() {
		return GetSpecializationPreviewAction.class;
	}

	@Override
	public RequestValidator<GetSpecializationPreviewAction> getRequestValidator(
			GetSpecializationPreviewAction action, RequestContext requestContext) {
		return NullValidator.get();
	}

	@Override
	public GetSpecializationPreviewResult execute(GetSpecializationPreviewAction action,
			ExecutionContext executionContext) {
		
		// TODO: Actually implement this.
		String dummyResponse = "<#green-goblin>\n" + 
				"    rel:enemyOf <#spiderman> ;\n" + 
				"    a foaf:Person ;    # in the context of the Marvel universe\n" +
				"    foaf:name \"Green Goblin\" .\n" + 
				"\n" +
				"<#spiderman>\n" +
				"    rel:enemyOf <#green-goblin> ;\n" + 
				"    a foaf:Person ;\n" +
				"    foaf:name \"Spiderman\", \"Человек-паук\"@ru .";
		
		GetSpecializationPreviewResult result = new GetSpecializationPreviewResult(dummyResponse);
		return result;
	}
}
