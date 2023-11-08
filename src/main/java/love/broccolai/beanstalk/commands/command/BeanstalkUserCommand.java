package love.broccolai.beanstalk.commands.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import com.google.inject.Inject;
import java.time.Duration;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.action.ActionService;
import love.broccolai.beanstalk.service.action.result.FlyResult;
import love.broccolai.beanstalk.service.message.MessageService;
import love.broccolai.beanstalk.service.profile.ProfileService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
    public void register(final CommandManager<CommandSender> commandManager) {
        Command.Builder<CommandSender> baseCommand = commandManager.commandBuilder("flight");

        commandManager.command(baseCommand
            .senderType(Player.class)
            .literal("status")
            .handler(this::handleStatus)
        );

        commandManager.command(baseCommand
            .senderType(Player.class)
            .literal("on")
            .handler(this::handleEnable)
        );

        commandManager.command(baseCommand
            .senderType(Player.class)
            .literal("off")
            .handler(this::handleDisable)
        );
    }

    private void handleStatus(final CommandContext<CommandSender> context) {
        Player sender = (Player) context.getSender();
        Profile profile = this.profileService.get(sender.getUniqueId());

        Duration duration = profile.flightRemaining();

        this.messageService.status(sender, duration);
    }

    private void handleEnable(final CommandContext<CommandSender> context) {
        Player sender = (Player) context.getSender();

        FlyResult actionResult = this.actionService.fly(sender);

        switch (actionResult) {
            case NO_FLIGHT_REMAINING -> this.messageService.noFlightRemaining(sender);
            case ALREADY_FLYING -> this.messageService.alreadyEnabled(sender);
            case SUCCESS -> this.messageService.enable(sender);
        }
    }

    private void handleDisable(final CommandContext<CommandSender> context) {
        Player sender = (Player) context.getSender();
        Profile profile = this.profileService.get(sender.getUniqueId());

        if (!profile.flying()) {
            this.messageService.alreadyDisabled(sender);
            return;
        }

        profile.flying(false);
        sender.setAllowFlight(false);

        this.messageService.disable(sender);
    }

}
