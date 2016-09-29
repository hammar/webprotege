package edu.stanford.bmir.protege.web.shared.xd.data.restrictions;

import java.io.Serializable;

public interface Restriction extends Serializable {
	
	/**
	 * Returns a string representation of the entity that is restricted by this restriction.
	 * @return
	 */
	public String getEntityLabel();
	public String toHtmlString();
}
