package fuzs.puzzleslib.capability.data;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/**
 * implementation of {@link CapabilityKey} for players on Fabric
 *
 * <p>Cardinal Components can handle all the syncing for us, but the implementation
 *
 * @param <C> capability type
 */
public class FabricPlayerCapabilityKey<C extends CapabilityComponent> extends FabricCapabilityKey<C> implements PlayerCapabilityKey<C> {
    /**
     * strategy for syncing this capability data to remote
     */
    private SyncStrategy syncStrategy = SyncStrategy.MANUAL;

    /**
     * @param capability     the wrapped {@link ComponentKey}
     * @param componentClass capability type class for setting type parameter
     */
    public FabricPlayerCapabilityKey(ComponentKey<ComponentHolder> capability, Class<C> componentClass) {
        super(capability, componentClass);
    }

    /**
     * @param syncStrategy      strategy for syncing this capability data to remote
     * @return                  builder
     */
    public FabricPlayerCapabilityKey<C> setSyncStrategy(SyncStrategy syncStrategy) {
        if (this.syncStrategy != SyncStrategy.MANUAL) throw new IllegalStateException("Attempting to set new sync behaviour when it has already been set");
        this.syncStrategy = syncStrategy;
        ServerEntityEvents.ENTITY_LOAD.register((Entity entity, ServerLevel world) -> {
            this.maybeGet(entity).ifPresent(capability -> {
                PlayerCapabilityKey.syncCapabilityToRemote(entity, (ServerPlayer) entity, this.syncStrategy, capability, this.getId(), true);
            });
        });
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((ServerPlayer player, ServerLevel origin, ServerLevel destination) -> {
            this.maybeGet(player).ifPresent(capability -> {
                PlayerCapabilityKey.syncCapabilityToRemote(player, player, this.syncStrategy, capability, this.getId(), true);
            });
        });
        if (syncStrategy == SyncStrategy.SELF_AND_TRACKING) {
            EntityTrackingEvents.START_TRACKING.register((Entity trackedEntity, ServerPlayer player) -> {
                this.maybeGet(trackedEntity).ifPresent(capability -> {
                    // we only want to sync to the client that just started tracking, so use SyncStrategy#SELF
                    PlayerCapabilityKey.syncCapabilityToRemote(trackedEntity, player, SyncStrategy.SELF, capability, this.getId(), true);
                });
            });
        }
        return this;
    }

    @Override
    public void syncToRemote(ServerPlayer player) {
        PlayerCapabilityKey.syncCapabilityToRemote(player, player, this.syncStrategy, this.orThrow(player), this.getId(), false);
    }
}
