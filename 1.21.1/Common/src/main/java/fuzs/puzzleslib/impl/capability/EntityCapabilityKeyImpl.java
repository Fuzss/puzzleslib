package fuzs.puzzleslib.impl.capability;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.EntityCapabilityKey;
import fuzs.puzzleslib.api.capability.v3.data.SyncStrategy;
import fuzs.puzzleslib.api.event.v1.entity.player.AfterChangeDimensionCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerCopyEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerNetworkEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerTrackingEvents;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import fuzs.puzzleslib.impl.core.EventHandlerProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public interface EntityCapabilityKeyImpl<T extends Entity, C extends CapabilityComponent<T>> extends EntityCapabilityKey.Mutable<T, C>, EventHandlerProvider {

    @Override
    default void registerEventHandlers() {
        if (this.getSyncStrategy() != SyncStrategy.MANUAL) {
            PlayerNetworkEvents.LOGGED_IN.register((ServerPlayer serverPlayer) -> {
                this.getIfProvided(serverPlayer).ifPresent(capabilityComponent -> {
                    capabilityComponent.setChanged(PlayerSet.ofPlayer(serverPlayer));
                });
            });
            AfterChangeDimensionCallback.EVENT.register((ServerPlayer serverPlayer, ServerLevel from, ServerLevel to) -> {
                this.getIfProvided(serverPlayer).ifPresent(capabilityComponent -> {
                    capabilityComponent.setChanged(PlayerSet.ofPlayer(serverPlayer));
                });
            });
            PlayerCopyEvents.RESPAWN.register((ServerPlayer serverPlayer, boolean originalStillAlive) -> {
                this.getIfProvided(serverPlayer).ifPresent(capabilityComponent -> {
                    capabilityComponent.setChanged(PlayerSet.ofPlayer(serverPlayer));
                });
            });
        }
        if (this.getSyncStrategy() == SyncStrategy.TRACKING) {
            PlayerTrackingEvents.START.register((Entity trackedEntity, ServerPlayer serverPlayer) -> {
                this.getIfProvided(trackedEntity).ifPresent(capabilityComponent -> {
                    capabilityComponent.setChanged(PlayerSet.ofPlayer(serverPlayer));
                });
            });
        }
    }
}
