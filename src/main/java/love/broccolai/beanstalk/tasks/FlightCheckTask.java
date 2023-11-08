package love.broccolai.beanstalk.tasks;

import com.google.inject.Inject;
import java.time.Duration;
import java.util.Collection;
import java.util.UUID;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.message.MessageService;
import love.broccolai.beanstalk.service.profile.ProfileService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public class FlightCheckTask {

    private static final int ONE_SECOND_OF_TICKS = 20;
    private static final Duration ONE_SECOND_DURATION = Duration.ofSeconds(1);
    private static final Duration ONE_MINUTE_DURATION = Duration.ofSeconds(1);

    private final ProfileService profileService;
    private final MessageService messageService;
    private final Plugin plugin;

    @Inject
    public FlightCheckTask(
        final ProfileService profileService,
        final MessageService messageService,
        final Plugin plugin
    ) {
        this.profileService = profileService;
        this.messageService = messageService;
        this.plugin = plugin;
    }

    public void register() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(
            this.plugin,
            this::run,
            ONE_SECOND_OF_TICKS,
            ONE_SECOND_OF_TICKS
        );
    }

    private void run() {
        Collection<UUID> onlinePlayerIds = Bukkit.getOnlinePlayers()
            .stream()
            .map(Player::getUniqueId)
            .toList();

        this.profileService.get(onlinePlayerIds)
            .values()
            .stream()
            .filter(Profile::flying)
            .forEach(this::checkPlayer);
    }

    private void checkPlayer(final Profile profile) {
        @Nullable Player player = Bukkit.getPlayer(profile.uuid());

        if (player == null) {
            return;
        }

        if (player.isFlying()) {
            return;
        }

        Duration newRemaining = profile.flightRemaining(current -> current.minus(ONE_SECOND_DURATION));

        if (!newRemaining.isZero()) {
            return;
        }

        if (ONE_MINUTE_DURATION.equals(newRemaining)) {
            this.messageService.minuteRemaining(player);
        }

        profile.flying(false);
        this.messageService.disable(player);
    }

}
