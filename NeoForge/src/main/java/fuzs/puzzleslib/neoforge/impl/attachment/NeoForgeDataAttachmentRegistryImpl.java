package fuzs.puzzleslib.neoforge.impl.attachment;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.impl.attachment.DataAttachmentRegistryImpl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

public final class NeoForgeDataAttachmentRegistryImpl implements DataAttachmentRegistryImpl {

    @Override
    public <A> DataAttachmentRegistry.EntityBuilder<A> getEntityTypeBuilder() {
        return new NeoForgeEntityDataAttachmentBuilder<>();
    }

    @Override
    public <A> DataAttachmentRegistry.Builder<BlockEntity, A> getBlockEntityTypeBuilder() {
        return new NeoForgeBlockEntityDataAttachmentBuilder<>();
    }

    @Override
    public <A> DataAttachmentRegistry.Builder<LevelChunk, A> getLevelChunkBuilder() {
        return new NeoForgeDataAttachmentBuilder<>();
    }

    @Override
    public <A> DataAttachmentRegistry.Builder<Level, A> getLevelBuilder() {
        return new NeoForgeDataAttachmentBuilder<>();
    }
}
