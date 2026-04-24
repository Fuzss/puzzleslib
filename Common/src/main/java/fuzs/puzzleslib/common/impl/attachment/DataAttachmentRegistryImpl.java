package fuzs.puzzleslib.common.impl.attachment;

import fuzs.puzzleslib.common.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.common.impl.core.proxy.ProxyImpl;

public interface DataAttachmentRegistryImpl {
    DataAttachmentRegistryImpl INSTANCE = ProxyImpl.get().getDataAttachmentRegistry();

    <V> DataAttachmentRegistry.EntityBuilder<V> getEntityTypeBuilder();

    <V> DataAttachmentRegistry.BlockEntityBuilder<V> getBlockEntityTypeBuilder();

    <V> DataAttachmentRegistry.LevelChunkBuilder<V> getLevelChunkBuilder();

    <V> DataAttachmentRegistry.LevelBuilder<V> getLevelBuilder();
}
