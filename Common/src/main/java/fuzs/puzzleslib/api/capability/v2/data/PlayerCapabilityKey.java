package fuzs.puzzleslib.api.capability.v2.data;

import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.capability.ClientboundSyncCapabilityMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/**
 * a special capability key implementation with additional methods for players
 * some of these should probably be available for not just players in the future
 *
 * @param <C> capability type
 */
public interface PlayerCapabilityKey<C extends CapabilityComponent> extends CapabilityKey<C> {

    /**
     * get this capability from <code>player</code> and sync with remote(s) as defined by {@link SyncStrategy}
     * the player must have this capability or an exception will be raised
     *
     * @param player    target for retrieving capability
     */
    void syncToRemote(ServerPlayer player);

    /**
     * sync capability to player as defined by <code>syncStrategy</code>>
     *
     * @param holder            holder for this capability
     * @param receiver          player to sync to
     * @param syncStrategy      sync strategy to use
     * @param capability        capability to sync
     * @param id                registered capability id
     * @param force             this is a forced sync (e.g. entity creation), so don't throw/log errors
     * @param <C>               capability type
     */
    static <C extends CapabilityComponent> void syncCapabilityToRemote(Entity holder, ServerPlayer receiver, SyncStrategy<?> syncStrategy, C capability, ResourceLocation id, boolean force) {
        if (syncStrategy != SyncStrategy.MANUAL) {
            if (!(capability instanceof SyncedCapabilityComponent syncedCapability)) {
                if (!force) {
                    throw new IllegalStateException("Unable to sync capability component that is not of type %s".formatted(SyncedCapabilityComponent.class));
                } else {
                    return;
                }
            }
            if (force || syncedCapability.isDirty()) {
                syncStrategy.accept(new ClientboundSyncCapabilityMessage(id, holder, capability.toCompoundTag()), receiver);
                // always mark clean after syncing
                syncedCapability.markClean();
            }
        } else if (!force) {
            PuzzlesLib.LOGGER.warn("Attempting to sync capability {} that is set to manual syncing", id);
        }
    }
}
