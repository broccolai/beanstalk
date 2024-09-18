package love.broccolai.beanstalk.utilities;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ReflectionHelper {

    private ReflectionHelper() {
        // utility class
    }

    public static boolean classExists(final String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    public static <T> Collection<T> parametersAnnotatedBy(
            final Class<? extends Annotation> annotationClass,
            final Method method,
            final @Nullable Object[] objectParameters
    ) {
        Collection<T> results = new ArrayList<>();
        Parameter[] reflectedParameters = method.getParameters();

        for (int i = 0; i < reflectedParameters.length; i++) {
            @Nullable Parameter reflectedParameter = reflectedParameters[i];
            if (reflectedParameter != null && reflectedParameter.isAnnotationPresent(annotationClass)) {
                //noinspection unchecked
                results.add((T) objectParameters[i]);
            }
        }

        return results;
    }

    public static <T> @Nullable T parameterAnnotatedBy(
            final Class<? extends Annotation> annotationClass,
            final Method method,
            final @Nullable Object[] objectParameters
    ) {
        Collection<T> parameters = parametersAnnotatedBy(annotationClass, method, objectParameters);

        if (parameters.isEmpty()) {
            return null;
        }

        return parameters.iterator().next();
    }

}
