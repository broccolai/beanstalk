package love.broccolai.beanstalk.service.item;

import java.time.Duration;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ItemService {

    ItemStack create(final Duration duration);

    Optional<Duration> flightDurationOfItem(final ItemStack item);

}
