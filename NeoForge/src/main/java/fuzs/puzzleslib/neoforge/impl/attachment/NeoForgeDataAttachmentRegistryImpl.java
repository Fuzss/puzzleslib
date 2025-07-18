package fuzs.puzzleslib.neoforge.impl.attachment;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.impl.attachment.DataAttachmentRegistryImpl;
import fuzs.puzzleslib.neoforge.impl.attachment.builder.NeoForgeBlockEntityDataAttachmentBuilder;
import fuzs.puzzleslib.neoforge.impl.attachment.builder.NeoForgeEntityDataAttachmentBuilder;
import fuzs.puzzleslib.neoforge.impl.attachment.builder.NeoForgeLevelChunkDataAttachmentBuilder;
import fuzs.puzzleslib.neoforge.impl.attachment.builder.NeoForgeLevelDataAttachmentBuilder;

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
    public <V> DataAttachmentRegistry.LevelChunkBuilder<V> getLevelChunkBuilder() {
        return new NeoForgeLevelChunkDataAttachmentBuilder<>();
    }

    @Override
    public <V> DataAttachmentRegistry.LevelBuilder<V> getLevelBuilder() {
        return new NeoForgeLevelDataAttachmentBuilder<>();
    }
}
