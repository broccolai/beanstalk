package love.broccolai.beanstalk.commands.cloud.commander;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PlayerCommander extends Commander {

    PlayerCommander(final CommandSourceStack commandSourceStack) {
        super(commandSourceStack);
    }

    public Player bukkit() {
        return (Player) this.stack().getSender();
    }

    public UUID uuid() {
        return this.bukkit().getUniqueId();
    }

}
