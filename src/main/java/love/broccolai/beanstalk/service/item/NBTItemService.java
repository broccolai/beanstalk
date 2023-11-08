package love.broccolai.beanstalk.service.item;

import broccolai.corn.paper.item.PaperItemBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.time.Duration;
import java.util.Objects;
import love.broccolai.beanstalk.utilities.DurationHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@Singleton
@DefaultQualifier(NonNull.class)
public class NBTItemService implements ItemService {

    private final NamespacedKey durationKey;

    @Inject
    public NBTItemService(final Plugin plugin) {
        this.durationKey = Objects.requireNonNull(
            NamespacedKey.fromString("duration", plugin)
        );
    }

    @Override
    public ItemStack create(final Duration duration) {
        return PaperItemBuilder.ofType(Material.FEATHER)
            .name(Component.text("flight feather"))
            .loreList(Component.text(DurationHelper.formatDuration(duration)))
            .setData(this.durationKey, PersistentDataType.LONG, duration.getSeconds())
            .build();
    }

}
