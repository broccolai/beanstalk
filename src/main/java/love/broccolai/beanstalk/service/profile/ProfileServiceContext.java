package love.broccolai.beanstalk.service.profile;

import cloud.commandframework.services.ChunkedRequestContext;
import java.util.Collection;
import java.util.UUID;
import love.broccolai.beanstalk.model.profile.Profile;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class ProfileServiceContext extends ChunkedRequestContext<UUID, Profile> {

    ProfileServiceContext(final Collection<UUID> requests) {
        super(requests);
    }

}
