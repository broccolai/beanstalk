package love.broccolai.template.utilities;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class ArrayHelper {

    private ArrayHelper() {
        // helper class
    }

    @SafeVarargs
    public static <T> T [] create(final T... values) {
        return values;
    }

}
