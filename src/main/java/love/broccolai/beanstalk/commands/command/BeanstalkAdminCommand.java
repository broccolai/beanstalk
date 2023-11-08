package love.broccolai.beanstalk.commands.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.DurationArgument;
import cloud.commandframework.context.CommandContext;
import com.google.inject.Inject;
import java.time.Duration;
import love.broccolai.beanstalk.commands.cloud.CloudArgumentFactory;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.item.ItemService;
import love.broccolai.beanstalk.service.message.MessageService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class BeanstalkAdminCommand implements PluginCommand {

    private final CloudArgumentFactory argumentFactory;
    private final MessageService messageService;
    private final ItemService itemService;

    @Inject
    public BeanstalkAdminCommand(
        final CloudArgumentFactory argumentFactory,
        final MessageService messageService,
        final ItemService service
    ) {
        this.argumentFactory = argumentFactory;
        this.messageService = messageService;
        this.itemService = service;
    }

    @Override
    public void register(final CommandManager<CommandSender> commandManager) {
        Command.Builder<CommandSender> baseCommand = commandManager.commandBuilder("beanstalk");

        commandManager.command(baseCommand
            .senderType(Player.class)
            .literal("generate")
            .argument(DurationArgument.of("duration"))
            .handler(this::handleGenerate)
        );

        commandManager.command(baseCommand
            .literal("status")
            .argument(this.argumentFactory.profile("target", true))
            .handler(this::handleStatus)
        );
    }

    private void handleGenerate(final CommandContext<CommandSender> context) {
        Player sender = (Player) context.getSender();
        Duration duration = context.get("duration");

        sender.getInventory().addItem(
            this.itemService.create(duration)
        );

        this.messageService.generate(sender, duration);
    }

    private void handleStatus(final CommandContext<CommandSender> context) {
        Player sender = (Player) context.getSender();
        Profile target = context.get("target");

        this.messageService.statusTarget(sender, target, target.flightRemaining());
    }
}
