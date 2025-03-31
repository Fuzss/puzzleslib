package fuzs.puzzleslib.impl.attachment;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public interface DataAttachmentRegistryImpl {
    DataAttachmentRegistryImpl INSTANCE = ProxyImpl.get().getDataAttachmentRegistry();

    <V> DataAttachmentRegistry.EntityBuilder<V> getEntityTypeBuilder();

    <V> DataAttachmentRegistry.BlockEntityBuilder<V> getBlockEntityTypeBuilder();

    <V> DataAttachmentRegistry.Builder<LevelChunk, V> getLevelChunkBuilder();

    <V> DataAttachmentRegistry.Builder<Level, V> getLevelBuilder();
}
