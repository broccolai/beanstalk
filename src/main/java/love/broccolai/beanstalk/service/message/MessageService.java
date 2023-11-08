package love.broccolai.beanstalk.service.message;

import java.time.Duration;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.message.annotations.Receiver;
import net.kyori.adventure.audience.Audience;
import net.kyori.moonshine.annotation.Message;
import net.kyori.moonshine.annotation.Placeholder;

public interface MessageService {

    @Message("feedback.status")
    void status(@Receiver Audience receiver, @Placeholder Duration duration);

    @Message("feedback.redeemed")
    void redeemed(@Receiver Audience receiver, @Placeholder Duration duration);

    @Message("feedback.enable")
    void enable(@Receiver Audience receiver);

    @Message("feedback.already-enabled")
    void alreadyEnabled(@Receiver Audience receiver);

    @Message("feedback.no-flight-remaining")
    void noFlightRemaining(@Receiver Audience receiver);

    @Message("feedback.disable")
    void disable(@Receiver Audience receiver);

    @Message("feedback.already-disabled")
    void alreadyDisabled(@Receiver Audience receiver);

    @Message("feedback.generate")
    void generate(@Receiver Audience receiver, @Placeholder Duration duration);

    @Message("feedback.status-target")
    void statusTarget(@Receiver Audience receiver, @Placeholder Profile target, @Placeholder Duration duration);
}
