package love.broccolai.beanstalk.commands.command;

import com.google.inject.Inject;
import love.broccolai.beanstalk.commands.cloud.commander.Commander;
import love.broccolai.beanstalk.commands.cloud.commander.PlayerCommander;
import love.broccolai.beanstalk.model.profile.FlightStatus;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.action.ActionService;
import love.broccolai.beanstalk.service.action.result.ModifyFlightResult;
import love.broccolai.beanstalk.service.message.MessageService;
import love.broccolai.beanstalk.service.profile.ProfileService;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class BeanstalkUserCommand implements PluginCommand {

    private final MessageService messageService;
    private final ProfileService profileService;
    private final ActionService actionService;

    @Inject
    public BeanstalkUserCommand(
        final MessageService messageService,
        final ProfileService profileService,
        final ActionService actionService
    ) {
        this.messageService = messageService;
        this.profileService = profileService;
        this.actionService = actionService;
    }

    @Override
    public void register(final CommandManager<Commander> commandManager) {
        Command.Builder<Commander> baseCommand = commandManager.commandBuilder("flight")
            .permission("beanstalk.user");

        commandManager.command(baseCommand
            .senderType(PlayerCommander.class)
            .literal("status")
            .handler(this::handleStatus)
        );

        commandManager.command(baseCommand
            .senderType(PlayerCommander.class)
            .literal("on")
            .handler(this::handleEnable)
        );

        commandManager.command(baseCommand
            .senderType(PlayerCommander.class)
            .literal("off")
            .handler(this::handleDisable)
        );
    }

    private void handleStatus(final CommandContext<PlayerCommander> context) {
        PlayerCommander sender = context.sender();
        Profile profile = this.profileService.get(sender.uuid());

        this.messageService.status(sender, profile.flightStatus(), profile.flightRemaining());
    }

    private void handleDisable(final CommandContext<PlayerCommander> context) {
        PlayerCommander sender = context.sender();
        ModifyFlightResult actionResult = this.actionService.modifyFly(sender.bukkit(), FlightStatus.DISABLED);

        switch (actionResult) {
            case ALREADY_IN_STATE -> this.messageService.alreadyDisabled(sender);
            case SUCCESS -> this.messageService.disable(sender);
            default -> {
                // todo: other cases are not possible for disabling, we should spilt the result
            }
        }
    }

    private void handleEnable(final CommandContext<PlayerCommander> context) {
        PlayerCommander sender = context.sender();
        ModifyFlightResult actionResult = this.actionService.modifyFly(sender.bukkit(), FlightStatus.ENABLED);

        switch (actionResult) {
            case NO_PERMISSION_IN_WORLD -> this.messageService.noPermissionInWorld(sender);
            case NO_FLIGHT_REMAINING -> this.messageService.noFlightRemaining(sender);
            case ALREADY_IN_STATE -> this.messageService.alreadyEnabled(sender);
            case SUCCESS -> this.messageService.enable(sender);
        }
    }

}
