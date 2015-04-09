package edu.stanford.bmir.protege.web.client.xd;

import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.xd.OdpDetails;
import edu.stanford.bmir.protege.web.shared.xd.OdpInstantiation;
import edu.stanford.bmir.protege.web.shared.xd.OdpSearchFilterConfiguration;
import edu.stanford.bmir.protege.web.shared.xd.OdpSearchResult;

/***
 * Interface defining eXtreme Design WebProtege tab GWT-RPC services.
 * @author Karl Hammar
 */
@RemoteServiceRelativePath("xd")
public interface XdService extends RemoteService  {

	List<OdpSearchResult> getOdpSearchContent(String queryString, OdpSearchFilterConfiguration filterConfiguration);
	OdpDetails getOdpDetails(String odpUri);	
	List<OdpInstantiation> getOdpInstantiations(ProjectId projectId);
	List<OdpDetails> getOdpsByCategory(String category);
	
	void cacheImports(ProjectId projectId);
	
	// TODO: below is test, adapt and add all of the needed input parameters
	void applyOdpSpecialization(ProjectId projectId, OdpDetails odp, List<OWLClass> classes, List<OWLSubClassOfAxiom> subClassOfAxioms);
	
	
	//TODO: Below is test code, remove once better stuff in place
	OWLClass getOdpImplementation(String uri);
	
	
}
