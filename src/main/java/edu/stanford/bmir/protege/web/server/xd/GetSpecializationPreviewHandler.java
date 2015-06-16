package edu.stanford.bmir.protege.web.server.xd;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import edu.stanford.bmir.protege.web.server.change.ChangeGenerationContext;
import edu.stanford.bmir.protege.web.server.change.OntologyChangeList;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractHasProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.UserHasProjectReadPermissionValidator;
import edu.stanford.bmir.protege.web.server.logging.WebProtegeLogger;
import edu.stanford.bmir.protege.web.server.owlapi.OWLAPIProject;
import edu.stanford.bmir.protege.web.server.owlapi.OWLAPIProjectManager;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetSpecializationPreviewAction;
import edu.stanford.bmir.protege.web.shared.xd.data.OdpSpecialization;
import edu.stanford.bmir.protege.web.shared.xd.results.GetSpecializationPreviewResult;

public class GetSpecializationPreviewHandler extends AbstractHasProjectActionHandler<GetSpecializationPreviewAction,GetSpecializationPreviewResult> {
	
	private final WebProtegeLogger log;

	@Inject
	public GetSpecializationPreviewHandler(OWLAPIProjectManager projectManager, WebProtegeLogger logger) {
		super(projectManager);
		System.err.println("AARGH REGISTERED YOU FUCK!");
		this.log = logger;
	}
	
	@Override
	public Class<GetSpecializationPreviewAction> getActionClass() {
		return GetSpecializationPreviewAction.class;
	}

	@Override
	protected GetSpecializationPreviewResult execute(GetSpecializationPreviewAction action, OWLAPIProject project, ExecutionContext executionContext) {
		System.err.println("ARGH YOU FUCK!");
		try {
			
			log.info("Got specialization action!! : " + action);
			OdpSpecialization spec = action.getOdpSpecialization();
			
			OdpSpecializationChangeListGenerator generator = new OdpSpecializationChangeListGenerator(action.getOdpSpecialization());
			OntologyChangeList<OWLEntity> changes = generator.generateChanges(project, new ChangeGenerationContext(executionContext.getUserId()));
			
			log.info(String.format("PROJID=%s)",spec.getProjectId()));
			log.info(String.format("ODPIRI=%s)",spec.getOdpIri().toString()));
			log.info(String.format("SPECSTRAT=%s)",spec.getSpecializationStrategy().toString()));
			log.info(String.format("ALIGNMENTS=%s)",spec.getAlignments().toString()));
			log.info(String.format("CLASS=%s)",spec.getClassFrameTrees().toString()));
			log.info(String.format("OBJECT=%s)",spec.getObjectPropertyFrameTrees().toString()));
			log.info(String.format("DATA=%s)",spec.getDataPropertyFrameTrees().toString()));
			
			// Get out the axioms from the change generator
			Set<OWLAxiom> newAxioms = new HashSet<OWLAxiom>();
			for (OWLOntologyChange change: changes.getChanges()) {
				if (change.isAxiomChange()) {
					newAxioms.add(change.getAxiom());
				}
				// TODO: What to do if axiom is annotation?
			}
			
			// Set up a temporary demo ontology including those axioms
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology demoOntology = manager.createOntology(newAxioms);
			
			// Configure the format and prefixes of the 
			OWLOntologyFormat format = manager.getOntologyFormat(demoOntology);
	        TurtleOntologyFormat turtleFormat = new TurtleOntologyFormat();
	        if (format.isPrefixOWLOntologyFormat()) {
	        	turtleFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
	        }
	        
	        // Save demo ontology into Turtle format into an output stream, send to client
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        manager.saveOntology(demoOntology, turtleFormat, baos);
	        GetSpecializationPreviewResult result = new GetSpecializationPreviewResult(baos.toString());
	        
	        return result;
			
			// TODO: Actually implement this.
			/*String dummyResponse = "<#green-goblin>\n" + 
					"    rel:enemyOf <#spiderman> ;\n" + 
					"    a foaf:Person ;    # in the context of the Marvel universe\n" +
					"    foaf:name \"Green Goblin\" .\n" + 
					"\n" +
					"<#spiderman>\n" +
					"    rel:enemyOf <#green-goblin> ;\n" + 
					"    a foaf:Person ;\n" +
					"    foaf:name \"Spiderman\", \"Человек-паук\"@ru .";*/
			
			
			
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected RequestValidator<GetSpecializationPreviewAction> getAdditionalRequestValidator(GetSpecializationPreviewAction action, RequestContext requestContext) {
		return new UserHasProjectReadPermissionValidator<GetSpecializationPreviewAction>();
	}
}
