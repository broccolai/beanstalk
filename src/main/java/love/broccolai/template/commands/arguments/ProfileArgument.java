package love.broccolai.template.commands.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;
import love.broccolai.template.model.profile.Profile;
import love.broccolai.template.service.profile.ProfileService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public class ProfileArgument extends CommandArgument<CommandSender, Profile> {

    @AssistedInject
    public ProfileArgument(
            final ProfileService profileService,
            final @Assisted("name") String name,
            final @Assisted("required") boolean required
    ) {
        super(required, name, new ProfileParser(profileService), Profile.class);
    }

    public static final class ProfileParser implements ArgumentParser<CommandSender, Profile> {

        private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");

        private final ProfileService profileService;

        public ProfileParser(final ProfileService profileService) {
            this.profileService = profileService;
        }

        @Override
        public ArgumentParseResult<Profile> parse(
                final CommandContext<CommandSender> commandContext,
                final Queue<String> inputQueue
        ) {
            @Nullable String input = inputQueue.peek();

            if (input == null || !USERNAME_PATTERN.matcher(input).matches()) {
                return ArgumentParseResult.failure(new NoInputProvidedException(ProfileArgument.class, commandContext));
            }

            Profile profile = this.profileService.get(input);

            inputQueue.remove();
            return ArgumentParseResult.success(profile);
        }

        @Override
        public List<String> suggestions(
                final CommandContext<CommandSender> commandContext,
                final String input
        ) {
            return Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .toList();
        }

    }

}
