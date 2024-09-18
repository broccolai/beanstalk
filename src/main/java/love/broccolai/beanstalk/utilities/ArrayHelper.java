package love.broccolai.beanstalk.utilities;

import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ArrayHelper {

    private ArrayHelper() {
        // helper class
    }

    @SafeVarargs
    public static <T> T [] create(final T... values) {
        return values;
    }

}
