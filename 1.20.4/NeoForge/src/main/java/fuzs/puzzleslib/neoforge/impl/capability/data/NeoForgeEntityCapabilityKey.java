package fuzs.puzzleslib.neoforge.impl.capability.data;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CopyStrategy;
import fuzs.puzzleslib.api.capability.v3.data.EntityCapabilityKey;
import fuzs.puzzleslib.api.capability.v3.data.SyncStrategy;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Predicate;

public class NeoForgeEntityCapabilityKey<T extends Entity, C extends CapabilityComponent<T>> extends NeoForgeCapabilityKey<T, C> implements EntityCapabilityKey.Mutable<T, C> {
    private SyncStrategy syncStrategy = SyncStrategy.MANUAL;
    private CopyStrategy copyStrategy = CopyStrategy.NEVER;

    public NeoForgeEntityCapabilityKey(DeferredHolder<AttachmentType<?>, AttachmentType<C>> holder, Predicate<Object> filter) {
        super(holder, filter);
    }

    @Override
    public Mutable<T, C> setSyncStrategy(SyncStrategy syncStrategy) {
        if (this.syncStrategy != SyncStrategy.MANUAL) {
            throw new IllegalStateException("Sync strategy has already been set!");
        } else {
            this.syncStrategy = syncStrategy;
            return this;
        }
    }

    @Override
    public Mutable<T, C> setCopyStrategy(CopyStrategy copyStrategy) {
        if (this.copyStrategy != CopyStrategy.NEVER) {
            throw new IllegalStateException("Copy strategy has already been set!");
        } else {
            this.copyStrategy = copyStrategy;
            return this;
        }
    }

    @Override
    public SyncStrategy getSyncStrategy() {
        return this.syncStrategy;
    }

    @Override
    public CopyStrategy getCopyStrategy() {
        return this.copyStrategy;
    }
}
