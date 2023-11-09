package love.broccolai.beanstalk.listeners;

import com.google.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import com.google.inject.Singleton;
import love.broccolai.beanstalk.event.FlightChangeEvent;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.event.EventService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@Singleton
@DefaultQualifier(NonNull.class)
public class PreventFallListener implements Listener {

    private final Set<UUID> trackedForPrevention = new HashSet<>();

    @Inject
    public PreventFallListener(final EventService eventService) {
        eventService.register(FlightChangeEvent.class, this::onFlightChange);
    }

    @EventHandler
    public void onDamage(final EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        UUID uuid = player.getUniqueId();

        if (!this.trackedForPrevention.contains(uuid)) {
            return;
        }

        event.setCancelled(true);
        this.trackedForPrevention.remove(uuid);
    }

    public void onFlightChange(final FlightChangeEvent event) {
        Profile profile = event.profile();

        if (!profile.flying()) {
            return;
        }

        this.trackedForPrevention.add(profile.uuid());
    }

}
