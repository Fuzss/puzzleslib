package fuzs.puzzleslib.api.capability.v3.data;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;

/**
 * Convenient {@link CapabilityKey} implementation for {@link BlockEntity}.
 *
 * @param <T> block entity type
 * @param <C> capability component type
 */
@ApiStatus.NonExtendable
public interface BlockEntityCapabilityKey<T extends BlockEntity, C extends CapabilityComponent<T>> extends CapabilityKey<T, C> {

    @Override
    default void setChanged(C capabilityComponent) {
        capabilityComponent.getHolder().setChanged();
    }

    @Override
    default ClientboundMessage<?> toPacket(C capabilityComponent) {
        throw new UnsupportedOperationException();
    }
}
