package edu.stanford.bmir.protege.web.client.xd;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;

public class OdpInstantiationWidget extends Composite implements ContextMenuHandler {
	  
	  // just an example, use a meaningful Widget here...
	  private Label label = new Label();
	 
	  private PopupPanel contextMenu;
	 
	  public OdpInstantiationWidget(String caption, String uri) {
		  label.setText(caption);
		  label.setTitle(uri);
		  label.addStyleName("xdUnselectable");
	    // initialize base widget, etc...
		  initWidget(this.label);
		  
		  Command cmd = new Command() {
		      public void execute() {
		        Window.alert("You selected a menu item!");
		      }
		    };
		  
	    this.contextMenu = new PopupPanel(true);
	    this.contextMenu.addStyleName("xdInstantiationsPopup");
	    MenuBar menu = new MenuBar(true);
	    menu.addItem("Modify", cmd);
	    menu.addItem("Delete", cmd);
	    this.contextMenu.setWidget(menu);
	    
	    this.contextMenu.hide();
	 
	    // of course it would be better if base would implement HasContextMenuHandlers, but the effect is the same
	    addDomHandler(this, ContextMenuEvent.getType());
	  }
	 
	 
	  public void onContextMenu(ContextMenuEvent event) {
	    // stop the browser from opening the context menu
	    event.preventDefault();
	    event.stopPropagation();
	 
	 
	    this.contextMenu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
	    this.contextMenu.show();
	  }
	}