package love.broccolai.beanstalk.commands.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import com.google.inject.Inject;
import java.time.Duration;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.message.MessageService;
import love.broccolai.beanstalk.service.profile.ProfileService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class BeanstalkUserCommand implements PluginCommand {

    private final MessageService messageService;
    private final ProfileService profileService;

    @Inject
    public BeanstalkUserCommand(
        final MessageService messageService,
        final ProfileService service
    ) {
        this.messageService = messageService;
        this.profileService = service;
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
        Profile profile = this.profileService.get(sender.getUniqueId());

        if (profile.flightRemaining().isZero()) {
            this.messageService.noFlightRemaining(sender);
            return;
        }

        if (profile.flying()) {
            this.messageService.alreadyEnabled(sender);
            return;
        }

        profile.flying(true);
        sender.setFlying(true);

        this.messageService.enable(sender);
    }

    private void handleDisable(final CommandContext<CommandSender> context) {
        Player sender = (Player) context.getSender();
        Profile profile = this.profileService.get(sender.getUniqueId());

        if (!profile.flying()) {
            this.messageService.alreadyDisabled(sender);
            return;
        }

        profile.flying(false);
        sender.setFlying(false);

        this.messageService.disable(sender);
    }

}
