package love.broccolai.beanstalk.model.profile;

import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class Profile {

    private final UUID uuid;
    private long flightRemaining;
    private boolean flying;

    public Profile(final UUID uuid, final long flightRemaining, final boolean flying) {
        this.uuid = uuid;
        this.flightRemaining = flightRemaining;
        this.flying = flying;
    }

    public UUID uuid() {
        return this.uuid;
    }

    public long flightRemaining() {
        return this.flightRemaining;
    }

    public void flightRemaining(final long flightRemaining) {
        this.flightRemaining = flightRemaining;
    }

    public boolean flying() {
        return this.flying;
    }

    public void flying(final boolean flying) {
        this.flying = flying;
    }

}
