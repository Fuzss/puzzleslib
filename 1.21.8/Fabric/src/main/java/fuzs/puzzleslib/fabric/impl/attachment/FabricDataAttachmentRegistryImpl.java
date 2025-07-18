package fuzs.puzzleslib.fabric.impl.attachment;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.fabric.impl.attachment.builder.FabricBlockEntityDataAttachmentBuilder;
import fuzs.puzzleslib.fabric.impl.attachment.builder.FabricEntityDataAttachmentBuilder;
import fuzs.puzzleslib.fabric.impl.attachment.builder.FabricLevelChunkDataAttachmentBuilder;
import fuzs.puzzleslib.fabric.impl.attachment.builder.FabricLevelDataAttachmentBuilder;
import fuzs.puzzleslib.impl.attachment.DataAttachmentRegistryImpl;

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
    public <V> DataAttachmentRegistry.LevelChunkBuilder<V> getLevelChunkBuilder() {
        return new FabricLevelChunkDataAttachmentBuilder<>();
    }

    @Override
    public <V> DataAttachmentRegistry.LevelBuilder<V> getLevelBuilder() {
        return new FabricLevelDataAttachmentBuilder<>();
    }
}
