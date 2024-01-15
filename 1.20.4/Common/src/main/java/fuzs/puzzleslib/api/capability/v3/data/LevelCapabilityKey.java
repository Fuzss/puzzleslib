package fuzs.puzzleslib.api.capability.v3.data;

import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

/**
 * Convenient {@link CapabilityKey} implementation for {@link Level}.
 *
 * @param <C> capability component type
 */
@ApiStatus.NonExtendable
public interface LevelCapabilityKey<C extends CapabilityComponent<Level>> extends CapabilityKey<Level, C> {

    @Override
    default void setChanged(C capabilityComponent) {
        // NO-OP
    }
}
