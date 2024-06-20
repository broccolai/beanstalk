package love.broccolai.beanstalk.commands.cloud;

import love.broccolai.beanstalk.commands.arguments.ProfileDescriptor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface CloudArgumentFactory {

    ProfileDescriptor profile();

}
