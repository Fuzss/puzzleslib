package fuzs.puzzleslib.neoforge.impl.attachment;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.impl.attachment.DataAttachmentRegistryImpl;
import fuzs.puzzleslib.neoforge.impl.attachment.builder.NeoForgeBlockEntityDataAttachmentBuilder;
import fuzs.puzzleslib.neoforge.impl.attachment.builder.NeoForgeDataAttachmentBuilder;
import fuzs.puzzleslib.neoforge.impl.attachment.builder.NeoForgeEntityDataAttachmentBuilder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public final class NeoForgeDataAttachmentRegistryImpl implements DataAttachmentRegistryImpl {

    @Override
    public <V> DataAttachmentRegistry.EntityBuilder<V> getEntityTypeBuilder() {
        return new NeoForgeEntityDataAttachmentBuilder<>();
    }

    @Override
    public <V> DataAttachmentRegistry.BlockEntityBuilder<V> getBlockEntityTypeBuilder() {
        return new NeoForgeBlockEntityDataAttachmentBuilder<>();
    }

    @Override
    public <V> DataAttachmentRegistry.Builder<LevelChunk, V> getLevelChunkBuilder() {
        return new NeoForgeDataAttachmentBuilder<>((LevelChunk levelChunk) -> levelChunk.getLevel().registryAccess());
    }

    @Override
    public <V> DataAttachmentRegistry.Builder<Level, V> getLevelBuilder() {
        return new NeoForgeDataAttachmentBuilder<>(Level::registryAccess);
    }
}
