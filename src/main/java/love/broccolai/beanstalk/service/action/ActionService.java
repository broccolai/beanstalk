package love.broccolai.beanstalk.service.action;

import java.time.Duration;
import java.util.function.UnaryOperator;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.action.result.FlyResult;
import love.broccolai.beanstalk.service.action.result.ModifyFlyResult;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface ActionService {

    FlyResult fly(Player player);

    ModifyFlyResult modifyFlight(Profile profile, UnaryOperator<Duration> modifier);

}
