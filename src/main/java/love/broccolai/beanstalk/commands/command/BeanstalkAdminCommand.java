package love.broccolai.beanstalk.commands.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.DurationArgument;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import com.google.inject.Inject;
import java.time.Duration;
import love.broccolai.beanstalk.commands.cloud.CloudArgumentFactory;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.action.ActionService;
import love.broccolai.beanstalk.service.item.ItemService;
import love.broccolai.beanstalk.service.message.MessageService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class BeanstalkAdminCommand implements PluginCommand {

    private final CloudArgumentFactory argumentFactory;
    private final MessageService messageService;
    private final ActionService actionService;
    private final ItemService itemService;

    @Inject
    public BeanstalkAdminCommand(
        final CloudArgumentFactory argumentFactory,
        final MessageService messageService,
        final ActionService actionService,
        final ItemService service
    ) {
        this.argumentFactory = argumentFactory;
        this.messageService = messageService;
        this.actionService = actionService;
        this.itemService = service;
    }

    @Override
    public void register(final CommandManager<CommandSender> commandManager) {
        Command.Builder<CommandSender> baseCommand = commandManager.commandBuilder("beanstalk")
            .permission("beanstalk.admin");

        commandManager.command(baseCommand
            .literal("generate")
            .argument(PlayerArgument.of("target"))
            .argument(DurationArgument.of("duration"))
            .handler(this::handleGenerate)
        );

        commandManager.command(baseCommand
            .literal("status")
            .argument(this.argumentFactory.profile("target", true))
            .handler(this::handleStatus)
        );

        commandManager.command(baseCommand
            .literal("modify")
            .argument(EnumArgument.of(ModifyAction.class, "action"))
            .argument(this.argumentFactory.profile("target", true))
            .argument(DurationArgument.of("duration"))
            .handler(this::handleModify)
        );
    }

    private void handleGenerate(final CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Player target = context.get("target");
        Duration duration = context.get("duration");

        target.getInventory().addItem(
            this.itemService.create(duration)
        );

        this.messageService.generate(sender, target, duration);
    }

    private void handleStatus(final CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Profile target = context.get("target");

        this.messageService.statusTarget(sender, target, target.flightStatus(), target.flightRemaining());
    }

    private void handleModify(final CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Profile target = context.get("target");
        ModifyAction modifyAction = context.get("action");
        Duration duration = context.get("duration");

        this.actionService.modifyFlightDuration(target, flight -> switch (modifyAction) {
            case GIVE -> flight.plus(duration);
            case REMOVE -> flight.minus(duration);
            case SET -> duration;
        });

        this.messageService.modifyTarget(sender, target, target.flightRemaining());
    }

    private enum ModifyAction {
        GIVE,
        REMOVE,
        SET
    }
}
