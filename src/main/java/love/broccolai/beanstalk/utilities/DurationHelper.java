package love.broccolai.beanstalk.utilities;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class DurationHelper {

    private DurationHelper() {
    }

    public static String formatDuration(final Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        long seconds = duration.minusHours(hours).minusMinutes(minutes).getSeconds();

        List<String> parts = new ArrayList<>();

        if (hours > 0) {
            parts.add(hours + "h");
        }

        if (minutes > 0) {
            parts.add(minutes + "m");
        }

        if (hours == 0 && seconds > 0 || duration.isZero()) {
            parts.add(seconds + "s");
        }

        return String.join("", parts);
    }

}
