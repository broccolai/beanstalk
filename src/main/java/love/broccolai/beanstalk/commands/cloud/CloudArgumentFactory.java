package love.broccolai.beanstalk.commands.cloud;

import love.broccolai.beanstalk.commands.arguments.ProfileDescriptor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CloudArgumentFactory {

    ProfileDescriptor profile();

}
