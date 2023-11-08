package love.broccolai.beanstalk.listeners;

import com.google.inject.Inject;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.profile.ProfileService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class PlayerLeaveListener implements Listener {

    private final ProfileService profileService;

    @Inject
    public PlayerLeaveListener(final ProfileService profileService) {
        this.profileService = profileService;
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent event) {
        this.disablePlayerFlight(event);
    }

    @EventHandler
    public void onSwitchWorld(final PlayerChangedWorldEvent event) {
        this.disablePlayerFlight(event);
    }

    private void disablePlayerFlight(final PlayerEvent event) {
        Player player = event.getPlayer();
        Profile profile = this.profileService.get(player.getUniqueId());

        profile.flying(false);
    }
}
