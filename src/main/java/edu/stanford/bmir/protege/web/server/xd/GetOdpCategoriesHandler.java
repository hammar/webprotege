package edu.stanford.bmir.protege.web.server.xd;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import javax.inject.Inject;

import org.springframework.web.client.RestTemplate;

import edu.stanford.bmir.protege.web.server.dispatch.ActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.NullValidator;
import edu.stanford.bmir.protege.web.server.logging.WebProtegeLogger;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetOdpCategoriesAction;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpCategoriesResult;

public class GetOdpCategoriesHandler implements ActionHandler<GetOdpCategoriesAction,GetOdpCategoriesResult> {

	private final WebProtegeLogger log;
	private String XdpServiceUriBase;
	
	@Inject
	public GetOdpCategoriesHandler(WebProtegeLogger logger) {
		super();
		this.log = logger;
		try {
			Properties XdServiceProperties = new Properties();
			XdServiceProperties.load(GetOdpCategoriesHandler.class.getResourceAsStream("XdpService.properties"));
			XdpServiceUriBase = XdServiceProperties.getProperty("XdpServiceUriBase");
		} 
		catch (IOException e) {
			XdpServiceUriBase = "http://localhost:8080";
			log.info(String.format("Unable to find configuration file %s. Setting XdpServiceUriBase to default value %s.", "xdService.properties", XdpServiceUriBase));
		}
	}
	
	@Override
	public Class<GetOdpCategoriesAction> getActionClass() {
		return GetOdpCategoriesAction.class;
	}


	@Override
	public RequestValidator<GetOdpCategoriesAction> getRequestValidator(GetOdpCategoriesAction action, RequestContext requestContext) {
		return NullValidator.get();
	}

	@Override
	public GetOdpCategoriesResult execute(GetOdpCategoriesAction action, ExecutionContext executionContext) {
		RestTemplate restTemplate = new RestTemplate();
		String queryUri = String.format("%s/retrieve/odpCategories", XdpServiceUriBase);
		String[] results = restTemplate.getForObject(queryUri, String[].class); 
		return new GetOdpCategoriesResult(Arrays.asList(results));
	}
}
