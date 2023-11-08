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
        Command.Builder<CommandSender> baseCommand = commandManager.commandBuilder("template-test");

        commandManager.command(baseCommand
            .senderType(Player.class)
            .literal("status")
            .handler(this::handleStatus)
        );
    }

    private void handleStatus(final CommandContext<CommandSender> context) {
        Player sender = (Player) context.getSender();
        Profile profile = this.profileService.get(sender.getUniqueId());

        Duration duration = Duration.ofSeconds(profile.flightRemaining());

        this.messageService.status(sender, duration);
    }

}
