package love.broccolai.beanstalk.utilities;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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

    public static <T> Class<T> sneakyClassForName(final String clazz) {
        try {
            return (Class<T>) Class.forName(clazz);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> parametersAnnotatedBy(
            final Class<? extends Annotation> annotationClass,
            final Method method,
            final @Nullable Object[] objectParameters
    ) {
        Collection<T> results = new ArrayList<>();
        Parameter[] reflectedParameters = method.getParameters();

        for (int i = 0; i < reflectedParameters.length; i++) {
            Parameter reflectedParameter = reflectedParameters[i];
            if (reflectedParameter.isAnnotationPresent(annotationClass)) {
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
