package love.broccolai.beanstalk.service.profile.provider;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.Closeable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.data.StorageService;
import org.jspecify.annotations.NullMarked;

@Singleton
@NullMarked
public final class ProfileCacheProvider implements PartialProfileProvider, Closeable {

    private final StorageService storageService;

    private final Cache<UUID, Profile> uuidCache;

    @Inject
    public ProfileCacheProvider(final StorageService storageService) {
        this.storageService = storageService;
        this.uuidCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .<UUID, Profile>removalListener(notification -> this.storageService.saveProfile(notification.getValue()))
                .build();
    }

    @Override
    public Map<UUID, Profile> handleRequests(final List<UUID> requests) {
        Map<UUID, Profile> results = new HashMap<>();

        for (final UUID request : requests) {
            Profile profile = this.uuidCache.getIfPresent(request);

            if (profile != null) {
                results.put(request, profile);
            }
        }

        return results;
    }

    public void cache(final Map<UUID, Profile> entries) {
        this.uuidCache.putAll(entries);
    }

    @Override
    public void close() {
        this.uuidCache.asMap().values().forEach(this.storageService::saveProfile);
    }

}
