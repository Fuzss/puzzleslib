package fuzs.puzzleslib.neoforge.impl.attachment.builder;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import net.minecraft.world.level.Level;

public final class NeoForgeLevelDataAttachmentBuilder<V> extends NeoForgeDataAttachmentBuilder<Level, V, DataAttachmentRegistry.LevelBuilder<V>> implements DataAttachmentRegistry.LevelBuilder<V> {

    public NeoForgeLevelDataAttachmentBuilder() {
        super(Level::registryAccess);
    }

    @Override
    public DataAttachmentRegistry.LevelBuilder<V> getThis() {
        return this;
    }
}
