package edu.stanford.bmir.protege.web.server.xd.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Handler;

import org.apache.commons.lang.StringUtils;

import edu.stanford.bmir.protege.web.server.owlapi.OWLAPIProject;
import edu.stanford.bmir.protege.web.shared.user.UserId;
import edu.stanford.bmir.protege.web.shared.xd.OdpSearchFilterConfiguration;
import edu.stanford.bmir.protege.web.shared.xd.OdpSearchResult;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;

public class XdpLogger {
	
	private static XdpLogger instance = null;
	
	public static final String LOG_NAME = "xdp";
	
	private final Logger logger;
	private final SimpleDateFormat dateFormat;
	private static Handler fileHandler;
	
	public static XdpLogger getInstance() {
		if(instance == null) {
			instance = new XdpLogger();
		}
		return instance;
	}
	
	private XdpLogger() {
		this.logger = Logger.getLogger(LOG_NAME);
		try {
			// Try to set up a file system handler where logs are dumped
			fileHandler = new FileHandler("/var/log/xdplog/xdplog.log", true);
			this.logger.addHandler(fileHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.dateFormat = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssZ");
	}

    private void writeToLog(String message, Level level) {
        if (logger.isLoggable(level)) {
            logger.log(level, message);
        }
    }
    
    private String formatLogMessage(String action, UserId userId, String data) {
    	String timestamp = dateFormat.format(new Date());
    	return String.format("%s;%s;%s;%s", timestamp, action, userId.getUserName(), data);
    }
    
    public void logOdpSearchExecuted(UserId userId, String queryString, OdpSearchFilterConfiguration filterConfiguration, OdpSearchResult[] results) {
    	// Serialize query
    	String data = queryString;
    	
    	// Serialize filter configuration
    	data += (";" + filterConfiguration.toString());
    	
    	// Serialize result set
    	data += (";" + StringUtils.join(results, ";"));
    
    	// Construct and write log message
    	String logMessage = formatLogMessage("ODP-SEARCH", userId, data);
    	this.writeToLog(logMessage, Level.INFO);
    }
    
    public void logOdpContentsRetrieved(UserId userId, String odpIri) {
    	String logMessage = formatLogMessage("ODP-CONTENTS-RETRIEVED", userId, odpIri);
    	this.writeToLog(logMessage, Level.INFO);
    }
    
    public void logOdpMetadataRetrieved(UserId userId, String odpIri) {
    	String logMessage = formatLogMessage("ODP-METADATA-RETRIEVED", userId, odpIri);
    	this.writeToLog(logMessage, Level.INFO);
    }
    
    public void logSuggestedOdpAlignments(UserId userId, OWLAPIProject project, Set<Alignment> alignments) {  	
    	// Serialize project ID
    	String data = project.getProjectId().toString();
    	
    	// Serialize project revision number
    	data += (";" + project.getChangeManager().getCurrentRevision().getValueAsInt());
    	
    	// Serialize alignment suggestions
    	data += (";" + StringUtils.join(alignments, ";"));
    	
    	// Construct and write log message
    	String logMessage = formatLogMessage("ODP-ALIGNMENT-SUGGESTIONS", userId, data);
    	this.writeToLog(logMessage, Level.INFO);
    }
    
    public void logUsedOdpAlignments(UserId userId, OWLAPIProject project, Set<Alignment> alignments) {  	
    	// Serialize project ID
    	String data = project.getProjectId().toString();
    	
    	// Serialize project revision number
    	data += (";" + project.getChangeManager().getCurrentRevision().getValueAsInt());
    	
    	// Serialize used alignments
    	if (alignments.size() > 0) {
    		data += (";" + StringUtils.join(alignments, ";"));
    	}
    	else {
    		data += (";NO ALIGNMENTS SELECTED");
    	}
    	
    	// Construct and write log message
    	String logMessage = formatLogMessage("ODP-ALIGNMENTS-USED", userId, data);
    	this.writeToLog(logMessage, Level.INFO);
    }
}
