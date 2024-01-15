package fuzs.puzzleslib.api.capability.v3.data;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;

/**
 * Controls how capability data should be handled when entity data is copied.
 * <p>This happens in {@link net.minecraft.world.entity.Mob#convertTo(EntityType, boolean)} and when the player is being respawned.
 */
public enum CopyStrategy {
    /**
     * Always copy capability data when copying other entity data, independently of the cause.
     */
    ALWAYS {

        @Override
        public <T1 extends Entity, T2 extends Entity> void copy(T1 oldEntity, CapabilityComponent<T1> oldCapability, T2 newEntity, CapabilityComponent<T2> newCapability) {
            copy(oldCapability, newCapability);
        }
    },
    /**
     * Do not copy entity data, allows for manual handling if desired. Data is still copied for players returning from the End dimension.
     */
    NEVER {

        @Override
        public <T1 extends Entity, T2 extends Entity> void copy(T1 oldEntity, CapabilityComponent<T1> oldCapability, T2 newEntity, CapabilityComponent<T2> newCapability) {

        }
    },
    /**
     * Copy entity data when inventory contents of a player are copied, which is the case after dying when the <code>keepInventory</code> game rule is active.
     */
    KEEP_PLAYER_INVENTORY {

        @Override
        public <T1 extends Entity, T2 extends Entity> void copy(T1 oldEntity, CapabilityComponent<T1> oldCapability, T2 newEntity, CapabilityComponent<T2> newCapability) {
            if (newEntity.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                if (oldEntity instanceof Player && newEntity instanceof Player) {
                    copy(oldCapability, newCapability);
                }
            }
        }
    };

    /**
     * Determines whether capability data should be copied.
     *
     * @param oldEntity        source entity
     * @param oldCapability    source capability component
     * @param newEntity        target entity
     * @param newCapability    target capability component
     * @param <T1>             source entity type
     * @param <T2>             target entity type
     */
    public abstract <T1 extends Entity, T2 extends Entity> void copy(T1 oldEntity, CapabilityComponent<T1> oldCapability, T2 newEntity, CapabilityComponent<T2> newCapability);

    static void copy(CapabilityComponent<?> oldCapability, CapabilityComponent<?> newCapability) {
        newCapability.read(oldCapability.toCompoundTag());
    }
}
