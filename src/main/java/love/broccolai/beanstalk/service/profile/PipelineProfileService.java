package love.broccolai.beanstalk.service.profile;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.profile.provider.PartialProfileProvider;
import love.broccolai.beanstalk.service.profile.provider.ProfileCacheProvider;
import love.broccolai.beanstalk.service.profile.provider.ProfileCreateProvider;
import love.broccolai.beanstalk.service.profile.provider.ProfileDataProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.services.ServicePipeline;

@Singleton
@DefaultQualifier(NonNull.class)
public class PipelineProfileService implements ProfileService {

    private static final String UNKNOWN_PLAYER = "UNKNOWN";

    private final ServicePipeline pipeline = ServicePipeline.builder().build();
    private final ProfileCacheProvider cacheProvider;

    private final Cache<UUID, String> profileNameCache = Caffeine.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build();

    @Inject
    public PipelineProfileService(
        final ProfileCreateProvider createProvider,
        final ProfileDataProvider dataProvider,
        final ProfileCacheProvider cacheProvider
    ) {
        this.cacheProvider = cacheProvider;

        this.pipeline
            .registerServiceType(PartialProfileProvider.TYPE, createProvider)
            .registerServiceImplementation(
                PartialProfileProvider.TYPE,
                dataProvider,
                Collections.emptyList()
            )
            .registerServiceImplementation(
                PartialProfileProvider.TYPE,
                cacheProvider,
                Collections.emptyList()
            );
    }

    @Override
    public final Profile get(final UUID uniqueId) {
        return this.get(Collections.singletonList(uniqueId)).get(uniqueId);
    }

    @Override
    public final Map<UUID, Profile> get(final Collection<UUID> uniqueIds) {
        Map<UUID, Profile> results = this.pipeline.pump(new ProfileServiceContext(uniqueIds))
            .through(PartialProfileProvider.TYPE)
            .complete();

        this.cacheProvider.cache(results);

        return results;
    }

    @Override
    public Profile get(final String username) {
        return this.get(Bukkit.getOfflinePlayer(username).getUniqueId());
    }

    @Override
    public String name(final Profile profile) {
        return this.profileNameCache.get(profile.uuid(), uuid -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(profile.uuid());
            return Objects.requireNonNullElse(player.getName(), UNKNOWN_PLAYER);
        });
    }

}
