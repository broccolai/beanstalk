package love.broccolai.beanstalk.service.action;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.time.Duration;
import java.util.function.UnaryOperator;
import love.broccolai.beanstalk.event.FlightChangeEvent;
import love.broccolai.beanstalk.model.profile.FlightStatus;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.action.result.ModifyFlightDurationResult;
import love.broccolai.beanstalk.service.action.result.ModifyFlightResult;
import love.broccolai.beanstalk.service.event.EventService;
import love.broccolai.beanstalk.service.profile.ProfileService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@Singleton
@DefaultQualifier(NonNull.class)
public class EventActionService implements ActionService {

    private final ProfileService profileService;
    private final EventService eventService;

    @Inject
    public EventActionService(final ProfileService profileService, final EventService eventService) {
        this.profileService = profileService;
        this.eventService = eventService;
    }

    @Override
    public ModifyFlightResult modifyFly(final Player player, final FlightStatus status) {
        Profile profile = this.profileService.get(player.getUniqueId());

        if (status == FlightStatus.ENABLED && profile.flightRemaining().isZero()) {
            return ModifyFlightResult.NO_FLIGHT_REMAINING;
        }

        if (profile.flightStatus() == status) {
            return ModifyFlightResult.ALREADY_IN_STATE;
        }

        profile.flightStatus(status);
        player.setAllowFlight(status == FlightStatus.ENABLED);

        this.eventService.post(new FlightChangeEvent(profile));

        return ModifyFlightResult.SUCCESS;
    }

    @Override
    public ModifyFlightDurationResult modifyFlightDuration(final Profile profile, final UnaryOperator<Duration> modifier) {
        Duration modifiedDuration = modifier.apply(profile.flightRemaining());

        if (modifiedDuration.isNegative()) {
            modifiedDuration = Duration.ZERO;
        }

        profile.flightRemaining(modifiedDuration);

        if (this.modifyFlyIfDurationZero(profile)) {
            return ModifyFlightDurationResult.DISABLED_FLIGHT;
        }

        return ModifyFlightDurationResult.SUCCESS;
    }

    private boolean modifyFlyIfDurationZero(final Profile profile) {
        if (!profile.flightRemaining().isZero()) {
            return false;
        }

        @Nullable Player player = Bukkit.getPlayer(profile.uuid());

        if (player == null) {
            return false;
        }

        this.modifyFly(player, FlightStatus.DISABLED);
        return true;
    }

}
