package edu.stanford.bmir.protege.web.client.xd.specialization.panels;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.widgets.Panel;

import edu.stanford.bmir.protege.web.shared.xd.data.alignment.Alignment;

public class AlignmentsPanel extends Panel {
	
	public AlignmentsPanel() {
		super();
		
        this.setBorder(false);  
        this.setId("card-3");
        this.setTitle("ODP Specialization Alignment");
        this.add(new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean in rhoncus sapien. Phasellus vitae magna at enim molestie auctor et at est. Nullam consequat odio eu varius venenatis. Etiam aliquet ligula ac posuere lacinia. Curabitur aliquet elit a viverra fringilla. "));
        

        // TODO: Do awesome stuff here.
	}
	
	public Set<Alignment> getSelectedAlignments() {
		// TODO: implement me
		return new HashSet<Alignment>();
	}
}
