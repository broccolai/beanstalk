package love.broccolai.beanstalk;

import cloud.commandframework.CommandManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import love.broccolai.beanstalk.commands.command.PluginCommand;
import love.broccolai.beanstalk.inject.ConfigurationModule;
import love.broccolai.beanstalk.inject.FactoryModule;
import love.broccolai.beanstalk.inject.PluginModule;
import love.broccolai.beanstalk.inject.ServiceModule;
import love.broccolai.beanstalk.listeners.FeatherUseListener;
import love.broccolai.beanstalk.service.profile.provider.ProfileCacheProvider;
import love.broccolai.beanstalk.tasks.FlightCheckTask;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class Beanstalk extends JavaPlugin {

    private static final Key<CommandManager<CommandSender>> COMMAND_MANAGER_KEY = Key.get(new TypeLiteral<>() {
    });

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

        this.registerCommands(this.injector);

        Bukkit.getPluginManager().registerEvents(
            this.injector.getInstance(FeatherUseListener.class),
            this
        );

        this.injector.getInstance(FlightCheckTask.class).register();
    }

    @Override
    public void onDisable() {
        this.injector.getInstance(ProfileCacheProvider.class).close();
    }

    private void registerCommands(final Injector injector) {
        CommandManager<CommandSender> commandManager = injector.getInstance(COMMAND_MANAGER_KEY);

        for (final Class<? extends PluginCommand> clazz : PluginCommand.COMMANDS) {
            PluginCommand command = injector.getInstance(clazz);
            command.register(commandManager);
        }
    }

}
