package love.broccolai.template.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.leangen.geantyref.TypeToken;
import love.broccolai.template.config.LocaleConfiguration;
import love.broccolai.template.service.data.DatabaseStorageService;
import love.broccolai.template.service.data.StorageService;
import love.broccolai.template.service.message.MessageRenderer;
import love.broccolai.template.service.message.MessageService;
import love.broccolai.template.service.message.placeholder.NumberPlaceholderResolver;
import love.broccolai.template.service.message.placeholder.StringPlaceholderResolver;
import love.broccolai.template.service.message.receiver.BasicReceiverResolver;
import love.broccolai.template.service.profile.PipelineProfileService;
import love.broccolai.template.service.profile.ProfileService;
import love.broccolai.template.service.task.PaperTaskService;
import love.broccolai.template.service.task.TaskService;
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
    }

    @Provides
    public MessageService messageService(
        final LocaleConfiguration localeConfiguration,
        final BasicReceiverResolver basicReceiverResolver,
        final MessageRenderer messageRenderer,
        final StringPlaceholderResolver stringPlaceholderResolver,
        final NumberPlaceholderResolver numberPlaceholderResolver
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
            .create(this.getClass().getClassLoader());
    }

}
