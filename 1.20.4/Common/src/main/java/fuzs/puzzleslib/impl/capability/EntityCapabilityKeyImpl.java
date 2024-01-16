package fuzs.puzzleslib.impl.capability;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CopyStrategy;
import fuzs.puzzleslib.api.capability.v3.data.EntityCapabilityKey;
import fuzs.puzzleslib.api.capability.v3.data.SyncStrategy;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingConversionCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerEvents;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface EntityCapabilityKeyImpl<T extends Entity, C extends CapabilityComponent<T>> extends EntityCapabilityKey.Mutable<T, C> {

    @Override
    default Mutable<T, C> setSyncStrategy(SyncStrategy syncStrategy) {
        if (this.getSyncStrategy() != SyncStrategy.MANUAL) {
            throw new IllegalStateException("Sync strategy has already been set!");
        } else {
            if (syncStrategy != SyncStrategy.MANUAL) {
                PlayerEvents.LOGGED_IN.register((ServerPlayer player) -> {
                    if (this.isProvidedBy(player)) {
                        PuzzlesLibMod.NETWORK.sendTo(player, this.toPacket(this.get((T) player)));
                    }
                });
                PlayerEvents.AFTER_CHANGE_DIMENSION.register((ServerPlayer player, ServerLevel from, ServerLevel to) -> {
                    if (this.isProvidedBy(player)) {
                        PuzzlesLibMod.NETWORK.sendTo(player, this.toPacket(this.get((T) player)));
                    }
                });
                PlayerEvents.RESPAWN.register((ServerPlayer player, boolean originalStillAlive) -> {
                    if (this.isProvidedBy(player)) {
                        PuzzlesLibMod.NETWORK.sendTo(player, this.toPacket(this.get((T) player)));
                    }
                });
                if (syncStrategy == SyncStrategy.TRACKING) {
                    PlayerEvents.START_TRACKING.register((Entity trackedEntity, ServerPlayer player) -> {
                        if (this.isProvidedBy(trackedEntity)) {
                            PuzzlesLibMod.NETWORK.sendTo(player, this.toPacket(this.get((T) trackedEntity)));
                        }
                    });
                }
            }
            return this;
        }
    }

    @Override
    default Mutable<T, C> setCopyStrategy(CopyStrategy copyStrategy) {
        if (this.getCopyStrategy() != CopyStrategy.NEVER) {
            throw new IllegalStateException("Copy strategy has already been set!");
        } else if (copyStrategy == CopyStrategy.ALWAYS) {
            LivingConversionCallback.EVENT.register((LivingEntity originalEntity, LivingEntity newEntity) -> {
                if (this.isProvidedBy(originalEntity) && this.isProvidedBy(newEntity)) {
                    this.getCopyStrategy().copy(originalEntity, this.get((T) originalEntity), newEntity, this.get((T) newEntity), false);
                }
            });
        }
        return this;
    }

    default void initialize() {
        PlayerEvents.COPY.register((ServerPlayer originalPlayer, ServerPlayer newPlayer, boolean originalStillAlive) -> {
            if (this.isProvidedBy(originalPlayer) && this.isProvidedBy(newPlayer)) {
                this.getCopyStrategy().copy(originalPlayer, this.get((T) originalPlayer), newPlayer, this.get((T) newPlayer), originalStillAlive);
            }
        });
    }
}
