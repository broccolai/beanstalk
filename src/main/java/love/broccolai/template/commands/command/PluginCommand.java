package love.broccolai.template.commands.command;

import cloud.commandframework.CommandManager;
import love.broccolai.template.utilities.ArrayHelper;
import org.bukkit.command.CommandSender;

public interface PluginCommand {

    Class<? extends PluginCommand>[] COMMANDS = ArrayHelper.create(
        TemplateCommand.class
    );

    void register(CommandManager<CommandSender> commandManager);
}
