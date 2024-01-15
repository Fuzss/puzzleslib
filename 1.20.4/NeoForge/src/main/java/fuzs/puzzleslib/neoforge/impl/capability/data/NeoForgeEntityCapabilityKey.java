package fuzs.puzzleslib.neoforge.impl.capability.data;

import fuzs.puzzleslib.api.capability.v2.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v2.data.EntityCapabilityKey;
import fuzs.puzzleslib.api.capability.v2.data.SyncStrategy;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Predicate;

public class NeoForgeEntityCapabilityKey<T extends Entity, C extends CapabilityComponent<T>> extends NeoForgeCapabilityKey<T, C> implements EntityCapabilityKey<T, C> {

    public NeoForgeEntityCapabilityKey(DeferredHolder<AttachmentType<?>, AttachmentType<C>> holder, Predicate<Object> filter) {
        super(holder, filter);
    }

    @Override
    public void setSyncStrategy(SyncStrategy syncStrategy) {

    }

    @Override
    public SyncStrategy getSyncStrategy() {
        return null;
    }
}
