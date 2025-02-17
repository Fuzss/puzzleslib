package fuzs.puzzleslib.fabric.impl.attachment;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.fabric.impl.attachment.builder.FabricBlockEntityDataAttachmentBuilder;
import fuzs.puzzleslib.fabric.impl.attachment.builder.FabricDataAttachmentBuilder;
import fuzs.puzzleslib.fabric.impl.attachment.builder.FabricEntityDataAttachmentBuilder;
import fuzs.puzzleslib.impl.attachment.DataAttachmentRegistryImpl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public final class FabricDataAttachmentRegistryImpl implements DataAttachmentRegistryImpl {

    @Override
    public <V> DataAttachmentRegistry.EntityBuilder<V> getEntityTypeBuilder() {
        return new FabricEntityDataAttachmentBuilder<>();
    }

    @Override
    public <V> DataAttachmentRegistry.BlockEntityBuilder<V> getBlockEntityTypeBuilder() {
        return new FabricBlockEntityDataAttachmentBuilder<>();
    }

    @Override
    public <V> DataAttachmentRegistry.Builder<LevelChunk, V> getLevelChunkBuilder() {
        return new FabricDataAttachmentBuilder<>((LevelChunk levelChunk) -> levelChunk.getLevel().registryAccess());
    }

    @Override
    public <V> DataAttachmentRegistry.Builder<Level, V> getLevelBuilder() {
        return new FabricDataAttachmentBuilder<>(Level::registryAccess);
    }
}
