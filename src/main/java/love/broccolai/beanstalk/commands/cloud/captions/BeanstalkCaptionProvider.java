package love.broccolai.beanstalk.commands.cloud.captions;

import love.broccolai.beanstalk.commands.cloud.commander.Commander;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.caption.DelegatingCaptionProvider;

@DefaultQualifier(NonNull.class)
public class BeanstalkCaptionProvider extends DelegatingCaptionProvider<Commander> {

    public static final String ARGUMENT_PARSE_FAILURE_PROFILE = "Could not parse profile for <input>";

    private static final CaptionProvider<?> PROVIDER = CaptionProvider.constantProvider()
        .putCaption(BeanstalkCaptionKeys.ARGUMENT_PARSE_FAILURE_PROFILE, ARGUMENT_PARSE_FAILURE_PROFILE)
        .build();

    @SuppressWarnings("unchecked")
    @Override
    public CaptionProvider<Commander> delegate() {
        return (CaptionProvider<Commander>) PROVIDER;
    }

}
