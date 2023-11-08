package love.broccolai.beanstalk.service.profile.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import love.broccolai.beanstalk.model.profile.Profile;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

/**
 * Final provider to use, creates the profile.
 */
@DefaultQualifier(NonNull.class)
public final class ProfileCreateProvider implements PartialProfileProvider {

    @Override
    public Map<UUID, Profile> handleRequests(final List<UUID> requests) {
        Map<UUID, Profile> results = new HashMap<>();

        for (UUID request : requests) {
            results.put(request, new Profile(request, -1));
        }

        return results;
    }

}
