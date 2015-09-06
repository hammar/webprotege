package edu.stanford.bmir.protege.web.server.visualization.vowl;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import com.google.inject.Inject;
import de.uni_stuttgart.vis.vowl.owl2vowl.Owl2Vowl;
import edu.stanford.bmir.protege.web.server.dispatch.ActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestContext;
import edu.stanford.bmir.protege.web.server.dispatch.RequestValidator;
import edu.stanford.bmir.protege.web.server.dispatch.validators.NullValidator;
import edu.stanford.bmir.protege.web.server.owlapi.*;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.visualization.vowl.*;

public class ConvertOntologyHandler implements ActionHandler<ConvertOntologyAction, ConvertOntologyResult> {

	private OWLAPIProjectManager projectManager;

	@Inject
	public ConvertOntologyHandler(OWLAPIProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	@Override
	public Class<ConvertOntologyAction> getActionClass() {
		return ConvertOntologyAction.class;
	}

	@Override
	public RequestValidator<ConvertOntologyAction> getRequestValidator(
			ConvertOntologyAction action, RequestContext requestContext) {
		return NullValidator.get();
	}

	@Override
	public ConvertOntologyResult execute(ConvertOntologyAction action, 
			ExecutionContext executionContext) {
		ProjectId projectId = action.getProjectID();
		String ontologyAsJSONStr = convertOntologyForVowlVisualization(projectId);

		return new ConvertOntologyResult(ontologyAsJSONStr);
	}

	private String convertOntologyForVowlVisualization(ProjectId projectId) {
		
		OWLOntology ontology = projectManager.getProject(projectId).getRootOntology();
		OWLOntologyID id = ontology.getOntologyID();

		Owl2Vowl owl2Vowl = new Owl2Vowl(ontology, toName(id));

		return owl2Vowl.getJsonAsString();
	}

	private String toName(OWLOntologyID id) {
		if (id.isAnonymous()) {
			return id.toString();
		}
		else {
			return id.getOntologyIRI().toString();
		}
	}

}
