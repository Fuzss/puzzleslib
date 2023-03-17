package fuzs.puzzleslib.api.capability.v2.initializer;

import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer;
import fuzs.puzzleslib.impl.capability.FabricCapabilityController;
import net.minecraft.world.level.chunk.LevelChunk;

public class ChunkComponentInitializerImpl implements ChunkComponentInitializer {

    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
        FabricCapabilityController.registerComponentFactories(LevelChunk.class, registry);
    }
}
