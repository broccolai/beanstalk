package love.broccolai.beanstalk.listeners;

import com.google.inject.Inject;
import love.broccolai.beanstalk.model.profile.FlightStatus;
import love.broccolai.beanstalk.service.action.ActionService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PlayerLeaveListener implements Listener {

    private final ActionService actionService;

    @Inject
    public PlayerLeaveListener(final ActionService actionService) {
        this.actionService = actionService;
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

        this.actionService.modifyFly(player, FlightStatus.DISABLED);
    }
}
