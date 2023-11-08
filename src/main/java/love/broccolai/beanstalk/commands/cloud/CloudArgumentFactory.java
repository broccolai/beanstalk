package love.broccolai.beanstalk.commands.cloud;

import com.google.inject.assistedinject.Assisted;
import love.broccolai.beanstalk.commands.arguments.ProfileArgument;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface CloudArgumentFactory {

    ProfileArgument profile(
        @Assisted("name") String name,
        @Assisted("required") boolean required
    );

}
