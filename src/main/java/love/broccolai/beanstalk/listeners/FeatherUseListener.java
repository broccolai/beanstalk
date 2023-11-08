package love.broccolai.beanstalk.listeners;

import com.google.inject.Inject;
import java.time.Duration;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.item.ItemService;
import love.broccolai.beanstalk.service.message.MessageService;
import love.broccolai.beanstalk.service.profile.ProfileService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public class FeatherUseListener implements Listener {

    private final MessageService messageService;
    private final ProfileService profileService;
    private final ItemService itemService;

    @Inject
    public FeatherUseListener(
        final MessageService messageService,
        final ProfileService profileService,
        final ItemService itemService
    ) {
        this.messageService = messageService;
        this.profileService = profileService;
        this.itemService = itemService;
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        @Nullable ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        @Nullable Duration flightsDuration = this.itemService.flightDurationOfItem(item);

        if (flightsDuration == null) {
            return;
        }

        Player player = event.getPlayer();
        Profile profile = this.profileService.get(player.getUniqueId());

        Duration newRemaining = profile.flightRemaining().plus(flightsDuration);
        profile.flightRemaining(newRemaining);

        this.messageService.redeemed(player, newRemaining);
    }

}
