package love.broccolai.template.service.profile.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import love.broccolai.template.model.profile.Profile;
import love.broccolai.template.service.data.StorageService;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@Singleton
@DefaultQualifier(NonNull.class)
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
