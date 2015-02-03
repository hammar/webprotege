package edu.stanford.bmir.protege.web.shared.xd;

import java.util.Comparator;

public class OdpSearchResultComparator implements Comparator<OdpSearchResult> {
	
	@Override
	public int compare(OdpSearchResult o1, OdpSearchResult o2) {
		return o1.getConfidence().compareTo(o2.getConfidence());
	}
}
