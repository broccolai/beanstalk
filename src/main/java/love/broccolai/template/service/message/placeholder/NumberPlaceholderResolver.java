package love.broccolai.template.service.message.placeholder;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.moonshine.placeholder.ConclusionValue;
import net.kyori.moonshine.placeholder.ContinuanceValue;
import net.kyori.moonshine.util.Either;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import static net.kyori.moonshine.placeholder.ContinuanceValue.continuanceValue;

@DefaultQualifier(NonNull.class)
public final class NumberPlaceholderResolver implements SinglePlaceholderResolver<Number> {

    @Override
    public Either<ConclusionValue<? extends Component>, ContinuanceValue<?>> single(
        final String placeholderName,
        final Number value,
        final Audience receiver,
        final Type owner,
        final Method method,
        final @Nullable Object[] parameters
    ) {
        return Either.right(continuanceValue(String.valueOf(value), String.class));
    }

}
