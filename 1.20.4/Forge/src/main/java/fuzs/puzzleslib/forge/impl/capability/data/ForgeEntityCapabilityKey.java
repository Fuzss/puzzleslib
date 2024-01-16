package fuzs.puzzleslib.forge.impl.capability.data;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CopyStrategy;
import fuzs.puzzleslib.api.capability.v3.data.SyncStrategy;
import fuzs.puzzleslib.impl.capability.EntityCapabilityKeyImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class ForgeEntityCapabilityKey<T extends Entity, C extends CapabilityComponent<T>> extends ForgeCapabilityKey<T, C> implements EntityCapabilityKeyImpl<T, C> {
    private SyncStrategy syncStrategy = SyncStrategy.MANUAL;
    private CopyStrategy copyStrategy = CopyStrategy.NEVER;

    public ForgeEntityCapabilityKey(ResourceLocation identifier, CapabilityTokenFactory<T, C> tokenFactory) {
        super(identifier, tokenFactory);
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
