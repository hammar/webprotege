package edu.stanford.bmir.protege.web.client.xd.specialization.propertydetails;

import org.semanticweb.owlapi.vocab.Namespaces;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

import com.gwtext.client.data.SimpleStore;

import edu.stanford.bmir.protege.web.client.xd.specialization.XdSpecializationWizard;

public abstract class DatatypePropertyDetailsWindow extends PropertyDetailsWindow {

	public DatatypePropertyDetailsWindow(XdSpecializationWizard parentWizard) {
		super(parentWizard);
	}

	@Override
	protected SimpleStore getRangeStore() {
		int storeSize = XSDVocabulary.values().length;
		String[][] storeData = new String[storeSize][2];
		int i = 0;
		for (XSDVocabulary xsdType: XSDVocabulary.values()) {
			String iri = xsdType.getIRI().toString();
			String shortname = iri.replace(Namespaces.XSD.toString(), "xsd:");
			storeData[i][0] = shortname;
			storeData[i][1] = iri;
			i++;
		}
		SimpleStore rangeStore = new SimpleStore(new String[]{"label", "iri"}, storeData);
		return rangeStore;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		propertyRange.setValueField("iri");
	}
}
