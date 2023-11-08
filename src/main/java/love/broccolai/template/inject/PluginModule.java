package love.broccolai.template.inject;

import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.FilteringCommandSuggestionProcessor;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.sql.DataSource;
import love.broccolai.template.TemplatePlugin;
import love.broccolai.template.commands.cloud.ExceptionHandler;
import love.broccolai.template.data.ProfileMapper;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;

@DefaultQualifier(NonNull.class)
public final class PluginModule extends AbstractModule {

    private final TemplatePlugin plugin;

    public PluginModule(final TemplatePlugin plugin) {
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
        Flyway.configure(TemplatePlugin.class.getClassLoader())
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
    private CommandManager<CommandSender> commandManager(
        final Plugin plugin,
        final ExceptionHandler exceptionHandler
    ) throws Exception {
        PaperCommandManager<CommandSender> commandManager = PaperCommandManager.createNative(
            plugin,
            AsynchronousCommandExecutionCoordinator.<CommandSender>builder().withAsynchronousParsing().build()
        );

        if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions();
        }

        commandManager.commandSuggestionProcessor(
            new FilteringCommandSuggestionProcessor<>(
                FilteringCommandSuggestionProcessor.Filter.<CommandSender>contains(true).andTrimBeforeLastSpace()
            )
        );

        exceptionHandler.apply(commandManager);

        return commandManager;
    }

}
