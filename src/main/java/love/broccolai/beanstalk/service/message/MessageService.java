package love.broccolai.beanstalk.service.message;

import love.broccolai.beanstalk.service.message.annotations.Receiver;
import net.kyori.adventure.audience.Audience;
import net.kyori.moonshine.annotation.Message;
import net.kyori.moonshine.annotation.Placeholder;

public interface MessageService {

    @Message("store")
    void store(@Receiver Audience receiver, @Placeholder Integer value);

    @Message("retrieve")
    void retrieve(@Receiver Audience receiver, @Placeholder Integer value);

}
