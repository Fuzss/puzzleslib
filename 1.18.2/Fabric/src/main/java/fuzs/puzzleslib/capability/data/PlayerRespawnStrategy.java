package fuzs.puzzleslib.capability.data;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;

/**
 * modes determining how capability data should be handled when the player entity is recreated, which will usually happen when returning from the end dimension and when respawning
 * this is basically the same class as in {@see <a href="https://github.com/OnyxStudios/Cardinal-Components-API">https://github.com/OnyxStudios/Cardinal-Components-API</a>} for the Fabric mod loader
 */
public class PlayerRespawnStrategy {
    /**
     * always copy data when recreating player
     */
    public static final PlayerRespawnStrategy ALWAYS_COPY = new PlayerRespawnStrategy(RespawnCopyStrategy.ALWAYS_COPY);
    /**
     * copy data when inventory contents are copied
     */
    public static final PlayerRespawnStrategy INVENTORY = new PlayerRespawnStrategy(RespawnCopyStrategy.INVENTORY);
    /**
     * copy data when returning from end, but never after dying
     */
    public static final PlayerRespawnStrategy LOSSLESS = new PlayerRespawnStrategy(RespawnCopyStrategy.LOSSLESS_ONLY);
    /**
     * never copy data
     */
    public static final PlayerRespawnStrategy NEVER = new PlayerRespawnStrategy(RespawnCopyStrategy.NEVER_COPY);

    /**
     * cardinal components api equivalent
     */
    private final RespawnCopyStrategy<Component> componentStrategy;

    /**
     * @param componentStrategy api equivalent
     */
    private PlayerRespawnStrategy(RespawnCopyStrategy<Component> componentStrategy) {
        this.componentStrategy = componentStrategy;
    }

    /**
     * simple method for converting to api equivalent, much more complex on Forge
     * @return converted to {@link RespawnCopyStrategy}
     */
    public RespawnCopyStrategy<Component> toComponentStrategy() {
        return this.componentStrategy;
    }
}
