package love.broccolai.beanstalk.commands.command;

import com.google.inject.Inject;
import java.io.File;
import java.time.Duration;
import java.util.UUID;
import love.broccolai.beanstalk.commands.cloud.commander.Commander;
import love.broccolai.beanstalk.service.profile.ProfileService;
import love.broccolai.beanstalk.service.profile.provider.ProfileCacheProvider;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class BeanstalkImportCommand implements PluginCommand {

    private static final String CUB_CONFIG_PREFIX = "FlightFeathers.FlyingTime";

    private final ProfileService profileService;

    private final ProfileCacheProvider profileCacheProvider;

    @Inject
    public BeanstalkImportCommand(
        final ProfileService profileService,
        final ProfileCacheProvider profileCacheProvider
    ) {
        this.profileService = profileService;
        this.profileCacheProvider = profileCacheProvider;
    }

    @Override
    public void register(final CommandManager<Commander> commandManager) {
        Command.Builder<Commander> baseCommand = commandManager.commandBuilder("beanstalk")
            .permission("beanstalk.admin")
            .literal("import");
        
        commandManager.command(baseCommand
            .literal("cubito")
            .handler(this::handleCubitoImport)
        );
    }

    private void handleCubitoImport(final CommandContext<Commander> context) {
        Commander sender = context.sender();

        File folder = new File(Bukkit.getPluginsFolder(), "FlightFeathers");
        File file = new File(folder, "config.yml");

        if (!file.exists()) {
            sender.sendMessage(Component.text("cubito config.yml does not exist!"));
            return;
        }

        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection flightTimeSection = configuration.getConfigurationSection(CUB_CONFIG_PREFIX);

        if (flightTimeSection == null) {
            sender.sendMessage(Component.text("could not load cubito config.yml!"));
            return;
        }

        for (String serializedId : flightTimeSection.getKeys(false)) {
            UUID uuid = UUID.fromString(serializedId);
            String key = String.join(".", CUB_CONFIG_PREFIX, serializedId);

            Duration duration = Duration.ofSeconds(configuration.getInt(key));

            if (duration.isZero()) {
                continue;
            }

            this.profileService.get(uuid).flightRemaining(current -> current.plus(duration));
            sender.sendMessage(Component.text("imported " + duration.toSeconds() + "s for " + uuid));
        }

        this.profileCacheProvider.close();
        sender.sendMessage(Component.text("successfully imported from cubito!"));
    }

}
