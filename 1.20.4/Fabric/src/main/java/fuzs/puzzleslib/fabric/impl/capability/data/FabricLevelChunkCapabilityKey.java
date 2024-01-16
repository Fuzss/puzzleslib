package fuzs.puzzleslib.fabric.impl.capability.data;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.LevelChunkCapabilityKey;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public class FabricLevelChunkCapabilityKey<C extends CapabilityComponent<LevelChunk>> extends FabricCapabilityKey<LevelChunk, C> implements LevelChunkCapabilityKey<C> {

    public FabricLevelChunkCapabilityKey(ComponentKey<ComponentAdapter<LevelChunk, C>> capability) {
        super(capability);
    }

    @Override
    public boolean isProvidedBy(@Nullable Object holder) {
        return holder instanceof LevelChunk && super.isProvidedBy(holder);
    }
}
