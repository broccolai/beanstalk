package love.broccolai.beanstalk.commands.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.DurationArgument;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.context.CommandContext;
import com.google.inject.Inject;
import java.io.File;
import java.time.Duration;
import java.util.UUID;
import love.broccolai.beanstalk.commands.cloud.CloudArgumentFactory;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.item.ItemService;
import love.broccolai.beanstalk.service.message.MessageService;
import love.broccolai.beanstalk.service.profile.ProfileService;
import love.broccolai.beanstalk.service.profile.provider.ProfileCacheProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public final class BeanstalkAdminCommand implements PluginCommand {

    private final CloudArgumentFactory argumentFactory;
    private final MessageService messageService;
    private final ProfileService profileService;
    private final ItemService itemService;

    private final ProfileCacheProvider profileCacheProvider;

    @Inject
    public BeanstalkAdminCommand(
        final CloudArgumentFactory argumentFactory,
        final MessageService messageService,
        final ProfileService profileService,
        final ItemService service,
        final ProfileCacheProvider profileCacheProvider
    ) {
        this.argumentFactory = argumentFactory;
        this.messageService = messageService;
        this.profileService = profileService;
        this.itemService = service;
        this.profileCacheProvider = profileCacheProvider;
    }

    @Override
    public void register(final CommandManager<CommandSender> commandManager) {
        Command.Builder<CommandSender> baseCommand = commandManager.commandBuilder("beanstalk")
            .permission("beanstalk.admin");

        commandManager.command(baseCommand
            .literal("generate")
            .argument(PlayerArgument.of("target"))
            .argument(DurationArgument.of("duration"))
            .handler(this::handleGenerate)
        );

        commandManager.command(baseCommand
            .literal("status")
            .argument(this.argumentFactory.profile("target", true))
            .handler(this::handleStatus)
        );

        commandManager.command(baseCommand
            .literal("modify")
            .argument(EnumArgument.of(ModifyAction.class, "action"))
            .argument(this.argumentFactory.profile("target", true))
            .argument(DurationArgument.of("duration"))
            .handler(this::handleModify)
        );

        commandManager.command(baseCommand
            .literal("import")
            .literal("cubito")
            .handler(this::handleCubitoImport)
        );
    }

    private void handleGenerate(final CommandContext<CommandSender> context) {
        Player sender = (Player) context.getSender();
        Player target = context.get("target");
        Duration duration = context.get("duration");

        target.getInventory().addItem(
            this.itemService.create(duration)
        );

        this.messageService.generate(sender, duration);
    }

    private void handleStatus(final CommandContext<CommandSender> context) {
        Player sender = (Player) context.getSender();
        Profile target = context.get("target");

        this.messageService.statusTarget(sender, target, target.flightRemaining());
    }

    private void handleModify(final CommandContext<CommandSender> context) {
        Player sender = (Player) context.getSender();
        Profile target = context.get("target");
        ModifyAction modifyAction = context.get("action");
        Duration duration = context.get("duration");

        target.flightRemaining(flight -> {
            return switch (modifyAction) {
                case GIVE -> flight.plus(duration);
                case REMOVE -> flight.minus(duration);
            };
        });

        //todo: move this to ActionService
        this.stopFlyingIfDurationZero(target);

        this.messageService.modifyTarget(sender, target, target.flightRemaining());
    }

    //todo: hide this away somewhere?
    private void handleCubitoImport(final CommandContext<CommandSender> context) {
        Player sender = (Player) context.getSender();

        File folder = new File(Bukkit.getPluginsFolder(), "FlightFeathers");
        File file = new File(folder, "config.yml");

        if (!file.exists()) {
            sender.sendMessage("cubito config.yml does not exist!");
            return;
        }

        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection flightTimeSection = configuration.getConfigurationSection("FlightFeathers.FlyingTime");

        if (flightTimeSection == null) {
            sender.sendMessage("could not load cubito config.yml!");
            return;
        }

        for (String serializedId : flightTimeSection.getKeys(false)) {
            UUID uuid = UUID.fromString(serializedId);
            Duration duration = Duration.ofSeconds(configuration.getInt(serializedId));

            this.profileService.get(uuid).flightRemaining(current -> current.plus(duration));
            sender.sendMessage("imported " + duration.toSeconds() + "s for " + uuid);
        }

        this.profileCacheProvider.close();
        sender.sendMessage("successfully imported from cubito!");
    }

    private void stopFlyingIfDurationZero(Profile profile) {
        if (!profile.flightRemaining().isZero()) {
            return;
        }

        profile.flying(false);

        Player player = Bukkit.getPlayer(profile.uuid());

        if (player == null) {
            return;
        }

        player.setAllowFlight(false);
    }

    private enum ModifyAction {
        GIVE,
        REMOVE
    }

}
