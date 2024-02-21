package fuzs.puzzleslib.impl.capability;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CopyStrategy;
import fuzs.puzzleslib.api.capability.v3.data.EntityCapabilityKey;
import fuzs.puzzleslib.api.capability.v3.data.SyncStrategy;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingConversionCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.AfterChangeDimensionCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerCopyEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerNetworkEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerTrackingEvents;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
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
                PlayerNetworkEvents.LOGGED_IN.register((ServerPlayer serverPlayer) -> {
                    this.getIfProvided(serverPlayer).ifPresent(capabilityComponent -> {
                        PuzzlesLibMod.NETWORK.sendMessage(PlayerSet.ofPlayer(serverPlayer),
                                this.toPacket(capabilityComponent)
                        );
                    });
                });
                AfterChangeDimensionCallback.EVENT.register((ServerPlayer serverPlayer, ServerLevel from, ServerLevel to) -> {
                    this.getIfProvided(serverPlayer).ifPresent(capabilityComponent -> {
                        PuzzlesLibMod.NETWORK.sendMessage(PlayerSet.ofPlayer(serverPlayer),
                                this.toPacket(capabilityComponent)
                        );
                    });
                });
                PlayerCopyEvents.RESPAWN.register((ServerPlayer serverPlayer, boolean originalStillAlive) -> {
                    this.getIfProvided(serverPlayer).ifPresent(capabilityComponent -> {
                        PuzzlesLibMod.NETWORK.sendMessage(PlayerSet.ofPlayer(serverPlayer),
                                this.toPacket(capabilityComponent)
                        );
                    });
                });
                if (syncStrategy == SyncStrategy.TRACKING) {
                    PlayerTrackingEvents.START.register((Entity trackedEntity, ServerPlayer serverPlayer) -> {
                        this.getIfProvided(trackedEntity).ifPresent(capabilityComponent -> {
                            PuzzlesLibMod.NETWORK.sendMessage(PlayerSet.ofPlayer(serverPlayer),
                                    this.toPacket(capabilityComponent)
                            );
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
                    this.getCopyStrategy()
                            .copy(originalEntity, originalCapability.get(), newEntity, newCapability.get(), false);
                }
            });
        }
        return this;
    }

    default void initialize() {
        PlayerCopyEvents.COPY.register((ServerPlayer originalPlayer, ServerPlayer newPlayer, boolean originalStillAlive) -> {
            Optional<C> originalCapability = this.getIfProvided(originalPlayer);
            Optional<C> newCapability = this.getIfProvided(newPlayer);
            if (originalCapability.isPresent() && newCapability.isPresent()) {
                this.getCopyStrategy()
                        .copy(originalPlayer,
                                originalCapability.get(),
                                newPlayer,
                                newCapability.get(),
                                originalStillAlive
                        );
            }
        });
    }
}
