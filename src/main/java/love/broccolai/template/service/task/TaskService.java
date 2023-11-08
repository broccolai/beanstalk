package love.broccolai.template.service.task;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface TaskService {
    void sync(Runnable runnable);

    void async(Runnable runnable);
}
