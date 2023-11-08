package love.broccolai.template.service.profile;

import cloud.commandframework.services.ServicePipeline;
import com.google.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import love.broccolai.template.model.profile.Profile;
import love.broccolai.template.service.profile.provider.PartialProfileProvider;
import love.broccolai.template.service.profile.provider.ProfileCacheProvider;
import love.broccolai.template.service.profile.provider.ProfileCreateProvider;
import love.broccolai.template.service.profile.provider.ProfileDataProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public class PipelineProfileService implements ProfileService {

    private final ServicePipeline pipeline = ServicePipeline.builder().build();
    private final ProfileCacheProvider cacheProvider;

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
            .getResult();

        this.cacheProvider.cache(results);

        return results;
    }

    @Override
    public Profile get(final String username) {
        return this.get(Bukkit.getOfflinePlayer(username).getUniqueId());
    }

    @Override
    public String name(final Profile profile) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(profile.uuid());
        return Objects.requireNonNull(player.getName());
    }

}
