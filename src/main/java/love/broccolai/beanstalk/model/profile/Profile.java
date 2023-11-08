package love.broccolai.beanstalk.model.profile;

import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class Profile {

    private final UUID uuid;
    private int data;

    public Profile(final UUID uuid, final int data) {
        this.uuid = uuid;
        this.data = data;
    }

    public UUID uuid() {
        return this.uuid;
    }

    public int data() {
        return this.data;
    }

    public void data(final int data) {
        this.data = data;
    }

}
