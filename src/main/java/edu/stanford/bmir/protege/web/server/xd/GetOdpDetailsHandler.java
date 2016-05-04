package edu.stanford.bmir.protege.web.server.xd;

import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;

import org.springframework.web.client.RestTemplate;

import edu.stanford.bmir.protege.web.server.dispatch.ActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.NullValidator;
import edu.stanford.bmir.protege.web.server.logging.WebProtegeLogger;
import edu.stanford.bmir.protege.web.server.xd.log.XdpLogger;
import edu.stanford.bmir.protege.web.shared.xd.OdpDetails;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetOdpDetailsAction;
import edu.stanford.bmir.protege.web.shared.xd.results.GetOdpDetailsResult;

public class GetOdpDetailsHandler implements ActionHandler<GetOdpDetailsAction,GetOdpDetailsResult> {

	private final XdpLogger xdpLog;
	private final WebProtegeLogger log;
	private String XdpServiceUriBase;
	
	@Inject
	public GetOdpDetailsHandler(WebProtegeLogger logger) {
		super();
		this.log = logger;
		this.xdpLog = XdpLogger.getInstance();
		try {
			Properties XdServiceProperties = new Properties();
			XdServiceProperties.load(GetOdpDetailsHandler.class.getResourceAsStream("XdpService.properties"));
			XdpServiceUriBase = XdServiceProperties.getProperty("XdpServiceUriBase");
		} 
		catch (IOException e) {
			XdpServiceUriBase = "http://localhost:8080";
			log.info(String.format("Unable to find configuration file %s. Setting XdpServiceUriBase to default value %s.", "xdService.properties", XdpServiceUriBase));
		}
	}
	
	@Override
	public Class<GetOdpDetailsAction> getActionClass() {
		return GetOdpDetailsAction.class;
	}

	@Override
	public RequestValidator<GetOdpDetailsAction> getRequestValidator(GetOdpDetailsAction action, RequestContext requestContext) {
		return NullValidator.get();
	}

	@Override
	public GetOdpDetailsResult execute(GetOdpDetailsAction action, ExecutionContext executionContext) {
		// TODO: Handle what to do if the given URI is not actually a found ODP. Return null or throw exception?
		String odpUri = action.getOdpUri();
		RestTemplate restTemplate = new RestTemplate();
		String queryUri = String.format("%s/retrieve/odpMetadata?uri=%s", XdpServiceUriBase, odpUri);
		OdpDetails odp = restTemplate.getForObject(queryUri, OdpDetails.class);
		
		// Log details request for later analysis
		xdpLog.logOdpMetadataRetrieved(executionContext.getUserId(), action.getOdpUri());
		
		return new GetOdpDetailsResult(odp);
	}
}
