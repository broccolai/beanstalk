package love.broccolai.beanstalk.config;

import com.google.inject.Singleton;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@Singleton
@DefaultQualifier(NonNull.class)
public final class MainConfiguration implements Configuration {

    public String locale = "en";
}
