package love.broccolai.beanstalk.service.action;

import com.google.inject.Inject;
import java.time.Duration;
import java.util.function.UnaryOperator;
import love.broccolai.beanstalk.event.FlightChangeEvent;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.action.result.FlyResult;
import love.broccolai.beanstalk.service.action.result.ModifyFlyResult;
import love.broccolai.beanstalk.service.event.EventService;
import love.broccolai.beanstalk.service.profile.ProfileService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public class EventActionService implements ActionService {

    private static final String PERMISSION_PREFIX = "beanstalk.fly.";

    private final ProfileService profileService;
    private final EventService eventService;

    @Inject
    public EventActionService(final ProfileService profileService, final EventService eventService) {
        this.profileService = profileService;
        this.eventService = eventService;
    }

    @Override
    public FlyResult fly(final Player player) {
        Profile profile = this.profileService.get(player.getUniqueId());

        if (profile.flightRemaining().isZero()) {
            return FlyResult.NO_FLIGHT_REMAINING;
        }

        if (profile.flying()) {
            return FlyResult.ALREADY_FLYING;
        }

        if (this.playerCanFlyInWorld(player)) {
            return FlyResult.NO_PERMISSION_IN_WORLD;
        }

        profile.flying(true);
        player.setAllowFlight(true);

        this.eventService.post(new FlightChangeEvent(profile));

        return FlyResult.SUCCESS;
    }

    @Override
    public ModifyFlyResult modifyFlight(final Profile profile, final UnaryOperator<Duration> modifier) {
        ModifyFlyResult result = ModifyFlyResult.SUCCESS;

        Duration modifiedDuration = modifier.apply(profile.flightRemaining());

        if (modifiedDuration.isNegative()) {
            result = ModifyFlyResult.CAPPED_TO_ZERO;
            modifiedDuration = Duration.ZERO;
        }

        profile.flightRemaining(modifiedDuration);
        this.stopFlyingIfDurationZero(profile);

        return result;
    }

    private boolean playerCanFlyInWorld(final Player player) {
        String world = player.getWorld().getName();

        return player.hasPermission(PERMISSION_PREFIX + world);
    }

    private void stopFlyingIfDurationZero(final Profile profile) {
        if (!profile.flightRemaining().isZero()) {
            return;
        }

        profile.flying(false);

        Player player = Bukkit.getPlayer(profile.uuid());

        if (player == null) {
            return;
        }

        player.setAllowFlight(false);
    }

}
