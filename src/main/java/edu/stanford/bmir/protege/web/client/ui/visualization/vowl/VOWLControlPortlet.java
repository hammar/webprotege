package edu.stanford.bmir.protege.web.client.ui.visualization.vowl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.ui.portlet.AbstractOWLEntityPortlet;
import edu.stanford.bmir.protege.web.client.ui.visualization.composites.ExtendedMenuItem;
import edu.stanford.bmir.protege.web.client.ui.visualization.selection.Selectable;
import edu.stanford.bmir.protege.web.client.ui.visualization.selection.SelectionEvent;
import edu.stanford.bmir.protege.web.client.ui.visualization.selection.SelectionListener;
import edu.stanford.bmir.protege.web.shared.selection.SelectionModel;

@SuppressWarnings("unchecked")
public class VOWLControlPortlet extends AbstractOWLEntityPortlet implements Selectable, KeyPressHandler {

	private static final String CONTROL_TITLE = "Control Bar";
	private MenuBar controlBar;
	// Listeners to selection events in this portlet
	private Collection<SelectionListener> listeners;
	private boolean paused = false;
	private String option = null;
	private Collection<? extends Object> selection;
	private FlowPanel panel;
	private ExtendedMenuItem cmi, dataProp, solSub, disjInfo, setOp, pickPin, nodeScale, 
	compNotation, classDistance, datatypeDistance, collapseDegree;
	private Command cmd;
	private Map<String, Object> map = new HashMap<String,Object>();
	private TextBox textBox;
	private MyPopup mp;

	public VOWLControlPortlet(SelectionModel selectionModel, Project project) {
		super(selectionModel, project);
		this.listeners = new ArrayList<SelectionListener>();
	}

	@Override
	public void initialize() {
		setTitle(CONTROL_TITLE);
		panel = new FlowPanel();
		controlBar = new MenuBar(false);
		controlBar.setAutoOpen(true);
		panel.add(controlBar);
		createMenuItems();
		add(panel);
	}

	private void addSelectedComponent(ExtendedMenuItem item){
		//listeners.clear();
		//vPanel = item.getPanel();
		textBox = item.getTextBox();
		textBox.addKeyPressHandler(this);
		mp = new MyPopup(textBox);
		mp.setPopupPosition(10, 180);
		mp.show();

	}

