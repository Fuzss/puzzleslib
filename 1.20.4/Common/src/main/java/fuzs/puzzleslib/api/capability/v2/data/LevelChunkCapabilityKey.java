package fuzs.puzzleslib.api.capability.v2.data;

import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface LevelChunkCapabilityKey<C extends CapabilityComponent<LevelChunk>> extends CapabilityKey<LevelChunk, C> {

    @Override
    default void setChanged(C capabilityComponent) {
        capabilityComponent.getHolder().setUnsaved(true);
    }
}
