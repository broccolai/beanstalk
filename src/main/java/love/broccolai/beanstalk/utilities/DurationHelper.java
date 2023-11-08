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

        List<String> parts = new ArrayList<>();

        if (hours > 0) {
            parts.add(hours + "h");
        }

        if (hours == 0 && minutes > 0) {
            parts.add(minutes + "m");
        }

        return String.join("", parts);
    }

}
