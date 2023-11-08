package love.broccolai.beanstalk.service.action;

import java.time.Duration;
import java.util.function.UnaryOperator;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.action.result.FlyResult;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface ActionService {

    //todo: should this action on the Profile?
    FlyResult fly(Player player);

    void modifyFlight(Profile profile, UnaryOperator<Duration> modifier);

}
