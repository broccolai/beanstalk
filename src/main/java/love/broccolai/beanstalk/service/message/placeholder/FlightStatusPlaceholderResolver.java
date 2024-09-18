package love.broccolai.beanstalk.service.message.placeholder;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import love.broccolai.beanstalk.model.profile.FlightStatus;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.moonshine.placeholder.ConclusionValue;
import net.kyori.moonshine.placeholder.ContinuanceValue;
import net.kyori.moonshine.util.Either;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class FlightStatusPlaceholderResolver implements SinglePlaceholderResolver<FlightStatus> {

    //todo(josh): localize
    private static final Component DISABLED = Component.text("Disabled", NamedTextColor.RED, TextDecoration.BOLD);
    private static final Component ENABLED = Component.text("Enabled", NamedTextColor.GREEN, TextDecoration.BOLD);

    @Override
    public Either<ConclusionValue<? extends Component>, ContinuanceValue<?>> single(
        final String placeholderName,
        final FlightStatus value,
        final Audience receiver,
        final Type owner,
        final Method method,
        final @Nullable Object[] parameters
    ) {
        Component component = switch (value) {
            case ENABLED -> ENABLED;
            case DISABLED -> DISABLED;
        };

        return Either.left(ConclusionValue.conclusionValue(component));
    }

}
