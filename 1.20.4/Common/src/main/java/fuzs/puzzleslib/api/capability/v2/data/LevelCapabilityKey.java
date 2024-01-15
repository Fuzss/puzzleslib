package fuzs.puzzleslib.api.capability.v2.data;

import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface LevelCapabilityKey<C extends CapabilityComponent<Level>> extends CapabilityKey<Level, C> {

    @Override
    default void setChanged(C capabilityComponent) {
        // NO-OP
    }
}
