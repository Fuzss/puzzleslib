package fuzs.puzzleslib.neoforge.impl.capability.data;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CopyStrategy;
import fuzs.puzzleslib.api.capability.v3.data.EntityCapabilityKey;
import fuzs.puzzleslib.api.capability.v3.data.SyncStrategy;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
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
            if (this.syncStrategy != SyncStrategy.MANUAL) {
                NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
                NeoForge.EVENT_BUS.addListener(this::onPlayerChangedDimension);
                if (syncStrategy == SyncStrategy.TRACKING) {
                    NeoForge.EVENT_BUS.addListener(this::onStartTracking);
                }
            }
            return this;
        }
    }

    @Override
    public Mutable<T, C> setCopyStrategy(CopyStrategy copyStrategy) {
        if (this.copyStrategy != CopyStrategy.NEVER) {
            throw new IllegalStateException("Copy strategy has already been set!");
        } else {
            this.copyStrategy = copyStrategy;
            if (this.copyStrategy != CopyStrategy.NEVER) {
                NeoForge.EVENT_BUS.addListener(this::onPlayerClone);
                NeoForge.EVENT_BUS.addListener(this::onAfterLivingConversion);
            }
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

    private void onPlayerClone(final PlayerEvent.Clone evt) {
        if (evt.isWasDeath() && this.isProvidedBy(evt.getOriginal()) && this.isProvidedBy(evt.getEntity())) {
            this.copyStrategy.copy(evt.getOriginal(), this.get((T) evt.getOriginal()), evt.getEntity(), this.get((T) evt.getEntity()));
        }
    }

    private void onAfterLivingConversion(final LivingConversionEvent.Post evt) {
        if (this.isProvidedBy(evt.getEntity()) && this.isProvidedBy(evt.getOutcome())) {
            this.copyStrategy.copy(evt.getEntity(), this.get((T) evt.getEntity()), evt.getOutcome(), this.get((T) evt.getOutcome()));
        }
    }

    private void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent evt) {
        if (this.isProvidedBy(evt.getEntity())) {
            this.syncStrategy.send(evt.getEntity(), this.toPacket(this.get((T) evt.getEntity())));
        }
    }

    private void onPlayerChangedDimension(final PlayerEvent.PlayerChangedDimensionEvent evt) {
        if (this.isProvidedBy(evt.getEntity())) {
            this.syncStrategy.send(evt.getEntity(), this.toPacket(this.get((T) evt.getEntity())));
        }
    }

    private void onStartTracking(final PlayerEvent.StartTracking evt) {
        if (this.isProvidedBy(evt.getTarget()) && this.syncStrategy == SyncStrategy.TRACKING) {
            // we only want to sync to the client that just started tracking, so use SyncStrategy#SELF
            SyncStrategy.SELF.send(evt.getEntity(), this.toPacket(this.get((T) evt.getTarget())));
        }
    }
}
