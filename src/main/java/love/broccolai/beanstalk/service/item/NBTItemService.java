package love.broccolai.beanstalk.service.item;

import broccolai.corn.paper.item.PaperItemBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import love.broccolai.beanstalk.config.MainConfiguration;
import love.broccolai.beanstalk.utilities.DurationHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

@Singleton
@NullMarked
public class NBTItemService implements ItemService {

    //todo: find a way to integrate this with moonshine!
    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private final MainConfiguration.ItemConfiguration configuration;
    private final NamespacedKey durationKey;

    @Inject
    public NBTItemService(final Plugin plugin, final MainConfiguration mainConfiguration) {
        this.configuration = mainConfiguration.item;

        this.durationKey = Objects.requireNonNull(
            NamespacedKey.fromString("duration", plugin)
        );
    }

    @Override
    public ItemStack create(final Duration duration) {
        TagResolver placeholders = TagResolver.resolver(
            "duration",
            Tag.preProcessParsed(DurationHelper.formatDuration(duration))
        );

        Material material = Objects.requireNonNull(
            Material.matchMaterial(this.configuration.material)
        );

        List<Component> lore = this.configuration.lore
            .stream()
            .map(line -> MINI.deserialize(line, placeholders))
            .toList();

        PaperItemBuilder itemBuilder = PaperItemBuilder.ofType(material)
            .name(MINI.deserialize(this.configuration.name, placeholders))
            .lore(lore)
            .customModelData(this.configuration.customModelData)
            .setData(this.durationKey, PersistentDataType.LONG, duration.getSeconds());

        if (this.configuration.shouldGlow) {
            itemBuilder = itemBuilder
                .addEnchant(Enchantment.UNBREAKING, 1)
                .addFlag(ItemFlag.HIDE_ENCHANTS);
        }

        return itemBuilder.build();
    }

    @Override
    public Optional<Duration> flightDurationOfItem(final ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return Optional.empty();
        }

        Long duration = meta.getPersistentDataContainer().get(
            this.durationKey,
            PersistentDataType.LONG
        );

        if (duration == null) {
            return Optional.empty();
        }

        return Optional.of(Duration.ofSeconds(duration));
    }

}
