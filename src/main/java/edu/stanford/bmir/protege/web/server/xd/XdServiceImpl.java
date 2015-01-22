package edu.stanford.bmir.protege.web.server.xd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.stanford.bmir.protege.web.client.xd.XdService;
import edu.stanford.bmir.protege.web.server.owlapi.OWLAPIProjectManager;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.xd.OdpDetails;
import edu.stanford.bmir.protege.web.shared.xd.OdpInstantiation;
import edu.stanford.bmir.protege.web.shared.xd.OdpSearchFilterConfiguration;
import edu.stanford.bmir.protege.web.shared.xd.OdpSearchResult;

/***
 * Implementation of the GWT-RPC methods used by the eXtreme Design WebProtege tab and
 * associated portlets.
 * @author Karl Hammar
 */
public class XdServiceImpl extends RemoteServiceServlet implements XdService {

	// Test data used for UI mockup.
	// TODO: Remove below when no longer needed.
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

	// Given a competency question query, returns those ODPS most likely to be usable for said query.	
	@Override
	public List<OdpSearchResult> getOdpSearchContent(String queryString, OdpSearchFilterConfiguration filterConfiguration) {
		// TODO: Actually implement this.
		OdpSearchResult[] hits = {new OdpSearchResult(od1,0.30), new OdpSearchResult(od2,0.60), new OdpSearchResult(od3,0.90)};
		return Arrays.asList(hits);
	}

	/**
	 * Returns ODP instantiations in the project, e.g. by the root ontology directly imported ontology modules
	 * that themselves in turn directly import exactly one ontology which is a known ODP.
	 */
	@Override
	public List<OdpInstantiation> getOdpInstantiations(ProjectId projectId) {
		List<OdpInstantiation> instantiations = new ArrayList<OdpInstantiation>();
		
		// General OWLAPI infrastructure stuff
		String cpAnnotationSchemaUri = "http://www.ontologydesignpatterns.org/schemas/cpannotationschema.owl";
        OWLAPIProjectManager pm = OWLAPIProjectManager.getProjectManager();
        OWLDataFactory df = OWLManager.getOWLDataFactory();
        OWLAnnotationProperty label = df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        OWLOntology rootOntology = pm.getProject(projectId).getRootOntology();

        // Check for all imports by the root ontology
        Set<OWLOntology> directImports = rootOntology.getDirectImports();
        for (OWLOntology candidateSpecialisation: directImports) {
        	
        	// Then check each import's imports in turn (those are potentially ODPs)
        	Set<OWLOntology> confirmedOdps = new HashSet<OWLOntology>();
        	Set<OWLOntology> candidateOdps = candidateSpecialisation.getDirectImports();
        	for (OWLOntology candidateOdp: candidateOdps) {
        		Boolean isCandidateReallyOdp = false;
        		
        		// Check whether potential ODP directly imports CP Annotation Schema. If so, it is actually an ODP.
        		for (OWLOntology importOfCandidateOdp: candidateOdp.getDirectImports()) {
        			if (importOfCandidateOdp.getOntologyID().getOntologyIRI().toString().equalsIgnoreCase(cpAnnotationSchemaUri)) {
        				isCandidateReallyOdp = true;
        				break;
        			}
        		}
        		if (isCandidateReallyOdp) {
        			confirmedOdps.add(candidateOdp);
        		}
        	}
        	
        	// At the moment we only handle specializations of one ODP at a time. If more than one
        	// confirmed ODP exists for a given root import, then we ignore. Somewhat ugly.
        	if (confirmedOdps.size() == 1) {
        		
        		// Get sufficient ODP details from ODP ontology
        		// Could be fetched from ODP index but with possible latency problem.
        		OWLOntology odpOntology = confirmedOdps.iterator().next();
        		String odpIri = odpOntology.getOntologyID().getOntologyIRI().toString();
        		// Default IRI-based label
        		String odpLabel = odpIri.substring(odpIri.lastIndexOf("/")+1);
        		// RDFS:label-based label
        		Set<OWLAnnotation> odpAnnotations = odpOntology.getAnnotations();
        		for (OWLAnnotation odpAnnotation: odpAnnotations) {
        			if (odpAnnotation.getProperty().getIRI().toString().equalsIgnoreCase(label.getIRI().toString())) {
        				OWLLiteral labelVal = (OWLLiteral)odpAnnotation.getValue();
        				odpLabel = labelVal.getLiteral();
        				break;
        			}
        		}
        		OdpDetails odpInfo = new OdpDetails(odpIri,odpLabel,null,null,null,null);
        		
        		// Get specialization IRI.
        		String specializationIri = candidateSpecialisation.getOntologyID().getOntologyIRI().toString();
        		// Default specialization label (overridden below if better exists)
        		String specializationLabel = "Instantiation: " + odpLabel;
        		// Get specialization RDFS:label if one exists. 
        		Set<OWLAnnotation> annotations = candidateSpecialisation.getAnnotations();
        		for (OWLAnnotation annotation: annotations) {
        			if (annotation.getProperty().getIRI().toString().equalsIgnoreCase(label.getIRI().toString())) {
        				OWLLiteral labelVal = (OWLLiteral)annotation.getValue();
        				specializationLabel = labelVal.getLiteral();
        				break;
        			}
        		}
        		
        		OdpInstantiation instantiation = new OdpInstantiation(specializationIri,specializationLabel,odpInfo);
        		System.out.println("Adding instantiation: (" + specializationIri + "," + specializationLabel + ",(" + odpInfo.getUri() + "," + odpInfo.getName() + "))");
        		instantiations.add(instantiation);
        	}
        }
        
        return instantiations;
	}

	@Override
	public OdpDetails getOdpDetails(String odpUri) {
		// TODO: Actually implement this method, fetching details from search.xd-protege.com ODP repository service.
		// TODO: Also handle what to do if the given URI is not actually a found ODP. Return null or throw exception?
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
}
