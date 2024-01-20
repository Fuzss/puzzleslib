package fuzs.puzzleslib.api.core.v1.utility;

import net.minecraft.nbt.CompoundTag;

/**
 * A basic template for an object that is serializable to Minecraft's nbt format.
 */
public interface NbtSerializable {

    /**
     * Serialize the component to a {@link CompoundTag}.
     *
     * @param tag tag to write to
     */
    void write(CompoundTag tag);

    /**
     * Deserialize the component from a {@link CompoundTag}.
     *
     * @param tag tag to read from
     */
    void read(CompoundTag tag);

    /**
     * Serialize the component to a {@link CompoundTag}.
     *
     * @return the capability serialized to {@link CompoundTag}
     */
    default CompoundTag toCompoundTag() {
        CompoundTag tag = new CompoundTag();
        this.write(tag);
        return tag;
    }
}
