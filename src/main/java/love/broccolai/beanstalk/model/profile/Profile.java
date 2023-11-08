package love.broccolai.beanstalk.model.profile;

import java.time.Duration;
import java.util.UUID;
import java.util.function.UnaryOperator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class Profile {

    private final UUID uuid;
    private Duration flightRemaining;

    // not persisted through restarts
    private boolean flying;

    public Profile(final UUID uuid, final Duration flightRemaining, final boolean flying) {
        this.uuid = uuid;
        this.flightRemaining = flightRemaining;
        this.flying = flying;
    }

    public UUID uuid() {
        return this.uuid;
    }

    public Duration flightRemaining() {
        return this.flightRemaining;
    }

    public void flightRemaining(final Duration flightRemaining) {
        this.flightRemaining = flightRemaining;
    }

    public Duration flightRemaining(final UnaryOperator<Duration> editor) {
        Duration modifiedDuration = editor.apply(this.flightRemaining);
        this.flightRemaining = modifiedDuration.isNegative() ? Duration.ZERO : modifiedDuration;
        return this.flightRemaining;
    }

    public boolean flying() {
        return this.flying;
    }

    public void flying(final boolean flying) {
        this.flying = flying;
    }

}
