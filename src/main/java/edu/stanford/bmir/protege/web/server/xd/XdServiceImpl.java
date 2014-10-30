package edu.stanford.bmir.protege.web.server.xd;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.stanford.bmir.protege.web.client.xd.XdService;

/***
 * Implementation of the GWT-RPC methods used by the eXtreme Design WebProtege tab and
 * associated portlets.
 * @author Karl Hammar
 */
public class XdServiceImpl extends RemoteServiceServlet implements XdService {

	// Automatically generated serial.
	private static final long serialVersionUID = 4505349021619302502L;

	// Given a competency question query, returns ODPs most likely to be usable for said query.
	@Override
	public List<String> getOdpSearchContent(String queryString) {
		// TODO: Actually implement this.
		String[] hits = {"http://www.example.com/odp1#","http://www.example.com/odp2#","http://www.example.com/odp3#"};
		return Arrays.asList(hits);
	}
}
