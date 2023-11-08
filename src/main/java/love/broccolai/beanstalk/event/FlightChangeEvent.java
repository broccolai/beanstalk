package love.broccolai.beanstalk.event;

import love.broccolai.beanstalk.model.profile.Profile;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public record FlightChangeEvent(Profile profile) implements Event {

}
