package edu.stanford.bmir.protege.web.shared.visualization.vowl;

import edu.stanford.bmir.protege.web.shared.dispatch.Result;

public class ConvertOntologyResult implements Result {

	private String ontologyAsJSONStr;
	
	ConvertOntologyResult() {
		
	}
	
	public ConvertOntologyResult(String ontologyAsJSONStr) {
		this.ontologyAsJSONStr = ontologyAsJSONStr;
	}
	
	public String getOntologyasJSONStr() {
		return ontologyAsJSONStr;
	}
}
