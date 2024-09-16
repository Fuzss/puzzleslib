package fuzs.puzzleslib.api.capability.v3.data;

import net.minecraft.world.entity.EntityType;

/**
 * Controls how capability data should be handled when entity data is copied.
 * <p>
 * This happens in {@link net.minecraft.world.entity.Mob#convertTo(EntityType, boolean)} and when the player is being
 * respawned.
 */
public enum CopyStrategy {
    /**
     * Always copy capability data when copying other entity data, independently of the cause.
     */
    ALWAYS,
    /**
     * Do not copy entity data, allows for manual handling if desired. Data is still copied for players returning from
     * the End dimension.
     */
    NEVER,
    /**
     * Copy entity data when inventory contents of a player are copied, which is the case after dying when the
     * <code>keepInventory</code> game rule is active.
     *
     * @deprecated use {@link #ALWAYS} instead
     */
    @Deprecated
    KEEP_PLAYER_INVENTORY;

    public boolean copyOnDeath() {
        return this != NEVER;
    }
}
