package fuzs.puzzleslib.api.capability.v3.data;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Convenient {@link CapabilityKey} implementation for {@link Level}.
 *
 * @param <C> capability component type
 */
@ApiStatus.NonExtendable
public interface LevelCapabilityKey<C extends CapabilityComponent<Level>> extends CapabilityKey<Level, C> {

    @Override
    default void setChanged(C capabilityComponent, @Nullable PlayerSet playerSet) {
        // NO-OP
    }

    @Override
    default ClientboundMessage<?> toPacket(C capabilityComponent) {
        throw new UnsupportedOperationException();
    }
}
