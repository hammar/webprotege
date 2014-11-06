package edu.stanford.bmir.protege.web.client.xd;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.xd.OdpDetails;
import edu.stanford.bmir.protege.web.shared.xd.OdpInstantiation;

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
    public void getOdpSearchContent(String queryString, AsyncCallback<List<String>> cb) {
        proxy.getOdpSearchContent(queryString, cb);
    }
    
    // Queries server for ODPs that are instantiated in the project
    public void getInstantiatedOdps(ProjectId projectId, AsyncCallback<List<OdpInstantiation>> cb) {
    	proxy.getOdpInstantiations(projectId, cb);
    }
    
    // Queries server for details about a particular ODP
    public void getOdpDetails(String odpUri, AsyncCallback<OdpDetails> cb) {
    	proxy.getOdpDetails(odpUri, cb);
    }
    
}
