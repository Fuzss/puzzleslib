package fuzs.puzzleslib.fabric.impl.capability.data;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CopyStrategy;
import fuzs.puzzleslib.api.capability.v3.data.SyncStrategy;
import fuzs.puzzleslib.impl.capability.EntityCapabilityKeyImpl;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.entity.Entity;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class FabricEntityCapabilityKey<T extends Entity, C extends CapabilityComponent<T>> extends FabricCapabilityKey<T, C> implements EntityCapabilityKeyImpl<T, C> {
    private SyncStrategy syncStrategy = SyncStrategy.MANUAL;
    private CopyStrategy copyStrategy = CopyStrategy.NEVER;

    public FabricEntityCapabilityKey(AttachmentType<C> attachmentType, Predicate<Object> filter, Supplier<C> factory) {
        super(attachmentType, filter, factory);
        this.initialize();
    }

    @Override
    public Mutable<T, C> setSyncStrategy(SyncStrategy syncStrategy) {
        EntityCapabilityKeyImpl.super.setSyncStrategy(syncStrategy);
        this.syncStrategy = syncStrategy;
        return this;
    }

    @Override
    public Mutable<T, C> setCopyStrategy(CopyStrategy copyStrategy) {
        EntityCapabilityKeyImpl.super.setCopyStrategy(copyStrategy);
        this.copyStrategy = copyStrategy;
        return this;
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
