package love.broccolai.template.service.profile;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import love.broccolai.template.model.profile.Profile;
import love.broccolai.template.service.Service;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface ProfileService extends Service {

    Profile get(UUID uuid);

    Profile get(String username);

    Map<UUID, Profile> get(Collection<UUID> uuids);

    String name(Profile profile);

}
