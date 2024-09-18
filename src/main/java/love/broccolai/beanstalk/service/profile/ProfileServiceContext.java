package love.broccolai.beanstalk.service.profile;

import java.util.Collection;
import java.util.UUID;
import love.broccolai.beanstalk.model.profile.Profile;
import org.incendo.cloud.services.ChunkedRequestContext;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ProfileServiceContext extends ChunkedRequestContext<UUID, Profile> {

    ProfileServiceContext(final Collection<UUID> requests) {
        super(requests);
    }

}
