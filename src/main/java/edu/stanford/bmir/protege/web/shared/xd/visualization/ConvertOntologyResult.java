package edu.stanford.bmir.protege.web.shared.xd.visualization;

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
