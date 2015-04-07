package edu.stanford.bmir.protege.web.client.xd;

import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.xd.OdpDetails;
import edu.stanford.bmir.protege.web.shared.xd.OdpInstantiation;
import edu.stanford.bmir.protege.web.shared.xd.OdpSearchFilterConfiguration;
import edu.stanford.bmir.protege.web.shared.xd.OdpSearchResult;

/***
 * Service manager simplifying XD GWT-RPC usage.
 * This is implemented as a singleton and should be called as
 * XdServiceManager.getInstance().WhateverMethod()
 * @author Karl Hammar
 */
public class XdServiceManager {

	private XdServiceAsync proxy;
	static XdServiceManager instance;

	// Instantiate or retrieve singleton instance
    public static XdServiceManager getInstance() {
        if (instance == null) {
            instance = new XdServiceManager();
        }
        return instance;
    }
	
    // Constructor, automatically creates async proxy using GWT-RPC framework
    private XdServiceManager() {
        proxy = (XdServiceAsync) GWT.create(XdService.class);
    }
    
    // Queries server for suitable ODPs for a given competency question query string.
    public void getOdpSearchContent(String queryString, OdpSearchFilterConfiguration filterConfiguration, AsyncCallback<List<OdpSearchResult>> cb) {
        proxy.getOdpSearchContent(queryString, filterConfiguration, cb);
    }
    
    // Queries server for ODPs that are instantiated in the project
    public void getInstantiatedOdps(ProjectId projectId, AsyncCallback<List<OdpInstantiation>> cb) {
    	proxy.getOdpInstantiations(projectId, cb);
    }
    
    // Queries server for details about a particular ODP
    public void getOdpDetails(String odpUri, AsyncCallback<OdpDetails> cb) {
    	proxy.getOdpDetails(odpUri, cb);
    }
    
    // Queries server for a list of all ODPs of a particular category
    public void getOdpsByCategory(String category, AsyncCallback<List<OdpDetails>> cb) {
    	proxy.getOdpsByCategory(category, cb);
    }
    
    // Queries server to return the implementation of a particular ODP 
    public void getOdpImplementation(String uri, AsyncCallback<OWLClass> cb) {
    	proxy.getOdpImplementation(uri, cb);
    }
    
}
