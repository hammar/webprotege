package edu.stanford.bmir.protege.web.server.xd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.springframework.web.client.RestTemplate;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.stanford.bmir.protege.web.client.xd.XdService;
import edu.stanford.bmir.protege.web.server.logging.WebProtegeLogger;
import edu.stanford.bmir.protege.web.server.logging.WebProtegeLoggerManager;
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
	
	private String XdpServiceUriBase;
	private WebProtegeLogger log;
	
	public XdServiceImpl() {
		super();
		log = WebProtegeLoggerManager.get(getClass());
		try {
			Properties XdServiceProperties = new Properties();
			XdServiceProperties.load(XdServiceImpl.class.getResourceAsStream("xdService.properties"));
			XdpServiceUriBase = XdServiceProperties.getProperty("XdpServiceUriBase");
		} 
		catch (IOException e) {
			XdpServiceUriBase = "http://localhost:8080";
			log.info(String.format("Unable to find configuration file %s. Setting XdpServiceUriBase to default value %s.", "xdService.properties", XdpServiceUriBase));
		}
	}
	
	// Automatically generated serial.
	private static final long serialVersionUID = 4505349021619302502L;

	/**
	 * Given a competency question query, returns those ODPS most likely to be usable for said query.
	 */
	@Override
	public List<OdpSearchResult> getOdpSearchContent(String queryString, OdpSearchFilterConfiguration filterConfiguration)  {
		RestTemplate restTemplate = new RestTemplate();
		String queryUri = String.format("%s/odpSearch?queryString=%s", XdpServiceUriBase, queryString);
		OdpSearchResult[] results = restTemplate.postForObject(queryUri, filterConfiguration, OdpSearchResult[].class);
		return Arrays.asList(results);
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
        		OdpDetails odpInfo = new OdpDetails(odpIri,odpLabel,null,null,null,null,null,null,null);
        		
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
        		instantiations.add(instantiation);
        	}
        }
        
        return instantiations;
	}

	@Override
	public OdpDetails getOdpDetails(String odpUri) {
		// TODO: Handle what to do if the given URI is not actually a found ODP. Return null or throw exception?
		RestTemplate restTemplate = new RestTemplate();
		String queryUri = String.format("%s/odpDetails?uri=%s", XdpServiceUriBase, odpUri);
		OdpDetails odp = restTemplate.getForObject(queryUri, OdpDetails.class);
		return odp;
	}

	@Override
	public List<OdpDetails> getOdpsByCategory(String category) {
		RestTemplate restTemplate = new RestTemplate();
		String queryUri = String.format("%s/odpsByCategory?category=%s", XdpServiceUriBase, category);
		OdpDetails[] results = restTemplate.getForObject(queryUri, OdpDetails[].class); 
		return Arrays.asList(results);
	}

	// This is a junk implementation, just to test out how WebProtege deals with sending OWL-API objects
	// over the wire and picking them up client-side.
	// TODO: Actually implement this properly.
	@Override
	public OWLClass getOdpImplementation(String uri) {
		try {
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        String base = "http://org.semanticweb.datarangeexample";
        OWLOntology ont = man.createOntology(IRI.create(base));
        OWLDataFactory factory = man.getOWLDataFactory();
        
        OWLDataProperty hasAge = factory.getOWLDataProperty(IRI.create(base + "hasAge"));
        OWLFunctionalDataPropertyAxiom funcAx = factory.getOWLFunctionalDataPropertyAxiom(hasAge);
        man.applyChange(new AddAxiom(ont, funcAx));
        
        OWLDatatype intDatatype = factory.getIntegerOWLDatatype();
        OWLLiteral eighteenConstant = factory.getOWLLiteral(18);
        OWLFacet facet = OWLFacet.MIN_INCLUSIVE;
        OWLDataRange intGreaterThan18 = factory.getOWLDatatypeRestriction(intDatatype, facet, eighteenConstant);
        OWLClassExpression thingsWithAgeGreaterOrEqualTo18 = factory.getOWLDataSomeValuesFrom(hasAge, intGreaterThan18);
        OWLClass adult = factory.getOWLClass(IRI.create(base + "#Adult"));
        OWLSubClassOfAxiom ax = factory.getOWLSubClassOfAxiom(adult, thingsWithAgeGreaterOrEqualTo18);
        man.applyChange(new AddAxiom(ont, ax));
        return adult;
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