	private void createMenuItems() {
		controlBar.moveSelectionDown();

		MenuBar exportMenu = new MenuBar(true);
		controlBar.addItem("Export", exportMenu);
		controlBar.addSeparator();

		MenuBar gravityMenu = new MenuBar(true);
		controlBar.addItem("Gravity", gravityMenu);
		controlBar.addSeparator();

		MenuBar filterMenu = new MenuBar(true);
		controlBar.addItem("Filter", filterMenu);
		controlBar.addSeparator();

		MenuBar modesMenu = new MenuBar(true);
		controlBar.addItem("Modes", modesMenu);
		controlBar.addSeparator();

		//MenuBar resetMenu = new MenuBar(true);
		controlBar.addItem("Reset", new Command() {
			public void execute() {
				selection = Arrays.asList("reset");
				notifySelectionListeners(new SelectionEvent(VOWLControlPortlet.this));
			}
		});
		controlBar.addSeparator();

		//MenuBar pauseMenu = new MenuBar(true);
		controlBar.addItem("Pause", new Command() {
			public void execute() {
				selection = Arrays.asList(new String[]{"pause", String.valueOf(paused?"false":"true")});
				notifySelectionListeners(new SelectionEvent(VOWLControlPortlet.this));
				
				if(paused)
					paused = false;
				else
					paused = true;
				

			}
		});
		controlBar.addSeparator();

		MenuBar aboutMenu = new MenuBar(true);
		controlBar.addItem("About", aboutMenu);

		exportMenu.addItem("Export as SVG", new Command() {
			public void execute() {
				;
			}
		});
		exportMenu.addItem("Export as JSON", new Command() {
			public void execute() {
				;
			}
		});
		controlBar.addSeparator();

		TextBox tb = new TextBox();
		tb.setName("classDistance");
		tb.setValue("200");
		tb.setTitle("Class Distance");
		classDistance = new ExtendedMenuItem(new Command() {

			public void execute() {
				addSelectedComponent(classDistance);
			}}, tb);

		gravityMenu.addItem(classDistance);


		TextBox tb2 = new TextBox();
		tb2.setName("datatypeDistance");
		tb2.setValue("120");
		tb2.setTitle("Datatype Distance");
		datatypeDistance = new ExtendedMenuItem(new Command() {

			public void execute() {
				addSelectedComponent(datatypeDistance);
			}}, tb2);
		gravityMenu.addItem(datatypeDistance);

		controlBar.addSeparator();

		cmd = new Command() {
			public void execute() {
				//addSelectedComponent(dataProp);
				if (dataProp.isEnabled())
					dataProp.setEnabled(false);
				else
					dataProp.setEnabled(true);
			}
		};

		dataProp = new ExtendedMenuItem("<input type=\"checkbox\" name=\"dataProp\" value=\"0\"> Datatype prop.", true, cmd);
		filterMenu.addItem(dataProp);


		cmd = new Command() {
			public void execute() {
				//addSelectedComponent(dataProp);
				if (solSub.isEnabled())
					solSub.setEnabled(false);
				else
					solSub.setEnabled(true);
			}
		};

		solSub = new ExtendedMenuItem("<input type=\"checkbox\" name=\"solSub\" value=\"0\"> Solitary subclass", true, cmd);
		filterMenu.addItem(solSub);

		/*
		filterMenu.addItem("Solitary subclass", new Command() {
			public void execute() {
				;
			}
		});
		 */

		cmd = new Command() {
			public void execute() {
				//addSelectedComponent(dataProp);
				if (disjInfo.isEnabled())
					disjInfo.setEnabled(false);
				else
					disjInfo.setEnabled(true);
			}
		};

		disjInfo = new ExtendedMenuItem("<input type=\"checkbox\" name=\"disjInfo\" value=\"1\" checked> Disjointness info", true, cmd);
		filterMenu.addItem(disjInfo);


		cmd = new Command() {
			public void execute() {
				//addSelectedComponent(dataProp);
				if (setOp.isEnabled())
					setOp.setEnabled(false);
				else
					setOp.setEnabled(true);
			}
		};

		setOp = new ExtendedMenuItem("<input type=\"checkbox\" name=\"setOp\" value=\"0\"> Set operators", true, cmd);
		filterMenu.addItem(setOp);

		TextBox tb3 = new TextBox();
		tb3.setName("collapseDegree");
		tb3.setValue("0");
		tb3.setTitle("Degree of collapsing");
		collapseDegree = new ExtendedMenuItem(new Command() {

			public void execute() {
				addSelectedComponent(collapseDegree);
			}}, tb3);
		filterMenu.addItem(collapseDegree);

		controlBar.addSeparator();

		cmd = new Command() {
			public void execute() {
				//addSelectedComponent(dataProp);

				if (pickPin.isEnabled())
					pickPin.setEnabled(false);
				else
					pickPin.setEnabled(true);
				
				selection = Arrays.asList("pickPin");
				notifySelectionListeners(new SelectionEvent(VOWLControlPortlet.this));
			}
		};

		pickPin = new ExtendedMenuItem("<input type=\"checkbox\" name=\"pickPin\" value=\"0\"> Pick & Pin", true, cmd);
		modesMenu.addItem(pickPin);


		cmd = new Command() {
			public void execute() {
				//addSelectedComponent(dataProp);
				if (nodeScale.isEnabled())
					nodeScale.setEnabled(false);
				else
					nodeScale.setEnabled(true);
			}
		};

		nodeScale = new ExtendedMenuItem("<input type=\"checkbox\" name=\"nodeScale\" value=\"1\" checked> Node Scaling", true, cmd);
		modesMenu.addItem(nodeScale);

		cmd = new Command() {
			public void execute() {
				//addSelectedComponent(dataProp);
				if (compNotation.isEnabled())
					compNotation.setEnabled(false);
				else
					compNotation.setEnabled(true);
			}
		};

		compNotation = new ExtendedMenuItem("<input type=\"checkbox\" name=\"compNotation\" value=\"0\"> Compact Notation", true, cmd);
		modesMenu.addItem(compNotation);

		controlBar.addSeparator();
		aboutMenu.addItem("<a target=\"_blank\" href=\"http://vowl.visualdataweb.org/webvowl/license.txt\">MIT License © 2014/15</a>", true, new Command() {
			public void execute() {
				;
			}
		});
		aboutMenu.addItem("WebVOWL Developers:\r\n" + 
				"Vincent Link, Steffen Lohmann, Eduard Marbach, Stefan Negru", new Command() {
			public void execute() {
				;
			}
		});
		aboutMenu.addSeparator();
		aboutMenu.addItem("<a target=\"_blank\" href=\"http://vowl.visualdataweb.org/webvowl.html#releases\">\r\n" + 
				"Version: beta 0.4.0\r\n" + 
				"<br>\r\n" + 
				"(release history)\r\n" + 
				"</a>", true, new Command() {
			public void execute() {
				;
			}
		});
		aboutMenu.addSeparator();
		aboutMenu.addItem("<a target=\"_blank\" href=\"http://purl.org/vowl/\">VOWL Specification »</a>\r\n", true, new Command() {
			public void execute() {
				;
			}
		});
	}


	@Override
	public void addSelectionListener(SelectionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void notifySelectionListeners(SelectionEvent clickEvent) {
		for (SelectionListener listener: listeners) {
			listener.selectionChanged(new SelectionEvent(this));
		}
	}

	@Override
	public void removeSelectionListener(SelectionListener listener) {
		listeners.remove(listener);
	}

	@Override
	public Collection<? extends Object> getSelection() {
		return selection;
	}

	@Override
	public VerticalPanel getPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSelection(Collection<? extends Object> selection) {
		this.selection = selection;

	}

	@Override
	public void onKeyPress(KeyPressEvent event) {

		Object sender = event.getSource();
		if (sender instanceof TextBox && event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
			TextBox textBox = (TextBox)event.getSource();
			selection = Arrays.asList(new String[]{textBox.getName(), textBox.getValue()});
			notifySelectionListeners(new SelectionEvent(VOWLControlPortlet.this));
			//mp.clear();
			mp.hide();
		}

	}

	private class MyPopup extends PopupPanel {

		public MyPopup(TextBox tb) {
			// PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
			// If this is set, the panel closes itself automatically when the user
			// clicks outside of it.
			super(true);

			// PopupPanel is a SimplePanel, so you have to set it's widget property to
			// whatever you want its contents to be.
			//setWidget(new Label("Click outside of this popup to close it"));

			setWidget(tb);
			//setWidget(new Label("Click outside of this popup to close it"));
		}
	}



}
