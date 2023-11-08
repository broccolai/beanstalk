package love.broccolai.beanstalk.service.message;

import java.time.Duration;
import love.broccolai.beanstalk.service.message.annotations.Receiver;
import net.kyori.adventure.audience.Audience;
import net.kyori.moonshine.annotation.Message;
import net.kyori.moonshine.annotation.Placeholder;

public interface MessageService {

    @Message("generate")
    void generate(@Receiver Audience receiver, @Placeholder Duration value);

    @Message("status")
    void status(@Receiver Audience receiver, @Placeholder Duration value);

}
