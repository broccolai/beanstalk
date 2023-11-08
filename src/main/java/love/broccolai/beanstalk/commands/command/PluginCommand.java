package love.broccolai.beanstalk.commands.command;

import cloud.commandframework.CommandManager;
import love.broccolai.beanstalk.utilities.ArrayHelper;
import org.bukkit.command.CommandSender;

public interface PluginCommand {

    Class<? extends PluginCommand>[] COMMANDS = ArrayHelper.create(
        BeanstalkUserCommand.class
    );

    void register(CommandManager<CommandSender> commandManager);
}
