package love.broccolai.beanstalk.commands.cloud.commander;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
@SuppressWarnings("UnstableApiUsage")
public class Commander implements ForwardingAudience.Single {
    private final CommandSourceStack stack;

    Commander(final CommandSourceStack stack) {
        this.stack = stack;
    }

    @Override
    public Audience audience() {
        final @Nullable Entity executor = this.stack.getExecutor();
        return executor == null ? this.stack.getSender() : executor;
    }

    public boolean hasPermission(final String permission) {
        return this.stack.getSender().hasPermission(permission);
    }

    public CommandSourceStack stack() {
        return this.stack;
    }

    public CommandSender source() {
        return this.stack.getSender();
    }

    public static Commander from(final CommandSourceStack stack) {
        if (stack.getSender() instanceof org.bukkit.entity.Player) {
            return new PlayerCommander(stack);
        }

        return new Commander(stack);
    }

}
