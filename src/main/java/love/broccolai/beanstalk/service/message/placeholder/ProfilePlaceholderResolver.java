package love.broccolai.beanstalk.service.message.placeholder;

import com.google.inject.Inject;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.profile.ProfileService;
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
public final class ProfilePlaceholderResolver implements SinglePlaceholderResolver<Profile> {

    private final ProfileService profileService;

    @Inject
    public ProfilePlaceholderResolver(final ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public Either<ConclusionValue<? extends Component>, ContinuanceValue<?>> single(
        final String placeholderName,
        final Profile value,
        final Audience receiver,
        final Type owner,
        final Method method,
        final @Nullable Object[] parameters
    ) {
        return Either.right(continuanceValue(
            this.profileService.name(value),
            String.class
        ));
    }

}
