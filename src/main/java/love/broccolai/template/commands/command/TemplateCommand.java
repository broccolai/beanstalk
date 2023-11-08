package love.broccolai.template.commands.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.context.CommandContext;
import com.google.inject.Inject;
import love.broccolai.template.commands.cloud.CloudArgumentFactory;
import love.broccolai.template.model.profile.Profile;
import love.broccolai.template.service.message.MessageService;
import org.bukkit.command.CommandSender;

public final class TemplateCommand implements PluginCommand {

    private final CloudArgumentFactory argumentFactory;
    private final MessageService messageService;

    @Inject
    public TemplateCommand(
        final CloudArgumentFactory argumentFactory,
        final MessageService messageService
    ) {
        this.argumentFactory = argumentFactory;
        this.messageService = messageService;
    }

    @Override
    public void register(final CommandManager<CommandSender> commandManager) {
        Command.Builder<CommandSender> baseCommand = commandManager.commandBuilder("template-test");

        commandManager.command(baseCommand
            .literal("store")
            .argument(this.argumentFactory.profile("target", true))
            .argument(IntegerArgument.of("data"))
            .handler(this::handleStore)
        );

        commandManager.command(baseCommand
            .literal("retrieve")
            .argument(this.argumentFactory.profile("target", true))
            .handler(this::handleRetrieve)
        );
    }

    private void handleStore(final CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Profile target = context.get("target");
        Integer data = context.get("data");

        target.data(data);

        this.messageService.store(sender, data);
    }

    private void handleRetrieve(final CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Profile target = context.get("target");

        int data = target.data();

        this.messageService.retrieve(sender, data);
    }

}
