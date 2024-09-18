package love.broccolai.beanstalk.commands.command;

import love.broccolai.beanstalk.commands.cloud.commander.Commander;
import love.broccolai.beanstalk.utilities.ArrayHelper;
import org.incendo.cloud.CommandManager;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PluginCommand {

    Class<? extends PluginCommand>[] COMMANDS = ArrayHelper.create(
        BeanstalkUserCommand.class,
        BeanstalkAdminCommand.class,
        BeanstalkImportCommand.class
    );

    void register(CommandManager<Commander> commandManager);
}
