package love.broccolai.beanstalk.service.action;

import com.google.inject.Inject;
import java.time.Duration;
import java.util.function.UnaryOperator;
import love.broccolai.beanstalk.event.FlightChangeEvent;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.action.result.FlyResult;
import love.broccolai.beanstalk.service.event.EventService;
import love.broccolai.beanstalk.service.profile.ProfileService;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

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
    public FlyResult fly(final Player player) {
        Profile profile = this.profileService.get(player.getUniqueId());

        if (profile.flightRemaining().isZero()) {
            return FlyResult.NO_FLIGHT_REMAINING;
        }

        if (profile.flying()) {
            return FlyResult.ALREADY_FLYING;
        }

        profile.flying(true);
        player.setAllowFlight(true);

        this.eventService.post(new FlightChangeEvent(profile));

        return FlyResult.SUCCESS;
    }

    @Override
    public void modifyFlight(final Profile profile, final UnaryOperator<Duration> modifier) {

    }

}