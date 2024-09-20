package love.broccolai.beanstalk;

import com.google.inject.Inject;
import love.broccolai.beanstalk.service.action.ActionService;
import love.broccolai.beanstalk.service.message.MessageService;
import love.broccolai.beanstalk.service.profile.ProfileService;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class BeanstalkApi {

    static @MonotonicNonNull BeanstalkApi instance;

    public static BeanstalkApi instance() {
        return instance;
    }

    private final ProfileService profileService;
    private final ActionService actionService;
    private final MessageService messageService;

    @Inject
    public BeanstalkApi(
            final ProfileService profileService,
            final ActionService actionService,
            final MessageService messageService
    ) {
        this.profileService = profileService;
        this.actionService = actionService;
        this.messageService = messageService;
    }

    public ProfileService profileService() {
        return this.profileService;
    }

    public ActionService actionService() {
        return this.actionService;
    }

    public MessageService messageService() {
        return this.messageService;
    }

}
