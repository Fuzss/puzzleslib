package fuzs.puzzleslib.api.capability.v3.data;

import fuzs.puzzleslib.api.network.v4.PlayerSet;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Convenient {@link CapabilityKey} implementation for {@link LevelChunk}.
 *
 * @param <C> capability component type
 */
@ApiStatus.NonExtendable
public interface LevelChunkCapabilityKey<C extends CapabilityComponent<LevelChunk>> extends CapabilityKey<LevelChunk, C> {

    @Override
    default void setChanged(C capabilityComponent, @Nullable PlayerSet playerSet) {
        capabilityComponent.getHolder().markUnsaved();
    }

    @Override
    default ClientboundPlayMessage toMessage(C capabilityComponent) {
        throw new UnsupportedOperationException();
    }
}
