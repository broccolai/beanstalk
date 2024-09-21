package love.broccolai.beanstalk.expansion;

import com.google.inject.Inject;
import com.google.inject.Injector;
import io.github.miniplaceholders.api.Expansion;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.message.MessageService;
import love.broccolai.beanstalk.service.profile.ProfileService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class MiniPlaceholdersExpansion {

    private final ProfileService profileService;
    private final MessageService messageService;

    @Inject
    public MiniPlaceholdersExpansion(
        final ProfileService profileService,
        final MessageService messageService
    ) {
        this.profileService = profileService;
        this.messageService = messageService;
    }

    public static void tryRegister(final Injector injector) {
        try {
            Class.forName("io.github.miniplaceholders.api.MiniPlaceholders");
        } catch (ClassNotFoundException e) {
            return;
        }

        injector.getInstance(MiniPlaceholdersExpansion.class).register();
    }

    public void register() {
        Expansion expansion = Expansion.builder("beanstalk")
            .filter(Player.class)
            .audiencePlaceholder("status", this::status)
            .audiencePlaceholder("remaining", this::remaining)
            .build();

        expansion.register();
    }

    private Tag status(final Audience audience, final ArgumentQueue queue, final Context context) {
        Player player = (Player) audience;
        Profile profile = this.profileService.get(player.getUniqueId());

        Component component = this.messageService.statusTag(
            profile.flightStatus(),
            profile.flightRemaining()
        );

        return Tag.selfClosingInserting(component);
    }

    private Tag remaining(final Audience audience, final ArgumentQueue queue, final Context context) {
        Player player = (Player) audience;
        Profile profile = this.profileService.get(player.getUniqueId());

        Component component = this.messageService.remainingTag(
            profile.flightRemaining()
        );

        return Tag.selfClosingInserting(component);
    }

}
