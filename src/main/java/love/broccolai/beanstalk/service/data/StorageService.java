package love.broccolai.beanstalk.service.data;

import java.util.Optional;
import java.util.UUID;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.Service;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface StorageService extends Service {

    Optional<Profile> loadProfile(UUID uuid);

    void saveProfile(Profile profile);

}
