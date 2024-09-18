package love.broccolai.beanstalk.service.profile.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.data.StorageService;
import org.jspecify.annotations.NullMarked;

@Singleton
@NullMarked
public final class ProfileDataProvider implements PartialProfileProvider {

    private final StorageService storageService;

    @Inject
    public ProfileDataProvider(final StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public Map<UUID, Profile> handleRequests(final List<UUID> requests) {
        Map<UUID, Profile> results = new HashMap<>();

        for (final UUID uuid : requests) {
            this.storageService
                .loadProfile(uuid)
                .ifPresent(profile -> results.put(uuid, profile));
        }

        return results;
    }

}
