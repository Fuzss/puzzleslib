package fuzs.puzzleslib.neoforge.impl.capability.data;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CopyStrategy;
import fuzs.puzzleslib.api.capability.v3.data.SyncStrategy;
import fuzs.puzzleslib.impl.capability.EntityCapabilityKeyImpl;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Predicate;

public class NeoForgeEntityCapabilityKey<T extends Entity, C extends CapabilityComponent<T>> extends NeoForgeCapabilityKey<T, C> implements EntityCapabilityKeyImpl<T, C> {
    private SyncStrategy syncStrategy = SyncStrategy.MANUAL;
    private CopyStrategy copyStrategy = CopyStrategy.NEVER;

    public NeoForgeEntityCapabilityKey(DeferredHolder<AttachmentType<?>, AttachmentType<C>> attachmentType, Codec<C> codec, Predicate<Object> filter) {
        super(attachmentType, codec, filter);
    }

    @Override
    public void configureBuilder(AttachmentType.Builder<C> builder) {
        if (this.getCopyStrategy().copyOnDeath()) builder.copyOnDeath();
        this.registerEventHandlers();
    }

    @Override
    public Mutable<T, C> setSyncStrategy(SyncStrategy syncStrategy) {
        this.syncStrategy = syncStrategy;
        return this;
    }

    @Override
    public Mutable<T, C> setCopyStrategy(CopyStrategy copyStrategy) {
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
