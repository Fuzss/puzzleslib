package fuzs.puzzleslib.api.capability.v3.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;

/**
 * Controls how capability data should be handled when entity data is copied.
 * <p>
 * This happens in
 * {@link net.minecraft.world.entity.Mob#convertTo(EntityType, ConversionParams, EntitySpawnReason,
 * ConversionParams.AfterConversion)} and when the player is being respawned.
 */
public enum CopyStrategy {
    /**
     * Always copy capability data when copying other entity data, independently of the cause.
     */
    ALWAYS {
        @Override
        public void copy(Entity oldEntity, CapabilityComponent<?> oldCapability, Entity newEntity, CapabilityComponent<?> newCapability, boolean originalStillAlive) {
            this.copy(newEntity.registryAccess(), oldCapability, newCapability);
        }
    },
    /**
     * Do not copy entity data, allows for manual handling if desired. Data is still copied for players returning from
     * the End dimension.
     */
    NEVER {
        @Override
        public void copy(Entity oldEntity, CapabilityComponent<?> oldCapability, Entity newEntity, CapabilityComponent<?> newCapability, boolean originalStillAlive) {
            if (originalStillAlive) this.copy(newEntity.registryAccess(), oldCapability, newCapability);
        }
    },
    /**
     * Copy entity data when inventory contents of a player are copied, which is the case after dying when the
     * <code>keepInventory</code> game rule is active.
     *
     * @deprecated will no longer be supported in upcoming versions to allow migrating to Fabric Api's / NeoForge's
     *         native implementation
     */
    @Deprecated KEEP_PLAYER_INVENTORY {
        @Override
        public void copy(Entity oldEntity, CapabilityComponent<?> oldCapability, Entity newEntity, CapabilityComponent<?> newCapability, boolean originalStillAlive) {
            NEVER.copy(oldEntity, oldCapability, newEntity, newCapability, originalStillAlive);
        }
    };

    /**
     * Determines whether capability data should be copied.
     *
     * @param oldEntity          source entity
     * @param oldCapability      source capability component
     * @param newEntity          target entity
     * @param newCapability      target capability component
     * @param originalStillAlive is the entity still alive or has it died
     */
    public abstract void copy(Entity oldEntity, CapabilityComponent<?> oldCapability, Entity newEntity, CapabilityComponent<?> newCapability, boolean originalStillAlive);

    void copy(HolderLookup.Provider registries, CapabilityComponent<?> oldCapability, CapabilityComponent<?> newCapability) {
        newCapability.read(oldCapability.toCompoundTag(registries), registries);
    }
}
