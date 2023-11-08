package love.broccolai.template.service.task;

import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class PaperTaskService implements TaskService {

    private final Plugin plugin;

    @Inject
    public PaperTaskService(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void sync(final Runnable runnable) {
        Bukkit.getScheduler().runTask(this.plugin, runnable);
    }

    @Override
    public void async(final Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }

}
