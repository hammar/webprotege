package edu.stanford.bmir.protege.web.server.xd;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import javax.inject.Inject;

import org.springframework.web.client.RestTemplate;

import com.karlhammar.xdpservices.data.CodpDetails;

import edu.stanford.bmir.protege.web.server.dispatch.ActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.NullValidator;
import edu.stanford.bmir.protege.web.server.logging.WebProtegeLogger;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetOdpsByCategoryAction;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpsByCategoryResult;

public class GetOdpsByCategoryHandler implements ActionHandler<GetOdpsByCategoryAction,GetOdpsByCategoryResult> {

	private final WebProtegeLogger log;
	private String XdpServiceUriBase;
	
	@Inject
	public GetOdpsByCategoryHandler(WebProtegeLogger logger) {
		super();
		this.log = logger;
		try {
			Properties XdServiceProperties = new Properties();
			XdServiceProperties.load(GetOdpsByCategoryHandler.class.getResourceAsStream("XdpService.properties"));
			XdpServiceUriBase = XdServiceProperties.getProperty("XdpServiceUriBase");
		} 
		catch (IOException e) {
			XdpServiceUriBase = "http://localhost:8080";
			log.info(String.format("Unable to find configuration file %s. Setting XdpServiceUriBase to default value %s.", "xdService.properties", XdpServiceUriBase));
		}
	}
	
	@Override
	public Class<GetOdpsByCategoryAction> getActionClass() {
		return GetOdpsByCategoryAction.class;
	}

	@Override
	public RequestValidator<GetOdpsByCategoryAction> getRequestValidator(GetOdpsByCategoryAction action, RequestContext requestContext) {
		return NullValidator.get();
	}

	@Override
	public GetOdpsByCategoryResult execute(GetOdpsByCategoryAction action, ExecutionContext executionContext) {
		String category = action.getCategory();
		RestTemplate restTemplate = new RestTemplate();
		String queryUri = String.format("%s/retrieve/odpMetadataByCategory?category=%s", XdpServiceUriBase, category);
		CodpDetails[] results = restTemplate.getForObject(queryUri, CodpDetails[].class); 
		return new GetOdpsByCategoryResult(Arrays.asList(results));
	}
}
