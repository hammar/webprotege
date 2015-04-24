package edu.stanford.bmir.protege.web.client.xd.specialization;

import org.semanticweb.owlapi.model.OWLEntity;

import com.google.gwt.user.client.ui.Label;

public class ClassDetailsWindow extends DetailsWindow {

	public ClassDetailsWindow() {
		super();
	}
	
	@Override
	public void reset() {
		// TODO: Write this
	}

	@Override
	public void load(OWLEntity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize() {
		this.add(new Label("This window specializes/edits classes!"));
	}

	@Override
	public void persistAndClose() {
		// TODO Auto-generated method stub
		this.hide();
	}
	
}
