package love.broccolai.beanstalk.service.message.placeholder;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.Duration;
import love.broccolai.beanstalk.utilities.DurationHelper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.moonshine.placeholder.ConclusionValue;
import net.kyori.moonshine.placeholder.ContinuanceValue;
import net.kyori.moonshine.util.Either;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static net.kyori.moonshine.placeholder.ContinuanceValue.continuanceValue;

@NullMarked
public final class DurationPlaceholderResolver implements SinglePlaceholderResolver<Duration> {

    @Override
    public Either<ConclusionValue<? extends Component>, ContinuanceValue<?>> single(
        final String placeholderName,
        final Duration value,
        final Audience receiver,
        final Type owner,
        final Method method,
        final @Nullable Object[] parameters
    ) {
        return Either.right(continuanceValue(DurationHelper.formatDuration(value), String.class));
    }

}
