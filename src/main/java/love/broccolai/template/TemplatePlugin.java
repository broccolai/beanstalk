package love.broccolai.template;

import cloud.commandframework.CommandManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import love.broccolai.template.commands.command.PluginCommand;
import love.broccolai.template.inject.ConfigurationModule;
import love.broccolai.template.inject.FactoryModule;
import love.broccolai.template.inject.PluginModule;
import love.broccolai.template.inject.ServiceModule;
import love.broccolai.template.service.profile.provider.ProfileCacheProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class TemplatePlugin extends JavaPlugin {

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
