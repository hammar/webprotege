
package edu.stanford.bmir.protege.web.server.itemlist;

import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.metaproject.UserDetailsManager;
import edu.stanford.bmir.protege.web.shared.itemlist.GetPossibleItemCompletionsResult;
import edu.stanford.bmir.protege.web.shared.itemlist.GetUserIdCompletionsAction;
import edu.stanford.bmir.protege.web.shared.user.UserId;
import edu.stanford.smi.protege.server.metaproject.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class GetUserIdCompletionsActionHandler_TestCase {

    private GetUserIdCompletionsActionHandler actionHandler;

    @Mock
    private UserDetailsManager userDetailsManager;

    private List<UserId> userIds;

    @Mock
    private UserId johnSmith, janeDoe;

    @Mock
    private GetUserIdCompletionsAction action;

    @Before
    public void setUp()
        throws Exception
    {
        actionHandler = new GetUserIdCompletionsActionHandler(userDetailsManager);
        userIds = Arrays.asList(johnSmith, janeDoe);
        when(userDetailsManager.getUserIds()).thenReturn(userIds);
        when(johnSmith.getUserName()).thenReturn("John Smith");
        when(janeDoe.getUserName()).thenReturn("Jane Doe");
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void shouldThrowNullPointerExceptionIf_userDetailsManager_IsNull() {
        new GetUserIdCompletionsActionHandler(null);
    }

    @Test
    public void shouldIgnoreCase() {
        when(action.getCompletionText()).thenReturn("j");
        GetPossibleItemCompletionsResult<UserId> result = actionHandler.execute(action, mock(ExecutionContext.class));
        assertThat(result.getPossibleItemCompletions(), hasItems(janeDoe, johnSmith));
    }

    @Test
    public void shouldMatchWithinUserName() {
        when(action.getCompletionText()).thenReturn("Doe");
        GetPossibleItemCompletionsResult<UserId> result = actionHandler.execute(action, mock(ExecutionContext.class));
        assertThat(result.getPossibleItemCompletions(), hasItem(janeDoe));
    }
}
