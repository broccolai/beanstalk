package love.broccolai.beanstalk.event;

import love.broccolai.beanstalk.model.profile.Profile;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record FlightChangeEvent(Profile profile) implements Event {

}
