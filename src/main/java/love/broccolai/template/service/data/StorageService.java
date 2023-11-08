package love.broccolai.template.service.data;

import java.util.Optional;
import java.util.UUID;
import love.broccolai.template.model.profile.Profile;
import love.broccolai.template.service.Service;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface StorageService extends Service {

    Optional<Profile> loadProfile(UUID uuid);

    void saveProfile(Profile profile);

}
