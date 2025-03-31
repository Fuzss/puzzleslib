package fuzs.puzzleslib.api.capability.v3.data;

import fuzs.puzzleslib.api.network.v4.PlayerSet;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Convenient {@link CapabilityKey} implementation for {@link BlockEntity}.
 *
 * @param <T> block entity type
 * @param <C> capability component type
 */
@ApiStatus.NonExtendable
public interface BlockEntityCapabilityKey<T extends BlockEntity, C extends CapabilityComponent<T>> extends CapabilityKey<T, C> {

    @Override
    default void setChanged(C capabilityComponent, @Nullable PlayerSet playerSet) {
        capabilityComponent.getHolder().setChanged();
    }

    @Override
    default ClientboundPlayMessage toMessage(C capabilityComponent) {
        throw new UnsupportedOperationException();
    }
}
