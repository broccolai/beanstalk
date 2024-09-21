package love.broccolai.beanstalk.expansion;

import com.google.inject.Inject;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.message.MessageService;
import love.broccolai.beanstalk.service.profile.ProfileService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@NullMarked
public final class PlaceholderAPIExpansion extends PlaceholderExpansion implements BeanstalkExpansion {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer
        .builder()
        .useUnusualXRepeatedCharacterHexFormat()
        .build();

    private final Plugin plugin;
    private final Logger logger;
    private final ProfileService profileService;
    private final MessageService messageService;
    private final Map<String, Function<OfflinePlayer, String>> placeholders = new HashMap<>();

    @Inject
    public PlaceholderAPIExpansion(
        final Plugin plugin,
        final Logger logger,
        final ProfileService profileService,
        final MessageService messageService
    ) {
        this.plugin = plugin;
        this.logger = logger;
        this.profileService = profileService;
        this.messageService = messageService;

        this.placeholders.put("status", this::status);
        this.placeholders.put("remaining", this::remaining);
    }

    @Override
    public void apply() {
        this.register();
        this.logger.info("Registered PlaceholderAPI expansion");
    }

    @Override
    public @Nullable String onRequest(final OfflinePlayer player, final String params) {
        if (!this.placeholders.containsKey(params)) {
            return null;
        }

        return this.placeholders.get(params).apply(player);
    }

    private String status(final OfflinePlayer player) {
        Profile profile = this.profileService.get(player.getUniqueId());

        Component component = this.messageService.statusTag(
            profile.flightStatus(),
            profile.flightRemaining()
        );

        return SERIALIZER.serialize(component);
    }

    private String remaining(final OfflinePlayer player) {
        Profile profile = this.profileService.get(player.getUniqueId());

        Component component = this.messageService.remainingTag(
            profile.flightRemaining()
        );

        return SERIALIZER.serialize(component);
    }

    @Override
    public String getIdentifier() {
        return this.plugin.getName();
    }

    @Override
    public String getAuthor() {
        return "broccolai";
    }

    @Override
    public String getVersion() {
        return this.plugin.getPluginMeta().getVersion();
    }

}
