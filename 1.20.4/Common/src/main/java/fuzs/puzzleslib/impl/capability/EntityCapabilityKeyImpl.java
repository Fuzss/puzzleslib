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

import java.util.Optional;

public interface EntityCapabilityKeyImpl<T extends Entity, C extends CapabilityComponent<T>> extends EntityCapabilityKey.Mutable<T, C> {

    @Override
    default Mutable<T, C> setSyncStrategy(SyncStrategy syncStrategy) {
        if (this.getSyncStrategy() != SyncStrategy.MANUAL) {
            throw new IllegalStateException("Sync strategy has already been set!");
        } else {
            if (syncStrategy != SyncStrategy.MANUAL) {
                PlayerEvents.LOGGED_IN.register((ServerPlayer player) -> {
                    this.getIfProvided(player).ifPresent(capabilityComponent -> {
                        PuzzlesLibMod.NETWORK.sendTo(player, this.toPacket(capabilityComponent));
                    });
                });
                PlayerEvents.AFTER_CHANGE_DIMENSION.register((ServerPlayer player, ServerLevel from, ServerLevel to) -> {
                    this.getIfProvided(player).ifPresent(capabilityComponent -> {
                        PuzzlesLibMod.NETWORK.sendTo(player, this.toPacket(capabilityComponent));
                    });
                });
                PlayerEvents.RESPAWN.register((ServerPlayer player, boolean originalStillAlive) -> {
                    this.getIfProvided(player).ifPresent(capabilityComponent -> {
                        PuzzlesLibMod.NETWORK.sendTo(player, this.toPacket(capabilityComponent));
                    });
                });
                if (syncStrategy == SyncStrategy.TRACKING) {
                    PlayerEvents.START_TRACKING.register((Entity trackedEntity, ServerPlayer player) -> {
                        this.getIfProvided(trackedEntity).ifPresent(capabilityComponent -> {
                            PuzzlesLibMod.NETWORK.sendTo(player, this.toPacket(capabilityComponent));
                        });
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
                Optional<C> originalCapability = this.getIfProvided(originalEntity);
                Optional<C> newCapability = this.getIfProvided(newEntity);
                if (originalCapability.isPresent() && newCapability.isPresent()) {
                    this.getCopyStrategy().copy(originalEntity, originalCapability.get(), newEntity, newCapability.get(), false);
                }
            });
        }
        return this;
    }

    default void initialize() {
        PlayerEvents.COPY.register((ServerPlayer originalPlayer, ServerPlayer newPlayer, boolean originalStillAlive) -> {
            Optional<C> originalCapability = this.getIfProvided(originalPlayer);
            Optional<C> newCapability = this.getIfProvided(newPlayer);
            if (originalCapability.isPresent() && newCapability.isPresent()) {
                this.getCopyStrategy().copy(originalPlayer, originalCapability.get(), newPlayer, newCapability.get(), originalStillAlive);
            }
        });
    }
}
