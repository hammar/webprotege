package edu.stanford.bmir.protege.web.client.dispatch;

import com.google.common.base.Optional;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.web.bindery.event.shared.Event;
import edu.stanford.bmir.protege.web.client.dispatch.cache.ResultCache;
import edu.stanford.bmir.protege.web.client.renderer.RenderingManager;
import edu.stanford.bmir.protege.web.client.ui.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.shared.BrowserTextMap;
import edu.stanford.bmir.protege.web.shared.HasBrowserTextMap;
import edu.stanford.bmir.protege.web.shared.HasProjectId;
import edu.stanford.bmir.protege.web.shared.dispatch.Action;
import edu.stanford.bmir.protege.web.shared.dispatch.DispatchServiceResultContainer;
import edu.stanford.bmir.protege.web.shared.dispatch.InvocationExceptionTolerantAction;
import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.event.EventBusManager;
import edu.stanford.bmir.protege.web.shared.event.HasEventList;
import edu.stanford.bmir.protege.web.shared.event.SerializableEvent;
import edu.stanford.bmir.protege.web.shared.events.EventList;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 20/01/2013
 */
public class DispatchServiceManager {

    private static DispatchServiceManager instance;


    private DispatchServiceAsync async;


    private DispatchServiceManager() {
        async = GWT.create(DispatchService.class);
    }


    public static DispatchServiceManager get() {
        if(instance == null) {
            instance = new DispatchServiceManager();
        }
        return instance;
    }

    private int requestCount;

    private Map<ProjectId, ResultCache> resultCacheMap = new HashMap<ProjectId, ResultCache>();

    private ResultCache getResultCache(ProjectId projectId) {
        ResultCache resultCache = resultCacheMap.get(projectId);
        if(resultCache == null) {
            resultCache = new ResultCache(projectId);
            resultCacheMap.put(projectId, resultCache);
        }
        return resultCache;
    }

    @SuppressWarnings("unchecked")
    public <A extends Action<R>, R extends Result> void execute(A action, final DispatchServiceCallback<R> callback) {
        callback.handleSubmittedForExecution();
        if(action instanceof HasProjectId) {
            ProjectId projectId = ((HasProjectId) action).getProjectId();
            ResultCache resultCache = getResultCache(projectId);
            Optional<R> result = resultCache.getCachedResult(action);
            if (result.isPresent()) {
                GWT.log("[DISPATCH] Using cached result (" + action + ")");
                callback.onSuccess(result.get());
                return;
            }
        }
        GWT.log("[DISPATCH] Making request to server.  Request " + requestCount + ". (" + action + ")");
        requestCount++;
        async.executeAction(action, new AsyncCallbackProxy(action, callback));
    }


    private class AsyncCallbackProxy<R extends Result> implements AsyncCallback<DispatchServiceResultContainer> {

        private Action<?> action;

        private DispatchServiceCallback<Result> delegate;

        public AsyncCallbackProxy(Action<?> action, DispatchServiceCallback<Result> delegate) {
            this.delegate = delegate;
            this.action = action;
        }

        @Override
        public void onFailure(Throwable caught) {
            handleError(caught, action, delegate);
        }

        @Override
        public void onSuccess(DispatchServiceResultContainer result) {
            // TODO: Fix
            if(action instanceof HasProjectId) {
                ResultCache resultCache = getResultCache(((HasProjectId) action).getProjectId());
                resultCache.cacheResult((Action<R>) action, (R) result.getResult());
            }
            cacheRenderables(result.getResult());
            dispatchEvents(result.getResult());
            delegate.onSuccess(result.getResult());
        }

    }


    private void cacheRenderables(Object result) {
        if (result instanceof HasBrowserTextMap) {
            BrowserTextMap browserTextMap = ((HasBrowserTextMap) result).getBrowserTextMap();
            RenderingManager.getManager().registerEntityData(browserTextMap.getOWLEntityData());
        }
    }

    private void dispatchEvents(Object result) {
        if(result instanceof HasEventList<?>) {
            EventList<? extends SerializableEvent<?>> eventList = ((HasEventList<? extends SerializableEvent<?>>) result).getEventList();

            List<? extends SerializableEvent<?>> events = eventList.getEvents();
            // TODO: FIX - Should be dispatched by the project event manager otherwise we will get events from the
            // TODO: more than once!

            for(Event<?> event : events) {
                GWT.log("[DISPATCH] Dispatching event (" + event.toDebugString() + ")");
            }
            EventBusManager.getManager().postEvents(events);
        }
    }

    private void handleError(Throwable throwable, Action<?> action, DispatchServiceCallback<?> callback) {
        // Skip handling for actions that do not care about errors
        if(action instanceof InvocationExceptionTolerantAction) {
            Optional<String> errorMessage = ((InvocationExceptionTolerantAction) action).handleInvocationException((InvocationException) throwable);
            if(errorMessage.isPresent()) {
                displayAlert(errorMessage.get());
            }
            return;
        }
        callback.onFailure(throwable);
    }

    private void displayAlert(String alert) {
        MessageBox.showAlert(alert);
    }
}
