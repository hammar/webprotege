package edu.stanford.bmir.protege.web.server.metaproject;

import com.google.common.base.Optional;
import edu.stanford.bmir.protege.web.server.user.HasUserIds;
import edu.stanford.bmir.protege.web.shared.user.UserDetails;
import edu.stanford.bmir.protege.web.shared.user.UserId;

import java.util.Collection;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 06/02/15
 */
public interface UserDetailsManager extends HasUserIds, HasGetUserByUserIdOrEmail, HasGetUserIdByEmailAddress {

    @Override
    Collection<UserId> getUserIds();

    Optional<UserDetails> getUserDetails(UserId userId);

    Optional<String> getEmail(UserId userId);

    void setEmail(UserId userId, String email);


}
