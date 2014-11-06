package edu.stanford.bmir.protege.web.server.xd;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.stanford.bmir.protege.web.client.xd.XdService;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.xd.OdpDetails;
import edu.stanford.bmir.protege.web.shared.xd.OdpInstantiation;

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

	@Override
	public List<OdpInstantiation> getOdpInstantiations(ProjectId projectId) {
		/* TODO: Actually implement this method, fetching imports from project that 
		 * are in fact ODP instantiations, and the corresponding ODPs. */
		OdpDetails od1 = new OdpDetails("http://www.ontologydesignpatterns.org/cp/owl/naryparticipation.owl","Nary Participation");
		OdpDetails od2 = new OdpDetails("http://www.ontologydesignpatterns.org/cp/owl/informationrealization.owl","Information Realization");
		OdpDetails od3 = new OdpDetails("http://infoeng.se/~karl/ilog2014/odps/accountability.owl","Accountability");
		OdpInstantiation oi1 = new OdpInstantiation("http://www.example.com/classAttendanceModule.owl","Instantiation: Class attendance",od1);
		OdpInstantiation oi2 = new OdpInstantiation("http://www.example.com/clubMembershipModule.owl","Instantiation: Club membership",od1);
		OdpInstantiation oi3 = new OdpInstantiation("http://www.example.com/meetingAttendanceModule.owl","Instantiation: Meeting attendance",od1);
		OdpInstantiation oi4 = new OdpInstantiation("http://www.example.com/printedBooksModule.owl","Instantiation: Books and Printed Copies",od2);
		OdpInstantiation oi5 = new OdpInstantiation("http://www.example.com/patientConsentModule.owl","Instantiation: Patient Consent",od3);
		OdpInstantiation oi6 = new OdpInstantiation("http://www.example.com/houseKeepingModule.owl","Instantiation: House-keeping",od3);
		return Arrays.asList(oi1,oi2,oi3,oi4,oi5,oi6);
	}

	@Override
	public OdpDetails getOdpDetails(String odpUri) {
		// TODO: Actually implement this method, fetching details from ODP repository (local or remote?)
		String odpName;
		if (odpUri == "http://www.ontologydesignpatterns.org/cp/owl/naryparticipation.owl") {
			odpName = "Nary Participation";
		}
		else if (odpUri == "http://www.ontologydesignpatterns.org/cp/owl/informationrealization.owl") {
			odpName = "Information Realization";
		}
		else if (odpUri == "http://infoeng.se/~karl/ilog2014/odps/accountability.owl") {
			odpName = "Accountability";
		}
		else {
			odpName = "ODP name unknown";
		}
		return new OdpDetails(odpUri,odpName);
	}

	@Override
	public List<OdpDetails> getUsedOdps(ProjectId projectId) {
		// TODO: Actually implement this method, fetching used ODPs via imported ODP instantiations.
		OdpDetails od1 = new OdpDetails("http://www.ontologydesignpatterns.org/cp/owl/naryparticipation.owl","Nary Participation");
		OdpDetails od2 = new OdpDetails("http://www.ontologydesignpatterns.org/cp/owl/informationrealization.owl","Information Realization");
		OdpDetails od3 = new OdpDetails("http://infoeng.se/~karl/ilog2014/odps/accountability.owl","Accountability");
		return Arrays.asList(od1,od2,od3);
	}
}
