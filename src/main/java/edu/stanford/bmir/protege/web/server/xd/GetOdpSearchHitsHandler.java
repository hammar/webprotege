package edu.stanford.bmir.protege.web.server.xd;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import javax.inject.Inject;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.karlhammar.xdpservices.data.OdpSearchFilterConfiguration;
import com.karlhammar.xdpservices.data.OdpSearchResult;

import edu.stanford.bmir.protege.web.client.dispatch.ActionExecutionException;
import edu.stanford.bmir.protege.web.server.dispatch.ActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.NullValidator;
import edu.stanford.bmir.protege.web.server.logging.WebProtegeLogger;
import edu.stanford.bmir.protege.web.server.xd.log.XdpLogger;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetOdpSearchHitsAction;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpSearchHitsResult;

public class GetOdpSearchHitsHandler implements ActionHandler<GetOdpSearchHitsAction,GetOdpSearchHitsResult> {

	private final XdpLogger xdpLog;
	private final WebProtegeLogger log;
	private String XdpServiceUriBase;
	
	@Inject
	public GetOdpSearchHitsHandler(WebProtegeLogger logger) {
		super();
		this.log = logger;
		this.xdpLog = XdpLogger.getInstance();
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
		try {
			String queryString = action.getQueryString();
			OdpSearchFilterConfiguration filterConfiguration = action.getFilterConfiguration();
			RestTemplate restTemplate = new RestTemplate();
			String queryUri = String.format("%s/search/odpSearch?queryString=%s", XdpServiceUriBase, queryString);
			OdpSearchResult[] results = restTemplate.postForObject(queryUri, filterConfiguration, OdpSearchResult[].class);
			
			// Log request, user ID, filters, and results for later analysis
			this.xdpLog.logOdpSearchExecuted(executionContext.getUserId(), queryString, filterConfiguration, results);
			
			return new GetOdpSearchHitsResult(Arrays.asList(results));
		}
		catch (RestClientException ex) {
			ex.getMostSpecificCause().printStackTrace();
			
			//ex.printStackTrace();
			throw new ActionExecutionException(ex.getCause());
		}
	}
}
