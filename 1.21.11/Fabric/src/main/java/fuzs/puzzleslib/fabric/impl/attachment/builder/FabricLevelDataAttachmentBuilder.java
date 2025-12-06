package fuzs.puzzleslib.fabric.impl.attachment.builder;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;

public final class FabricLevelDataAttachmentBuilder<V> extends FabricDataAttachmentBuilder<Level, V, DataAttachmentRegistry.LevelBuilder<V>> implements DataAttachmentRegistry.LevelBuilder<V> {

    @Override
    public DataAttachmentRegistry.LevelBuilder<V> getThis() {
        return this;
    }

    @Override
    protected RegistryAccess getRegistryAccess(Level holder) {
        return holder.registryAccess();
    }
}
