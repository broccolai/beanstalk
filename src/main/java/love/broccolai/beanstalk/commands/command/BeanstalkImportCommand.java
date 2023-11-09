package love.broccolai.beanstalk.commands.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import com.google.inject.Inject;
import java.io.File;
import java.time.Duration;
import java.util.UUID;
import love.broccolai.beanstalk.service.profile.ProfileService;
import love.broccolai.beanstalk.service.profile.provider.ProfileCacheProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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
    public void register(final CommandManager<CommandSender> commandManager) {
        Command.Builder<CommandSender> baseCommand = commandManager.commandBuilder("beanstalk")
            .permission("beanstalk.admin")
            .literal("import");
        
        commandManager.command(baseCommand
            .literal("cubito")
            .handler(this::handleCubitoImport)
        );
    }

    private void handleCubitoImport(final CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();

        File folder = new File(Bukkit.getPluginsFolder(), "FlightFeathers");
        File file = new File(folder, "config.yml");

        if (!file.exists()) {
            sender.sendMessage("cubito config.yml does not exist!");
            return;
        }

        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection flightTimeSection = configuration.getConfigurationSection(CUB_CONFIG_PREFIX);

        if (flightTimeSection == null) {
            sender.sendMessage("could not load cubito config.yml!");
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
            sender.sendMessage("imported " + duration.toSeconds() + "s for " + uuid);
        }

        this.profileCacheProvider.close();
        sender.sendMessage("successfully imported from cubito!");
    }

}
