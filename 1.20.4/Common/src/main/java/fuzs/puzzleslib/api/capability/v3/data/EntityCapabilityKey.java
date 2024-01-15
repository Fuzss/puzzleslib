package fuzs.puzzleslib.api.capability.v3.data;

import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.capability.ClientboundSyncCapabilityMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface EntityCapabilityKey<T extends Entity, C extends CapabilityComponent<T>> extends CapabilityKey<T, C> {

    void setSyncStrategy(SyncStrategy syncStrategy);

    SyncStrategy getSyncStrategy();

    @Override
    default void setChanged(C capabilityComponent) {
        if (capabilityComponent.getHolder() instanceof ServerPlayer player) {
            syncCapabilityToRemote(this, capabilityComponent, player);
        }
    }

    /**
     * sync capability to player as defined by <code>syncStrategy</code>>
     *
     * @param receiver          player to sync to
     * @param syncStrategy      sync strategy to use
     * @param capabilityComponent        capability to sync
     * @param id                registered capability id
     * @param force             this is a forced sync (e.g. entity creation), so don't throw/log errors
     * @param <C>               capability type
     */
    static <T extends Entity, C extends CapabilityComponent<T>> void syncCapabilityToRemote(EntityCapabilityKey<T, C> capabilityKey, C capabilityComponent, ServerPlayer receiver) {
        if (capabilityKey.getSyncStrategy() != SyncStrategies.MANUAL) {
            capabilityKey.getSyncStrategy().sendTo(new ClientboundSyncCapabilityMessage(capabilityKey.identifier(), capabilityComponent.getHolder().getId(), capabilityComponent.toCompoundTag()), receiver);
        } else {
            PuzzlesLib.LOGGER.warn("Attempting to sync capability {} that is set to manual syncing", capabilityKey.identifier());
        }
    }
}
