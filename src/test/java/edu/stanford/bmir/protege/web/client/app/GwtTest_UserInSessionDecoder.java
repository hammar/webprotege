package edu.stanford.bmir.protege.web.client.app;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.junit.client.GWTTestCase;
import edu.stanford.bmir.protege.web.client.TestResources;
import edu.stanford.bmir.protege.web.shared.app.UserInSession;
import edu.stanford.bmir.protege.web.shared.permissions.GroupId;
import edu.stanford.bmir.protege.web.shared.user.UserDetails;
import edu.stanford.bmir.protege.web.shared.user.UserId;


/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29/12/14
 */
public class GwtTest_UserInSessionDecoder extends GWTTestCase {

    public static final UserId EXPECTED_USER_ID = UserId.getUserId("JohnSmith");

    public static final String EXPECTED_DISPLAY_NAME = "John Smith";

    public static final String EXPECTED_EMAIL_ADDRESS = "john.smith@gmail.com";

    public static final ImmutableList<GroupId> EXPECTED_GROUPS =
            ImmutableList.of(
                    GroupId.get("X"),
                    GroupId.get("Y"),
                    GroupId.get("Z")
            );

    public static final UserInSession EXPECTED_SESSION_DATA = new UserInSession(
            UserDetails.getUserDetails(
                    EXPECTED_USER_ID,
                    EXPECTED_DISPLAY_NAME,
                    Optional.of(EXPECTED_EMAIL_ADDRESS)),
            EXPECTED_GROUPS
    );
    private String userInSessionJson;

    @Override
    public String getModuleName() {
        return "edu.stanford.bmir.protege.web.WebProtegeJUnit";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        delayTestFinish(10000);
        userInSessionJson = TestResources.INSTANCE.userInSessionJson().getText();
    }

    public void test_Decode() {
        UserInSessionDecoder decoder = new UserInSessionDecoder();
        UserInSession userInSession = decoder.decode(new JSONObject(
                JsonUtils.safeEval(userInSessionJson)
        ));
        assertEquals(userInSession, EXPECTED_SESSION_DATA);
        finishTest();
    }
}
