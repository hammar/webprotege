package edu.stanford.bmir.protege.web.client.xd;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Custom widget that displays ODP instantiations as labels with associated popup
 * menus. Used by {@link XdPatternInstancesPortlet}.
 * @author Karl Hammar <karl@karlhammar.com>
 *
 */
public class OdpInstantiationWidget extends Composite implements ContextMenuHandler {

	// Base widget is label.
	private Label label = new Label();
	private PopupPanel contextMenu;
	
	public OdpInstantiationWidget(String caption, String uri) {
		// Set up the base label widget
		label.setText(caption);
		label.setTitle(uri);
		label.addStyleName("xdUnselectable");
		initWidget(this.label);
		
		/* Commands to delete or bring up modification UI in details tab
		 * TODO: Inter-portlet communications need to be hooked up via custom
		 * listeners on XD Tab level for this to work. */
		Command cmdDelete = new Command() {
			public void execute() {
				Window.alert("You will now delete " + label.getTitle());
			}
		};
		Command cmdModify = new Command() {
			public void execute() {
				Window.alert("You will now modify " + label.getTitle());
			}
		}; 

		// Configure the popup menu
		this.contextMenu = new PopupPanel(true);
		this.contextMenu.addStyleName("xdInstantiationsPopup");
		MenuBar menu = new MenuBar(true);
		menu.addItem("Modify", cmdModify);
		menu.addItem("Delete", cmdDelete);
		this.contextMenu.setWidget(menu);
		this.contextMenu.hide();

		// of course it would be better if base would implement HasContextMenuHandlers, but the effect is the same
		addDomHandler(this, ContextMenuEvent.getType());
	}
	
	public void onContextMenu(ContextMenuEvent event) {
		// Stop the browser from opening the context menu
		event.preventDefault();
		event.stopPropagation();
		
		// Display the menu
		this.contextMenu.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
		this.contextMenu.show();
	}
}