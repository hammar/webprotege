package edu.stanford.bmir.protege.web.client.ui;

import com.google.common.base.Optional;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.event.TabPanelListenerAdapter;
import edu.stanford.bmir.protege.web.client.Application;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceCallbackWithProgressDisplay;
import edu.stanford.bmir.protege.web.client.events.UserLoggedOutEvent;
import edu.stanford.bmir.protege.web.client.events.UserLoggedOutHandler;
import edu.stanford.bmir.protege.web.client.place.PlaceManager;
import edu.stanford.bmir.protege.web.client.place.ProjectListPlace;
import edu.stanford.bmir.protege.web.client.place.ProjectViewPlace;
import edu.stanford.bmir.protege.web.client.project.ActiveProjectChangedEvent;
import edu.stanford.bmir.protege.web.client.project.ActiveProjectChangedHandler;
import edu.stanford.bmir.protege.web.client.project.Project;
import edu.stanford.bmir.protege.web.client.project.ProjectManager;

import edu.stanford.bmir.protege.web.client.ui.ontology.home.MyWebProtegeTab;
import edu.stanford.bmir.protege.web.client.ui.projectmanager.LoadProjectRequestHandler;
import edu.stanford.bmir.protege.web.shared.event.EventBusManager;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.projectsettings.ProjectSettings;
import edu.stanford.bmir.protege.web.shared.projectsettings.ProjectSettingsChangedEvent;
import edu.stanford.bmir.protege.web.shared.projectsettings.ProjectSettingsChangedHandler;

import java.util.LinkedHashMap;

/**
 * Class that holds all the tabs corresponding to ontologies. It also contains
 * that MyWebProtege Tab. This class manages the loading of projects and their
 * configurations.
 * @author Tania Tudorache <tudorache@stanford.edu>
 */
public class ProjectDisplayContainerPanel extends TabPanel {

    private MyWebProtegeTab myWebProTab;

    private final LinkedHashMap<ProjectId, ProjectDisplayImpl> projectId2ProjectPanelMap = new LinkedHashMap<ProjectId, ProjectDisplayImpl>();

    public ProjectDisplayContainerPanel() {
        super();
        buildUI();

        displayCurrentPlace();


        this.addListener(new TabPanelListenerAdapter() {
             @Override
            public void onTabChange(TabPanel source, Panel tab) {
                transmitActiveProject();
            }
        });


        EventBusManager.getManager().registerHandler(UserLoggedOutEvent.TYPE, new UserLoggedOutHandler() {
            @Override
            public void handleUserLoggedOut(UserLoggedOutEvent event) {
                // TODO: This is a bit extreme.  We only need to remove the tabs for which the user had no access rights
                removeAllTabs();
            }
        });

        EventBusManager.getManager().registerHandler(ActiveProjectChangedEvent.TYPE, new ActiveProjectChangedHandler() {
            @Override
            public void handleActiveProjectChanged(ActiveProjectChangedEvent event) {
                respondToActiveProjectChangedEvent(event);
            }
        });

        EventBusManager.getManager().registerHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
            @Override
            public void onPlaceChange(PlaceChangeEvent event) {
                displayCurrentPlace();
            }
        });

        EventBusManager.getManager().registerHandler(ProjectSettingsChangedEvent.getType(), new ProjectSettingsChangedHandler() {
            @Override
            public void handleProjectSettingsChanged(ProjectSettingsChangedEvent event) {
                ProjectSettings projectSettings = event.getProjectSettings();
                ProjectDisplayImpl projectDisplay = projectId2ProjectPanelMap.get(projectSettings.getProjectId());
                projectDisplay.setTitle(projectSettings.getProjectDisplayName());
            }
        });

