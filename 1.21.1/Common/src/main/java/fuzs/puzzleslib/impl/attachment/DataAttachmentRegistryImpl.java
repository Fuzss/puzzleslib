package fuzs.puzzleslib.impl.attachment;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

public interface DataAttachmentRegistryImpl {
    DataAttachmentRegistryImpl INSTANCE = ProxyImpl.get().getDataAttachmentRegistry();

    <A> DataAttachmentRegistry.EntityBuilder<A> getEntityTypeBuilder();

    <A> DataAttachmentRegistry.Builder<BlockEntity, A> getBlockEntityTypeBuilder();

    <A> DataAttachmentRegistry.Builder<LevelChunk, A> getLevelChunkBuilder();

    <A> DataAttachmentRegistry.Builder<Level, A> getLevelBuilder();
}
