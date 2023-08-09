package fuzs.puzzleslib.capability.data;

import net.minecraft.nbt.CompoundTag;

/**
 * modes determining how capability data should be handled when the player entity is recreated, which will usually happen when returning from the end dimension and when respawning
 * this is basically the same class as in {@see <a href="https://github.com/OnyxStudios/Cardinal-Components-API">https://github.com/OnyxStudios/Cardinal-Components-API</a>} for the Fabric mod loader
 */
public abstract class PlayerRespawnStrategy {
    /**
     * always copy data when recreating player
     */
    public static final PlayerRespawnStrategy ALWAYS_COPY = new PlayerRespawnStrategy() {
        @Override
        public void copy(CapabilityComponent oldCapability, CapabilityComponent newCapability, boolean returningFromEnd, boolean keepInventory) {
            this.actuallyCopy(oldCapability, newCapability);
        }
    };
    /**
     * copy data when inventory contents are copied
     */
    public static final PlayerRespawnStrategy INVENTORY = new PlayerRespawnStrategy() {
        @Override
        public void copy(CapabilityComponent oldCapability, CapabilityComponent newCapability, boolean returningFromEnd, boolean keepInventory) {
            if (returningFromEnd || keepInventory) {
                this.actuallyCopy(oldCapability, newCapability);
            }
        }
    };
    /**
     * copy data when returning from end, but never after dying
     */
    public static final PlayerRespawnStrategy LOSSLESS = new PlayerRespawnStrategy() {
        @Override
        public void copy(CapabilityComponent oldCapability, CapabilityComponent newCapability, boolean returningFromEnd, boolean keepInventory) {
            if (returningFromEnd) {
                this.actuallyCopy(oldCapability, newCapability);
            }
        }
    };
    /**
     * never copy data
     */
    public static final PlayerRespawnStrategy NEVER = new PlayerRespawnStrategy() {
        @Override
        public void copy(CapabilityComponent oldCapability, CapabilityComponent newCapability, boolean returningFromEnd, boolean keepInventory) {

        }
    };

    /**
     * internal method for copying capability data from source to target
     * @param oldCapability source capability component
     * @param newCapability target capability component
     */
    protected void actuallyCopy(CapabilityComponent oldCapability, CapabilityComponent newCapability) {
        CompoundTag tag = new CompoundTag();
        oldCapability.write(tag);
        newCapability.read(tag);
    }

    /**
     * determines whether capability data should be copied, if so calls {@link #actuallyCopy}
     * @param oldCapability source capability component
     * @param newCapability target capability component
     * @param returningFromEnd was the player not actually dead, but is instead returning from the end dimension
     * @param keepInventory is the <code>keepInventory</code> game rule enabled
     */
    public abstract void copy(CapabilityComponent oldCapability, CapabilityComponent newCapability, boolean returningFromEnd, boolean keepInventory);
}
