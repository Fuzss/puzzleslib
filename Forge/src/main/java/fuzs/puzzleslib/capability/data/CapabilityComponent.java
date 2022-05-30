package fuzs.puzzleslib.capability.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * an extension to INBTSerializable (Forge) or Component (Fabric) for providing common read and write methods
 */
public interface CapabilityComponent extends INBTSerializable<CompoundTag> {
    /**
     * @param tag tag to write to
     */
    void write(CompoundTag tag);

    /**
     * @param tag tag to read from
     */
    void read(CompoundTag tag);

    @Override
    default CompoundTag serializeNBT() {
        final CompoundTag tag = new CompoundTag();
        this.write(tag);
        return tag;
    }

    @Override
    default void deserializeNBT(CompoundTag tag) {
        this.read(tag);
    }
}
