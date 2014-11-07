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

	// Test data used for UI mockup.
	private OdpDetails od1 = new OdpDetails("http://www.ontologydesignpatterns.org/cp/owl/naryparticipation.owl",
			"Nary Participation",
			"All sorts of relations denoting events with multiple participants, space-time indexing, etc. can be represented with this pattern. When objects participate at the event at different times or with different parts, more elementary nary-participation instances must be created, and made parts of the main one.",
			"General",
			"What are the participants in that event at this time?, What events had what participants in that location?",
			"http://ontologydesignpatterns.org/wiki/images/e/e2/Naryparticipation.jpg");
	private OdpDetails od2 = new OdpDetails("http://www.ontologydesignpatterns.org/cp/owl/informationrealization.owl",
			"Information Realization",
			"This is a basic patterns, representing the difference between abstract and realized (manifested, concrete, etc.) information.",
			"Semiotics",
			"what are the physical realizations of this information object?, what information objects are realized by this physical object?",
			"http://ontologydesignpatterns.org/wiki/images/7/7b/Informationrealization.jpg");
	private OdpDetails od3 = new OdpDetails("http://infoeng.se/~karl/ilog2014/odps/accountability.owl",
			"Accountability",
			"This pattern captures time-limited relations or responsibilities that people and organisations can have to one another and that allows for certain actions to be taken.",
			"Business modelling",
			"Who can drive the Duff Blimp?,Has Homer signed patient consent for Dr Nick to do a hysterectomy?,How many people does Moe housewatch for in the summer of 2011?",
			"http://www.infoeng.se/~karl/temp/accountability.png");
	
	// Automatically generated serial.
	private static final long serialVersionUID = 4505349021619302502L;

	// Given a competency question query, returns URIs of those ODPS most likely 
	// to be usable for said query.
	// TODO: Update this (and corresponding interfaces) to also send human-readable name of ODP	
	@Override
	public List<String> getOdpSearchContent(String queryString) {
		// TODO: Actually implement this.
		String[] hits = {od1.getUri(),od2.getUri(),od3.getUri()};
		return Arrays.asList(hits);
	}

	@Override
	public List<OdpInstantiation> getOdpInstantiations(ProjectId projectId) {
		/* TODO: Actually implement this method, fetching imports from project that 
		 * are in fact ODP instantiations, and the corresponding ODPs. */
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
		// TODO: Also handle what to do if the given URI is not actually an ODP. Return null or throw exception?
		if (odpUri.equalsIgnoreCase("http://www.ontologydesignpatterns.org/cp/owl/naryparticipation.owl")) {
			return od1;
		}
		else if (odpUri.equalsIgnoreCase("http://www.ontologydesignpatterns.org/cp/owl/informationrealization.owl")) {
			return od2;
		}
		else if (odpUri.equalsIgnoreCase("http://infoeng.se/~karl/ilog2014/odps/accountability.owl")) {
			return od3;
		}
		else {
			return new OdpDetails(odpUri,"NONEXISTANT","NODESCRIPTION","NODOMAIN","NOCQS","NOIMAGE");
		}
	}

	@Override
	public List<OdpDetails> getUsedOdps(ProjectId projectId) {
		// TODO: Actually implement this method, fetching used ODPs via imported ODP instantiations.
		return Arrays.asList(od1,od2,od3);
	}
}
