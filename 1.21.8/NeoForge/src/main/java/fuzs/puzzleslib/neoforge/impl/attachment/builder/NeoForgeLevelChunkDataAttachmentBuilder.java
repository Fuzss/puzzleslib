package fuzs.puzzleslib.neoforge.impl.attachment.builder;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import net.minecraft.world.level.chunk.LevelChunk;

public final class NeoForgeLevelChunkDataAttachmentBuilder<V> extends NeoForgeDataAttachmentBuilder<LevelChunk, V, DataAttachmentRegistry.LevelChunkBuilder<V>> implements DataAttachmentRegistry.LevelChunkBuilder<V> {

    public NeoForgeLevelChunkDataAttachmentBuilder() {
        super((LevelChunk levelChunk) -> levelChunk.getLevel().registryAccess());
    }

    @Override
    public DataAttachmentRegistry.LevelChunkBuilder<V> getThis() {
        return this;
    }
}
