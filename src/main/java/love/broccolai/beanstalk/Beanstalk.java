package love.broccolai.beanstalk;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import love.broccolai.beanstalk.commands.cloud.commander.Commander;
import love.broccolai.beanstalk.commands.command.PluginCommand;
import love.broccolai.beanstalk.inject.ConfigurationModule;
import love.broccolai.beanstalk.inject.FactoryModule;
import love.broccolai.beanstalk.inject.PluginModule;
import love.broccolai.beanstalk.inject.ServiceModule;
import love.broccolai.beanstalk.listeners.FeatherUseListener;
import love.broccolai.beanstalk.listeners.PlayerLeaveListener;
import love.broccolai.beanstalk.listeners.PreventFallListener;
import love.broccolai.beanstalk.service.profile.provider.ProfileCacheProvider;
import love.broccolai.beanstalk.tasks.FlightCheckTask;
import love.broccolai.beanstalk.utilities.ArrayHelper;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.CommandManager;

@DefaultQualifier(NonNull.class)
public final class Beanstalk extends JavaPlugin {

    private static final Key<CommandManager<Commander>> COMMAND_MANAGER_KEY = Key.get(new TypeLiteral<>() {
    });

    private static final Class<? extends Listener>[] LISTENERS = ArrayHelper.create(
        FeatherUseListener.class,
        PreventFallListener.class,
        PlayerLeaveListener.class
    );

    private @MonotonicNonNull Injector injector;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onEnable() {
        this.getDataFolder().mkdirs();

        this.injector = Guice.createInjector(
            new PluginModule(this),
            new ConfigurationModule(),
            new FactoryModule(),
            new ServiceModule()
        );

        this.registerCommands();
        this.registerListeners();

        this.injector.getInstance(FlightCheckTask.class).register();
    }

    @Override
    public void onDisable() {
        this.injector.getInstance(ProfileCacheProvider.class).close();
    }

    private void registerCommands() {
        CommandManager<Commander> commandManager = this.injector.getInstance(COMMAND_MANAGER_KEY);

        for (final Class<? extends PluginCommand> clazz : PluginCommand.COMMANDS) {
            PluginCommand command = this.injector.getInstance(clazz);
            command.register(commandManager);
        }
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        for (final Class<? extends Listener> listener : LISTENERS) {
            pluginManager.registerEvents(
                this.injector.getInstance(listener),
                this
            );
        }
    }

}
