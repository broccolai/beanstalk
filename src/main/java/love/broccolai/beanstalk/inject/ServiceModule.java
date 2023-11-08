package love.broccolai.beanstalk.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.leangen.geantyref.TypeToken;
import java.time.Duration;
import love.broccolai.beanstalk.config.LocaleConfiguration;
import love.broccolai.beanstalk.service.action.ActionService;
import love.broccolai.beanstalk.service.action.EventActionService;
import love.broccolai.beanstalk.service.data.DatabaseStorageService;
import love.broccolai.beanstalk.service.data.StorageService;
import love.broccolai.beanstalk.service.event.EventService;
import love.broccolai.beanstalk.service.event.SimpleEventService;
import love.broccolai.beanstalk.service.item.ItemService;
import love.broccolai.beanstalk.service.item.NBTItemService;
import love.broccolai.beanstalk.service.message.MessageRenderer;
import love.broccolai.beanstalk.service.message.MessageService;
import love.broccolai.beanstalk.service.message.placeholder.DurationPlaceholderResolver;
import love.broccolai.beanstalk.service.message.placeholder.NumberPlaceholderResolver;
import love.broccolai.beanstalk.service.message.placeholder.StringPlaceholderResolver;
import love.broccolai.beanstalk.service.message.receiver.BasicReceiverResolver;
import love.broccolai.beanstalk.service.profile.PipelineProfileService;
import love.broccolai.beanstalk.service.profile.ProfileService;
import love.broccolai.beanstalk.service.task.PaperTaskService;
import love.broccolai.beanstalk.service.task.TaskService;
import net.kyori.adventure.audience.Audience;
import net.kyori.moonshine.Moonshine;
import net.kyori.moonshine.exception.scan.UnscannableMethodException;
import net.kyori.moonshine.strategy.StandardPlaceholderResolverStrategy;
import net.kyori.moonshine.strategy.supertype.StandardSupertypeThenInterfaceSupertypeStrategy;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public class ServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        this.bind(StorageService.class).to(DatabaseStorageService.class);
        this.bind(ProfileService.class).to(PipelineProfileService.class);
        this.bind(TaskService.class).to(PaperTaskService.class);
        this.bind(ItemService.class).to(NBTItemService.class);
        this.bind(EventService.class).to(SimpleEventService.class);
        this.bind(ActionService.class).to(EventActionService.class);
    }

    @Provides
    public MessageService messageService(
        final LocaleConfiguration localeConfiguration,
        final BasicReceiverResolver basicReceiverResolver,
        final MessageRenderer messageRenderer,
        final StringPlaceholderResolver stringPlaceholderResolver,
        final NumberPlaceholderResolver numberPlaceholderResolver,
        final DurationPlaceholderResolver durationPlaceholderResolver
    ) throws UnscannableMethodException {
        return Moonshine.<MessageService, Audience>builder(TypeToken.get(MessageService.class))
            .receiverLocatorResolver(basicReceiverResolver, 0)
            .sourced((audience, key) -> localeConfiguration.get(key))
            .rendered(messageRenderer)
            .sent(Audience::sendMessage)
            .resolvingWithStrategy(
                new StandardPlaceholderResolverStrategy<>(new StandardSupertypeThenInterfaceSupertypeStrategy(true))
            )
            .weightedPlaceholderResolver(String.class, stringPlaceholderResolver, 1)
            .weightedPlaceholderResolver(Number.class, numberPlaceholderResolver, 1)
            .weightedPlaceholderResolver(Duration.class, durationPlaceholderResolver, 1)
            .create(this.getClass().getClassLoader());
    }

}
