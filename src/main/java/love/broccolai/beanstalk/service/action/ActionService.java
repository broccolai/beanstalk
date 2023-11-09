package love.broccolai.beanstalk.service.action;

import java.time.Duration;
import java.util.function.UnaryOperator;
import love.broccolai.beanstalk.model.profile.FlightStatus;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.action.result.ModifyFlightDurationResult;
import love.broccolai.beanstalk.service.action.result.ModifyFlightResult;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface ActionService {

    ModifyFlightResult modifyFly(Player player, FlightStatus status);

    ModifyFlightDurationResult modifyFlightDuration(Profile profile, UnaryOperator<Duration> modifier);

}
