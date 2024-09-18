package love.broccolai.beanstalk.service.profile.provider;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import love.broccolai.beanstalk.model.profile.FlightStatus;
import love.broccolai.beanstalk.model.profile.Profile;
import org.jspecify.annotations.NullMarked;

/**
 * Final provider to use, creates the profile.
 */
@NullMarked
public final class ProfileCreateProvider implements PartialProfileProvider {

    @Override
    public Map<UUID, Profile> handleRequests(final List<UUID> requests) {
        Map<UUID, Profile> results = new HashMap<>();

        for (UUID request : requests) {
            results.put(request, new Profile(request, Duration.ZERO, FlightStatus.DISABLED));
        }

        return results;
    }

}
