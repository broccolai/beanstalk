package love.broccolai.beanstalk.commands.arguments;

import com.google.inject.Inject;
import io.leangen.geantyref.TypeToken;
import java.util.regex.Pattern;
import love.broccolai.beanstalk.commands.cloud.captions.BeanstalkCaptionKeys;
import love.broccolai.beanstalk.commands.cloud.commander.Commander;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.profile.ProfileService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;

@DefaultQualifier(NonNull.class)
public class ProfileDescriptor implements DescribedArgumentParser<Profile> {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");

    private final ProfileService profileService;

    @Inject
    public ProfileDescriptor(final ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public ArgumentParseResult<Profile> parse(
        final CommandContext<Commander> commandContext,
        final CommandInput commandInput
    ) {
        String input = commandInput.readString();

        if (!USERNAME_PATTERN.matcher(input).matches()) {
            return ArgumentParseResult.failure(new ProfileParseException(input, commandContext));
        }

        Profile profile = this.profileService.get(input);
        return ArgumentParseResult.success(profile);
    }

    @Override
    public Iterable<String> stringSuggestions(
        final CommandContext<Commander> commandContext,
        final CommandInput input
    ) {
        return Bukkit.getOnlinePlayers()
            .stream()
            .map(Player::getName)
            .toList();
    }

    @Override
    public TypeToken<Profile> valueType() {
        return TypeToken.get(Profile.class);
    }

    private static final class ProfileParseException extends ParserException {

        private ProfileParseException(final String input, final CommandContext<Commander> context) {
            super(
                ProfileDescriptor.class,
                context,
                BeanstalkCaptionKeys.ARGUMENT_PARSE_FAILURE_PROFILE,
                CaptionVariable.of("input", input)
            );
        }
    }
}
