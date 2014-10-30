package edu.stanford.bmir.protege.web.client.xd;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/***
 * Interface defining eXtreme Design WebProtege tab GWT-RPC services.
 * @author Karl Hammar
 */
@RemoteServiceRelativePath("xd")
public interface XdService extends RemoteService  {

	List<String> getOdpSearchContent(String queryString);
	
}
