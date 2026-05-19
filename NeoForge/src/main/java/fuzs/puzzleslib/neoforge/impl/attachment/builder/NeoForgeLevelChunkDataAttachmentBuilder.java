package fuzs.puzzleslib.neoforge.impl.attachment.builder;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.chunk.LevelChunk;

public final class NeoForgeLevelChunkDataAttachmentBuilder<V> extends NeoForgeDataAttachmentBuilder<LevelChunk, V> implements DataAttachmentRegistry.Builder<LevelChunk, V> {

    @Override
    protected RegistryAccess getRegistryAccess(LevelChunk holder) {
        return holder.getLevel().registryAccess();
    }
}
