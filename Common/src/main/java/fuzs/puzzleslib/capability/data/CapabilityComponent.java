package fuzs.puzzleslib.capability.data;

import net.minecraft.nbt.CompoundTag;

/**
 * an extension to INBTSerializable (Forge) or Component (Fabric) for providing common read and write methods
 */
public interface CapabilityComponent {

    /**
     * @param tag tag to write to
     */
    void write(CompoundTag tag);

    /**
     * @param tag tag to read from
     */
    void read(CompoundTag tag);
}
