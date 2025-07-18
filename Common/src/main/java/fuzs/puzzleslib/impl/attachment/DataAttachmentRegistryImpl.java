package fuzs.puzzleslib.impl.attachment;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;

public interface DataAttachmentRegistryImpl {
    DataAttachmentRegistryImpl INSTANCE = ProxyImpl.get().getDataAttachmentRegistry();

    <V> DataAttachmentRegistry.EntityBuilder<V> getEntityTypeBuilder();

    <V> DataAttachmentRegistry.BlockEntityBuilder<V> getBlockEntityTypeBuilder();

    <V> DataAttachmentRegistry.LevelChunkBuilder<V> getLevelChunkBuilder();

    <V> DataAttachmentRegistry.LevelBuilder<V> getLevelBuilder();
}