//        Application.get().getPlaceManager().setCurrentPlace(ProjectListPlace.DEFAULT_PLACE);
    }


    private Optional<ProjectId> getProjectIdForActiveTab() {
        Panel activeTab = getActiveTab();
        final Optional<ProjectId> projectId;
        if(activeTab instanceof ProjectDisplay) {
            projectId = Optional.of(((ProjectDisplay) activeTab).getProjectId());
        }
        else {
            projectId = Optional.absent();
        }
        return projectId;
    }


    private void transmitActiveProject() {
        final Optional<ProjectId> projectId = getProjectIdForActiveTab();
        Application.get().setActiveProject(projectId);
        if (projectId.isPresent()) {
            Application.get().getPlaceManager().setCurrentPlace(new ProjectViewPlace(projectId.get()));
        }
        else {
            Application.get().getPlaceManager().setCurrentPlace(ProjectListPlace.DEFAULT_PLACE);
        }
    }



    private void respondToActiveProjectChangedEvent(final ActiveProjectChangedEvent event) {
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable reason) {
            }

            @Override
            public void onSuccess() {
                Optional<ProjectId> projectId = event.getProjectId();
                if(!projectId.isPresent()) {
                    // Go home
                    setActiveTab(0);
                }
                else {
                    ProjectDisplayImpl display = projectId2ProjectPanelMap.get(projectId.get());
                    if (display != null) {
                        setActiveTab(display.getLabel());
                    }
                }
            }
        });
    }

    private void displayCurrentPlace() {
        PlaceManager placeManager = Application.get().getPlaceManager();
        Place currentPlace = placeManager.getCurrentPlace();
        if (currentPlace instanceof ProjectListPlace) {
            setActiveTab(0);
        }
        else if(currentPlace instanceof ProjectViewPlace) {
            ProjectViewPlace projectViewPlace = (ProjectViewPlace) currentPlace;
            loadProject(projectViewPlace.getProjectId());
        }
    }

    private void buildUI() {
        setLayoutOnTabChange(true); // TODO: check if necessary
        createAndAddHomeTab();
    }

    private void removeAllTabs() {
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable reason) {
            }

            @Override
            public void onSuccess() {
                for (ProjectDisplayImpl ontologyTabPanel : projectId2ProjectPanelMap.values()) {
                    Project project = ontologyTabPanel.getProject();
                    ProjectManager.get().unloadProject(project.getProjectId());
                    hideTabStripItem(ontologyTabPanel);
                    ontologyTabPanel.hide();
                    ontologyTabPanel.destroy();
                }
                projectId2ProjectPanelMap.clear();
                activate(0);
            }
        });

    }


    private void createAndAddHomeTab() {

        LoadProjectRequestHandler loadProjectRequestHandler = new LoadProjectRequestHandler() {
            @Override
            public void handleProjectLoadRequest(final ProjectId projectId) {
                GWT.runAsync(new RunAsyncCallback() {
                    @Override
                    public void onFailure(Throwable reason) {
                    }

                    @Override
                    public void onSuccess() {
                        loadProject(projectId);
                    }
                });
            }
        };

        myWebProTab = new MyWebProtegeTab(loadProjectRequestHandler);
//        myWebProTab.setTitle(myWebProTab.getLabel());
        add(myWebProTab);



    }


    private void loadProject(final ProjectId projectId) {
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable reason) {
            }

            @Override
            public void onSuccess() {
                ProjectDisplayImpl ontTab = projectId2ProjectPanelMap.get(projectId);
                if (ontTab != null) {
                    activate(ontTab.getId());
                }
                else {
                    Application.get().loadProject(projectId, new DispatchServiceCallbackWithProgressDisplay<Project>() {
                        @Override
                        public String getProgressDisplayTitle() {
                            return "Loading project";
                        }

                        @Override
                        public String getProgressDisplayMessage() {
                            return "Please wait.";
                        }

                        @Override
                        public void handleSuccess(Project project) {
                            addProjectDisplay(projectId);
                        }

                        @Override
                        protected String getErrorMessageTitle() {
                            return "Error";
                        }

                        @Override
                        protected String getErrorMessage(Throwable throwable) {
                            return "There was an error whilst loading the project.  Please try again.";
                        }
                    });
                }
            }
        });


    }

    private void addProjectDisplay(final ProjectId projectId) {
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable reason) {
            }

            @Override
            public void onSuccess() {
                ProjectDisplayImpl projectPanel = new ProjectDisplayImpl(projectId);
                projectPanel.setClosable(true);
                projectId2ProjectPanelMap.put(projectId, projectPanel);

                projectPanel.addListener(new PanelListenerAdapter() {
                    @Override
                    public boolean doBeforeDestroy(Component component) {
                        if (component instanceof ProjectDisplayImpl) {
                            ProjectDisplayImpl o = (ProjectDisplayImpl) component;
                            ProjectId projectId = o.getProjectId();
                            projectId2ProjectPanelMap.remove(projectId);
                            ProjectManager.get().unloadProject(projectId);
                            hideTabStripItem(o);
                            o.hide();
                            activate(0);
                        }
                        Application.get().setActiveProject(Optional.<ProjectId>absent());
                        return true;
                    }
                });

                add(projectPanel);
                activate(projectPanel.getId());
                setActiveTab(projectPanel.getId());
                projectPanel.layoutProject();
            }
        });
    }
}
