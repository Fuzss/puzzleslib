package fuzs.puzzleslib.api.capability.v2.data;

/**
 * Modes determining how capability data should be handled when the player entity is recreated, which will usually happen when returning from the end dimension and when respawning.
 * <p>This is basically the same class as in {@see <a href="https://github.com/OnyxStudios/Cardinal-Components-API">https://github.com/OnyxStudios/Cardinal-Components-API</a>} for the Fabric mod loader.
 */
public enum CopyStrategy {
    /**
     * Always copy data when recreating player, independently of the cause of recreation.
     */
    ALWAYS {

        @Override
        public void copy(CapabilityComponent oldCapability, CapabilityComponent newCapability, boolean returningFromEnd, boolean keepInventory) {
            actuallyCopy(oldCapability, newCapability);
        }
    },
    /**
     * Copy data when inventory contents are copied, which is the case when successfully returning from the End dimension,
     * but also after dying when the <code>keepInventory</code> game rule is active.
     */
    KEEP_PLAYER_INVENTORY {

        @Override
        public void copy(CapabilityComponent oldCapability, CapabilityComponent newCapability, boolean returningFromEnd, boolean keepInventory) {
            if (returningFromEnd || keepInventory) {
                actuallyCopy(oldCapability, newCapability);
            }
        }
    },
    /**
     * Do not copy data, allows for manual handling if desired. Data is still copied for players returning from the End dimension.
     */
    NEVER {

        @Override
        public void copy(CapabilityComponent oldCapability, CapabilityComponent newCapability, boolean returningFromEnd, boolean keepInventory) {

        }
    };

    /**
     * Determines whether capability data should be copied, if so calls {@link #actuallyCopy}.
     *
     * @param oldCapability    source capability component
     * @param newCapability    target capability component
     * @param returningFromEnd was the player not actually dead, but is instead returning from the end dimension
     * @param keepInventory    is the <code>keepInventory</code> game rule enabled
     */
    public abstract void copy(CapabilityComponent oldCapability, CapabilityComponent newCapability, boolean returningFromEnd, boolean keepInventory);

    private static void actuallyCopy(CapabilityComponent oldCapability, CapabilityComponent newCapability) {
        newCapability.read(oldCapability.toCompoundTag());
    }
}
