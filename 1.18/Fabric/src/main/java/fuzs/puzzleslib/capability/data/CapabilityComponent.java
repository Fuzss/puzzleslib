package fuzs.puzzleslib.capability.data;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.CompoundTag;

/**
 * an extension to INBTSerializable (Forge) or Component (Fabric) for providing common read and write methods
 */
public interface CapabilityComponent extends ComponentV3 {
    /**
     * @param tag tag to write to
     */
    void write(CompoundTag tag);

    /**
     * @param tag tag to read from
     */
    void read(CompoundTag tag);

    @Override
    default void writeToNbt(CompoundTag tag) {
        this.write(tag);
    }

    @Override
    default void readFromNbt(CompoundTag tag) {
        this.read(tag);
    }
}
