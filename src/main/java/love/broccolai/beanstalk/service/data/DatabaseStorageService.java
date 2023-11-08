package love.broccolai.beanstalk.service.data;

import com.google.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.utilities.QueriesLocator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jdbi.v3.core.Jdbi;

@DefaultQualifier(NonNull.class)
public class DatabaseStorageService implements StorageService {

    private final QueriesLocator locator = new QueriesLocator();

    private final Jdbi jdbi;

    @Inject
    public DatabaseStorageService(final Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public Optional<Profile> loadProfile(final UUID uuid) {
        return this.jdbi.withHandle(handle -> {
            return handle.createQuery(this.locator.query("select-profile"))
                .bind("uuid", uuid)
                .mapTo(Profile.class)
                .findFirst();
        });
    }

    @Override
    public void saveProfile(final Profile profile) {
        this.jdbi.useHandle(handle -> {
            handle.createUpdate(this.locator.query("save-profile"))
                .bind("uuid", profile.uuid())
                .bind("data", profile.data())
                .execute();
        });
    }

}
