package fuzs.puzzleslib.neoforge.impl.attachment.builder;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;

public final class NeoForgeLevelDataAttachmentBuilder<V> extends NeoForgeDataAttachmentBuilder<Level, V> implements DataAttachmentRegistry.Builder<Level, V> {

    @Override
    protected RegistryAccess getRegistryAccess(Level holder) {
        return holder.registryAccess();
    }
}
