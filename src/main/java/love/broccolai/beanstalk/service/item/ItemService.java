package love.broccolai.beanstalk.service.item;

import java.time.Duration;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface ItemService {

    ItemStack create(final Duration duration);

}
