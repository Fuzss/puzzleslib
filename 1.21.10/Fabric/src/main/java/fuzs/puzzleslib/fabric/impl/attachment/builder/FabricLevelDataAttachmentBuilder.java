package fuzs.puzzleslib.fabric.impl.attachment.builder;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import net.minecraft.world.level.Level;

public final class FabricLevelDataAttachmentBuilder<V> extends FabricDataAttachmentBuilder<Level, V, DataAttachmentRegistry.LevelBuilder<V>> implements DataAttachmentRegistry.LevelBuilder<V> {

    public FabricLevelDataAttachmentBuilder() {
        super(Level::registryAccess);
    }

    @Override
    public DataAttachmentRegistry.LevelBuilder<V> getThis() {
        return this;
    }
}
