package fuzs.puzzleslib.api.capability.v3.data;

import fuzs.puzzleslib.impl.capability.ClientboundEntityCapabilityMessage;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

/**
 * Convenient {@link CapabilityKey} implementation for {@link Entity}.
 *
 * @param <T> entity type
 * @param <C> capability component type
 */
@ApiStatus.NonExtendable
public interface EntityCapabilityKey<T extends Entity, C extends CapabilityComponent<T>> extends CapabilityKey<T, C> {

    /**
     * Set a behaviour for automatically syncing capability components to tracking remotes
     *
     * @return the sync strategy
     */
    SyncStrategy getSyncStrategy();

    /**
     * Set how capability data should be handled when entity data is copied.
     *
     * @return the copy strategy
     */
    CopyStrategy getCopyStrategy();

    @Override
    default void setChanged(C capabilityComponent) {
        this.getSyncStrategy().send(capabilityComponent.getHolder(), this.toPacket(capabilityComponent));
    }

    @Override
    default ClientboundEntityCapabilityMessage toPacket(C capabilityComponent) {
        return new ClientboundEntityCapabilityMessage(this.identifier(), capabilityComponent.getHolder().getId(), capabilityComponent.toCompoundTag());
    }

    /**
     * A builder-like subclass used during registration for setting additional properties.
     *
     * @param <T> entity type
     * @param <C> capability component type
     */
    interface Mutable<T extends Entity, C extends CapabilityComponent<T>> extends EntityCapabilityKey<T, C> {

        /**
         * Set a sync strategy for this capability component.
         * <p>The default value is {@link SyncStrategy#MANUAL}.
         * <p>Can only be called once with something other than the default value.
         *
         * @param syncStrategy the new sync strategy
         * @return this capability key instance
         */
        Mutable<T, C> setSyncStrategy(SyncStrategy syncStrategy);

        /**
         * Set a copy strategy for this capability component.
         * <p>The default value is {@link CopyStrategy#NEVER}.
         * <p>Can only be called once with something other than the default value.
         *
         * @param copyStrategy the new copy strategy
         * @return this capability key instance
         */
        Mutable<T, C> setCopyStrategy(CopyStrategy copyStrategy);
    }
}
