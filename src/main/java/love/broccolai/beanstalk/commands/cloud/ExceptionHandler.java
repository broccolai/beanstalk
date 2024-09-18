package love.broccolai.beanstalk.commands.cloud;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import love.broccolai.beanstalk.commands.cloud.commander.Commander;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.jspecify.annotations.NullMarked;

@Singleton
@NullMarked
public final class ExceptionHandler {

    private final MinecraftExceptionHandler<Commander> adventureHandler;

    @Inject
    public ExceptionHandler() {
        this.adventureHandler = this.generateAdventureHandler();
    }

    private MinecraftExceptionHandler<Commander> generateAdventureHandler() {
        return MinecraftExceptionHandler.createNative();
    }

    public void apply(final CommandManager<Commander> commandManager) {
        this.adventureHandler.registerTo(commandManager);
    }

}
