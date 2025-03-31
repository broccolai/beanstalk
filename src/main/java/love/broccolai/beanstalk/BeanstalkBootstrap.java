package love.broccolai.beanstalk;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;

@SuppressWarnings("UnstableApiUsage")
public final class BeanstalkBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(final BootstrapContext bootstrapContext) {
    }

    @Override
    public Beanstalk createPlugin(final PluginProviderContext context) {
        return new Beanstalk();
    }

}
