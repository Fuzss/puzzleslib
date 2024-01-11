package fuzs.puzzleslib.api.capability.v2.data;

import net.minecraft.nbt.CompoundTag;

/**
 * same functionality as INBTSerializable (Forge) or ComponentV3 (Fabric) for providing common read and write methods
 */
public interface CapabilityComponent {

    /**
     * @param tag tag to write to
     */
    default void write(CompoundTag tag) {

    }

    /**
     * @param tag tag to read from
     */
    default void read(CompoundTag tag) {
        
    }

    /**
     * @return this capability serialized to {@link CompoundTag}
     */
    default CompoundTag toCompoundTag() {
        CompoundTag tag = new CompoundTag();
        this.write(tag);
        return tag;
    }
}
