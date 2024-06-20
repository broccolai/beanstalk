package love.broccolai.beanstalk.service.profile.provider;

import io.leangen.geantyref.TypeToken;
import java.util.UUID;
import love.broccolai.beanstalk.model.profile.Profile;
import love.broccolai.beanstalk.service.profile.ProfileServiceContext;
import org.incendo.cloud.services.type.PartialResultService;

public interface PartialProfileProvider extends PartialResultService<UUID, Profile, ProfileServiceContext> {

    TypeToken<PartialProfileProvider> TYPE = TypeToken.get(PartialProfileProvider.class);

}
