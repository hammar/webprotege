package edu.stanford.bmir.protege.web.server.xd;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.uni_stuttgart.vis.vowl.owl2vowl.Owl2Vowl;
import edu.stanford.bmir.protege.web.server.change.ChangeGenerationContext;
import edu.stanford.bmir.protege.web.server.change.OntologyChangeList;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractHasProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.UserHasProjectReadPermissionValidator;
import edu.stanford.bmir.protege.web.server.owlapi.OWLAPIProject;
import edu.stanford.bmir.protege.web.server.owlapi.OWLAPIProjectManager;
import edu.stanford.bmir.protege.web.shared.xd.actions.GetInstantiationPreviewAction;
import edu.stanford.bmir.protege.web.shared.xd.results.GetInstantiationPreviewResult;

public class GetInstantiationPreviewHandler extends AbstractHasProjectActionHandler<GetInstantiationPreviewAction,GetInstantiationPreviewResult> {

	@Inject
	public GetInstantiationPreviewHandler(OWLAPIProjectManager projectManager) {
		super(projectManager);
	}
	
	@Override
	public Class<GetInstantiationPreviewAction> getActionClass() {
		return GetInstantiationPreviewAction.class;
	}

	@Override
	protected GetInstantiationPreviewResult execute(GetInstantiationPreviewAction action, OWLAPIProject project, ExecutionContext executionContext) {
		try {
			
			OdpInstantiationChangeListGenerator generator = new OdpInstantiationChangeListGenerator(action.getOdpInstantiation());
			OntologyChangeList<OWLEntity> changes = generator.generateChanges(project, new ChangeGenerationContext(executionContext.getUserId()));
			
			// Get out the axioms from the change generator
			Set<OWLAxiom> newAxioms = new HashSet<OWLAxiom>();
			for (OWLOntologyChange change: changes.getChanges()) {
				if (change.isAxiomChange()) {
					newAxioms.add(change.getAxiom());
				}
			}
			
			// Set up a temporary demo ontology including those axioms
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology demoOntology = manager.createOntology(newAxioms, IRI.create("wptmp:entity"));
			
			// Configure the format and prefixes of the         
	        ManchesterOWLSyntaxOntologyFormat manchesterSyntax = new ManchesterOWLSyntaxOntologyFormat();
	        for (Entry<String,String> prefixMapping: generator.getPrefixes().entrySet()) {
				manchesterSyntax.setPrefix(prefixMapping.getKey(), prefixMapping.getValue());
			}
	        
	        // Save demo ontology into Turtle format into an output stream
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        manager.saveOntology(demoOntology, manchesterSyntax, baos);
	        String instantiationAxioms = baos.toString();
	        
	        // Get VOWL representation for visualisation purposes
	        Owl2Vowl owl2Vowl = new Owl2Vowl(demoOntology, "wptmp:entity");
	        String instantiationAsJsonString = owl2Vowl.getJsonAsString();
	        
	        // Package result, send to client
	        GetInstantiationPreviewResult result = new GetInstantiationPreviewResult(instantiationAxioms, instantiationAsJsonString);
	        return result;
	        
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected RequestValidator<GetInstantiationPreviewAction> getAdditionalRequestValidator(GetInstantiationPreviewAction action, RequestContext requestContext) {
		return new UserHasProjectReadPermissionValidator<GetInstantiationPreviewAction>();
	}
}
