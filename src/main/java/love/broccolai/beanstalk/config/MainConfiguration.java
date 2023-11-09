package love.broccolai.beanstalk.config;

import com.google.inject.Singleton;
import java.util.List;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@Singleton
public final class MainConfiguration implements Configuration {

    public String locale = "en";

    public ItemConfiguration item;

    @ConfigSerializable
    public static final class ItemConfiguration {

        public String material = "minecraft:feather";

        public String name = "<light_purple><bold>Flight Feather";

        public List<String> lore = List.of(
            "<white>Provides <green><duration></green> of flight",
            "",
            "<dark_gray>Right click to redeem"
        );

        public int customModelData = 0;

        public boolean shouldGlow = true;
    }
}
