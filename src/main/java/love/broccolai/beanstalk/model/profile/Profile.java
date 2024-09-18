package love.broccolai.beanstalk.model.profile;

import java.time.Duration;
import java.util.UUID;
import java.util.function.UnaryOperator;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Profile {

    private final UUID uuid;
    private Duration flightRemaining;

    // not persisted through restarts
    private FlightStatus flying;

    public Profile(final UUID uuid, final Duration flightRemaining, final FlightStatus flying) {
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

    public FlightStatus flightStatus() {
        return this.flying;
    }

    public void flightStatus(final FlightStatus flying) {
        this.flying = flying;
    }

}
