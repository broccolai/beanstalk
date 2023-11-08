package love.broccolai.template.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import love.broccolai.template.TemplatePlugin;
import love.broccolai.template.config.Configuration;
import love.broccolai.template.config.LocaleConfiguration;
import love.broccolai.template.config.MainConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;

@DefaultQualifier(NonNull.class)
public final class ConfigurationModule extends AbstractModule {

    @Provides
    @Singleton
    public MainConfiguration provideMainConfiguration(final Path folder) throws IOException {
        Path file = folder.resolve("config.conf");

        return this.configuration(MainConfiguration.class, file);
    }

    @Provides
    @Singleton
    public LocaleConfiguration provideLocaleConfiguration(
        final Path folder,
        final MainConfiguration configuration
    ) throws IOException {
        String localeFileName = this.createLocaleFileName(configuration.locale);

        Path file = folder
            .resolve("locales")
            .resolve(localeFileName);

        ConfigurationLoader<?> loader = this.pathConfigurationLoader(file);

        ConfigurationNode node = loader.load();
        ConfigurationNode defaultNode = this.createDefaultNode(localeFileName);

        node = node.mergeFrom(defaultNode);

        loader.save(node);
        return node.visit(new LocaleConfiguration.Visitor());
    }

    private ConfigurationNode createDefaultNode(final String localeFileName) throws ConfigurateException {
        URL defaultNode = Objects.requireNonNull(TemplatePlugin.class.getResource("/locales/" + localeFileName));

        return HoconConfigurationLoader.builder()
            .defaultOptions(opts -> opts.shouldCopyDefaults(true))
            .url(defaultNode)
            .build()
            .load();
    }

    private String createLocaleFileName(final String source) {
        return "locale_" + source + ".conf";
    }

    private <T extends Configuration> T configuration(
        final Class<T> clazz,
        final Path file
    ) throws IOException {
        ObjectMapper<T> MAPPER = ObjectMapper.factory().get(clazz);

        if (Files.notExists(file)) {
            Files.createFile(file);
        }

        ConfigurationLoader<?> loader = this.pathConfigurationLoader(file);

        ConfigurationNode node = loader.load();
        T config = MAPPER.load(node);

        MAPPER.save(config, node);
        loader.save(node);

        return config;
    }

    private ConfigurationLoader<?> pathConfigurationLoader(final Path path) {
        return HoconConfigurationLoader.builder()
            .defaultOptions(opts -> opts.shouldCopyDefaults(true))
            .path(path)
            .build();
    }

}
