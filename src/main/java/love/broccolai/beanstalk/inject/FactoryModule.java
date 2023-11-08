package love.broccolai.beanstalk.inject;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import love.broccolai.beanstalk.commands.cloud.CloudArgumentFactory;

public final class FactoryModule extends AbstractModule {

    @Override
    protected void configure() {
        this.install(new FactoryModuleBuilder()
            .build(CloudArgumentFactory.class)
        );
    }

}
