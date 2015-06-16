package edu.stanford.bmir.protege.web.server.xd;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.springframework.web.client.RestTemplate;

import edu.stanford.bmir.protege.web.server.change.ChangeGenerationContext;
import edu.stanford.bmir.protege.web.server.change.ChangeListGenerator;
import edu.stanford.bmir.protege.web.server.change.OntologyChangeList;
import edu.stanford.bmir.protege.web.server.logging.WebProtegeLogger;
import edu.stanford.bmir.protege.web.server.owlapi.OWLAPIProject;
import edu.stanford.bmir.protege.web.server.owlapi.RenameMap;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpSpecialization;

public class OdpSpecializationChangeListGenerator implements ChangeListGenerator<OWLEntity> {

	
	@Inject private WebProtegeLogger log;
	private String XdpServiceUriBase;
	
	public OdpSpecializationChangeListGenerator(OdpSpecialization specialization) {
		super();
		
		this.specialization = specialization;
		
		try {
			Properties XdServiceProperties = new Properties();
			XdServiceProperties.load(GetOdpContentsHandler.class.getResourceAsStream("XdpService.properties"));
			XdpServiceUriBase = XdServiceProperties.getProperty("XdpServiceUriBase");
		} 
		catch (IOException e) {
			XdpServiceUriBase = "http://localhost:8080";
			log.info(String.format("Unable to find configuration file %s. Setting XdpServiceUriBase to default value %s.", "xdService.properties", XdpServiceUriBase));
		}
	}

	private OdpSpecialization specialization;
	
	@Override
	public OntologyChangeList<OWLEntity> generateChanges(OWLAPIProject project, ChangeGenerationContext context) {
		
		// Initiate change builder
		OntologyChangeList.Builder<OWLEntity> builder = new OntologyChangeList.Builder<OWLEntity>();
		
		// Load ODP
		OWLOntology odp = getOdp(specialization.getOdpIri());
		
		// For every axiom in ODP, add to change builder (e.g., copy the ODP into the target ontology)
		Set<OWLAxiom> allOdpAxioms = odp.getAxioms();
		for (OWLAxiom axiom: allOdpAxioms) {
			builder.addAxiom(project.getRootOntology(), axiom);
		}
		
		// TODO: create classes, create properties, create restriction axioms, create alignment axioms... the tricky stuff
		
		// Build and return
		return builder.build();
	}

	@Override
	public OWLEntity getRenamedResult(OWLEntity result, RenameMap renameMap) {
		// TODO Auto-generated method stub
		return null;
	}

	private OWLOntology getOdp(IRI odpIri) {
		try {
			// Fetch ODP as Turtle-formatted string from XdpService via REST
			RestTemplate restTemplate = new RestTemplate();
			String queryUri = String.format("%s/retrieve/odpBuildingBlockTurtle?uri=%s", XdpServiceUriBase, odpIri.toString());
			String turtleRepresentation = restTemplate.getForObject(queryUri, String.class);
			
			// Load ODP as in-memory OWLOntology representation
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntologyLoaderConfiguration config = new OWLOntologyLoaderConfiguration();
	        config = config.setFollowRedirects(false);
	        config = config.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
	        StreamDocumentSource sds = new StreamDocumentSource(IOUtils.toInputStream(turtleRepresentation));
	        OWLOntology odp = manager.loadOntologyFromOntologyDocument(sds, config);
	        return odp;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
