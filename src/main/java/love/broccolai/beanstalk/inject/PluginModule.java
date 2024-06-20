package love.broccolai.beanstalk.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.sql.DataSource;
import love.broccolai.beanstalk.Beanstalk;
import love.broccolai.beanstalk.commands.cloud.ExceptionHandler;
import love.broccolai.beanstalk.commands.cloud.captions.BeanstalkCaptionProvider;
import love.broccolai.beanstalk.commands.cloud.commander.Commander;
import love.broccolai.beanstalk.data.ProfileMapper;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.flywaydb.core.Flyway;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.suggestion.FilteringSuggestionProcessor;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;

@DefaultQualifier(NonNull.class)
public final class PluginModule extends AbstractModule {

    private final Beanstalk plugin;

    public PluginModule(final Beanstalk plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        this.bind(Plugin.class).toInstance(this.plugin);
        this.bind(Path.class).toInstance(this.plugin.getDataFolder().toPath());
        this.bind(Logger.class).toInstance(this.plugin.getSLF4JLogger());
    }

    @Provides
    @Singleton
    public DataSource provideDataSource(final Path folder) throws IOException {
        HikariConfig hikariConfig = new HikariConfig();

        Path file = folder.resolve("storage.db");

        if (!Files.exists(file)) {
            Files.createFile(file);
        }

        hikariConfig.setDriverClassName("org.h2.Driver");
        hikariConfig.setJdbcUrl("jdbc:h2:" + file.toAbsolutePath() + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE");

        hikariConfig.setMaximumPoolSize(10);

        DataSource dataSource = new HikariDataSource(hikariConfig);

        //todo(josh): find better place for this?
        Flyway.configure(Beanstalk.class.getClassLoader())
            .baselineOnMigrate(true)
            .locations("classpath:queries/migrations")
            .dataSource(dataSource)
            .load()
            .migrate();

        return dataSource;
    }

    @Provides
    @Singleton
    public Jdbi provideJdbi(final DataSource dataSource) {
        return Jdbi.create(dataSource)
            .registerRowMapper(new ProfileMapper());
    }

    @Provides
    @Singleton
    private CommandManager<Commander> commandManager(
        final Plugin plugin,
        final ExceptionHandler exceptionHandler
    ) {
        SenderMapper<CommandSourceStack, Commander> senderMapper = SenderMapper.create(
            Commander::from,
            Commander::stack
        );

        PaperCommandManager<Commander> commandManager = PaperCommandManager.builder(senderMapper)
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(plugin);

        commandManager.suggestionProcessor(
            new FilteringSuggestionProcessor<>(
                FilteringSuggestionProcessor.Filter.contains(true)
            )
        );

        exceptionHandler.apply(commandManager);

        commandManager.captionRegistry().registerProvider(new BeanstalkCaptionProvider());

        return commandManager;
    }

}
