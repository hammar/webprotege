package edu.stanford.bmir.protege.web.shared.xd;

import java.io.Serializable;

/**
 * Data class used for serializing an ODP search result to be sent over the wire.
 * References the ODP and gives a confidence score for that ODP being a suitable
 * match to the query for which this object is returned.
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
public class OdpSearchResult implements Serializable {

	private static final long serialVersionUID = 331396054286739588L;
	private OdpDetails odp;
	private Double confidence;
	
	// Overloaded equality operator used for searches in collections etc.
	// This is a data class so memory identitity is not sufficient criterion for equality;
	// instead we compare equality of ODP and of confidence score.
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj instanceof OdpSearchResult) {
			OdpSearchResult otherSearchResult = (OdpSearchResult)obj;
			return 
					this.odp.equals(otherSearchResult.odp) &&
					this.confidence.equals(otherSearchResult.confidence);
		}
		return false;
	}
	
	public OdpSearchResult() {
	}
	
	public OdpSearchResult(OdpDetails odp, Double confidence) {
		this.odp = odp;
		this.confidence = confidence;
	}

	public OdpDetails getOdp() {
		return odp;
	}

	public void setOdp(OdpDetails odp) {
		this.odp = odp;
	}

	public Double getConfidence() {
		return confidence;
	}

	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}
}
