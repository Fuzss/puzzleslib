package fuzs.puzzleslib.fabric.impl.capability.data;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CopyStrategy;
import fuzs.puzzleslib.api.capability.v3.data.SyncStrategy;
import fuzs.puzzleslib.impl.capability.EntityCapabilityKeyImpl;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FabricEntityCapabilityKey<T extends Entity, C extends CapabilityComponent<T>> extends FabricCapabilityKey<T, C> implements EntityCapabilityKeyImpl<T, C> {
    private SyncStrategy syncStrategy = SyncStrategy.MANUAL;
    private CopyStrategy copyStrategy = CopyStrategy.NEVER;

    public FabricEntityCapabilityKey(Supplier<AttachmentType<C>> attachmentType, Predicate<Object> filter, Supplier<C> factory) {
        super(attachmentType, filter, factory);
    }

    @Override
    public void configureBuilder(AttachmentRegistry.Builder<C> builder) {
        // do not use the Fabric version, it does not clone the component
//        if (this.getCopyStrategy().copyOnDeath()) builder.copyOnDeath();
        this.registerEventHandlers();
    }

    @Override
    public void registerEventHandlers() {
        EntityCapabilityKeyImpl.super.registerEventHandlers();
        if (this.getCopyStrategy().copyOnDeath()) {
            ServerLivingEntityEvents.MOB_CONVERSION.register((Mob previous, Mob converted, boolean keepEquipment) -> {
                this.copy(previous, converted);
            });
        }
        // always register this to copy when still alive
        ServerPlayerEvents.COPY_FROM.register((ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) -> {
            if (alive || this.getCopyStrategy().copyOnDeath()) {
                this.copy(oldPlayer, newPlayer);
            }
        });
    }

    private void copy(Entity originalEntity, Entity newEntity) {
        Optional<C> originalCapability = this.getIfProvided(originalEntity);
        if (originalCapability.isPresent() && this.isProvidedBy(newEntity)) {
            C capabilityComponent = this.clone(originalCapability.get(), originalEntity.registryAccess());
            this.set((T) newEntity, capabilityComponent);
        }
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
