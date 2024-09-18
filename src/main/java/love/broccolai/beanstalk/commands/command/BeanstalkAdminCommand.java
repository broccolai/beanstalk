package love.broccolai.beanstalk.commands.command;

import com.google.inject.Inject;
import java.time.Duration;
import love.broccolai.beanstalk.commands.cloud.CloudArgumentFactory;
import love.broccolai.beanstalk.commands.cloud.commander.Commander;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.action.ActionService;
import love.broccolai.beanstalk.service.item.ItemService;
import love.broccolai.beanstalk.service.message.MessageService;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.DurationParser;
import org.incendo.cloud.parser.standard.EnumParser;
import org.jspecify.annotations.NullMarked;

@NullMarked
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
    public void register(final CommandManager<Commander> commandManager) {
        Command.Builder<Commander> baseCommand = commandManager.commandBuilder("beanstalk")
            .permission("beanstalk.admin");

        commandManager.command(baseCommand
            .literal("generate")
            .required("target", PlayerParser.playerParser())
            .required("duration", DurationParser.durationParser())
            .handler(this::handleGenerate)
        );

        commandManager.command(baseCommand
            .literal("status")
            .required("target", this.argumentFactory.profile())
            .handler(this::handleStatus)
        );

        commandManager.command(baseCommand
            .literal("modify")
            .required("action", EnumParser.enumParser(ModifyAction.class))
            .required("target", this.argumentFactory.profile())
            .required("duration", DurationParser.durationParser())
            .handler(this::handleModify)
        );
    }

    private void handleGenerate(final CommandContext<Commander> context) {
        Commander sender = context.sender();
        Player target = context.get("target");
        Duration duration = context.get("duration");

        target.getInventory().addItem(
            this.itemService.create(duration)
        );

        this.messageService.generate(sender, target, duration);
    }

    private void handleStatus(final CommandContext<Commander> context) {
        Commander sender = context.sender();
        Profile target = context.get("target");

        this.messageService.statusTarget(sender, target, target.flightStatus(), target.flightRemaining());
    }

    private void handleModify(final CommandContext<Commander> context) {
        Commander sender = context.sender();
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
