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
import edu.stanford.bmir.protege.web.shared.xd.OdpSearchFilterConfiguration;
import edu.stanford.bmir.protege.web.shared.xd.OdpSearchResult;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetOdpSearchHitsAction;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpSearchHitsResult;

public class GetOdpSearchHitsHandler implements ActionHandler<GetOdpSearchHitsAction,GetOdpSearchHitsResult> {

	private final WebProtegeLogger log;
	private String XdpServiceUriBase;
	
	@Inject
	public GetOdpSearchHitsHandler(WebProtegeLogger logger) {
		super();
		this.log = logger;
		try {
			Properties XdServiceProperties = new Properties();
			XdServiceProperties.load(GetOdpSearchHitsHandler.class.getResourceAsStream("XdpService.properties"));
			XdpServiceUriBase = XdServiceProperties.getProperty("XdpServiceUriBase");
		} 
		catch (IOException e) {
			XdpServiceUriBase = "http://localhost:8080";
			log.info(String.format("Unable to find configuration file %s. Setting XdpServiceUriBase to default value %s.", "xdService.properties", XdpServiceUriBase));
		}
	}
	
	@Override
	public Class<GetOdpSearchHitsAction> getActionClass() {
		return GetOdpSearchHitsAction.class;
	}

	@Override
	public RequestValidator<GetOdpSearchHitsAction> getRequestValidator(GetOdpSearchHitsAction action, RequestContext requestContext) {
		return NullValidator.get();
	}

	@Override
	public GetOdpSearchHitsResult execute(GetOdpSearchHitsAction action, ExecutionContext executionContext) {
		String queryString = action.getQueryString();
		OdpSearchFilterConfiguration filterConfiguration = action.getFilterConfiguration();
		RestTemplate restTemplate = new RestTemplate();
		String queryUri = String.format("%s/search/odpSearch?queryString=%s", XdpServiceUriBase, queryString);
		OdpSearchResult[] results = restTemplate.postForObject(queryUri, filterConfiguration, OdpSearchResult[].class);
		return new GetOdpSearchHitsResult(Arrays.asList(results));
	}
}
