package edu.stanford.bmir.protege.web.shared.xd.results;

import java.io.Serializable;
import java.util.Set;

import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;

public class GetInstantiationAlignmentSuggestionsResult implements Result, Serializable {

	private static final long serialVersionUID = 4552909169511071901L;
	
	private Set<Alignment> alignments;

	/**
	 * GWT-RPC constructor
	 */
	@SuppressWarnings("unused")
	private GetInstantiationAlignmentSuggestionsResult() {
		
	}
	
	public GetInstantiationAlignmentSuggestionsResult(Set<Alignment> alignments) {
		this.alignments = alignments;
	}

	public Set<Alignment> getAlignments() {
		return alignments;
	}
}
