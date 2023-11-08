package love.broccolai.beanstalk.commands.cloud;

import cloud.commandframework.CommandManager;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@Singleton
@DefaultQualifier(NonNull.class)
public final class ExceptionHandler {

    private final MinecraftExceptionHandler<CommandSender> adventureHandler;

    @Inject
    public ExceptionHandler() {
        this.adventureHandler = this.generateAdventureHandler();
    }

    private MinecraftExceptionHandler<CommandSender> generateAdventureHandler() {
        return new MinecraftExceptionHandler<>();
    }

    public void apply(final CommandManager<CommandSender> commandManager) {
        this.adventureHandler.apply(commandManager, sender -> sender);
    }

}
