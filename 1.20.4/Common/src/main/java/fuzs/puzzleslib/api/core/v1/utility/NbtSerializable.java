package fuzs.puzzleslib.api.core.v1.utility;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Function;
import java.util.function.Supplier;

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
     * Encode the serializable instance to a {@link CompoundTag}.
     *
     * @return a tag containing the serialized instance
     */
    default CompoundTag toCompoundTag() {
        CompoundTag tag = new CompoundTag();
        this.write(tag);
        return tag;
    }

    /**
     * Decode the serializable instance from a {@link CompoundTag}.
     *
     * @param factory supplier for a new empty instance
     * @param <T>     serializable instance type
     * @return a tag containing the serialized instance
     */
    static <T extends NbtSerializable> Function<CompoundTag, T> fromCompoundTag(Supplier<T> factory) {
        return (CompoundTag tag) -> {
            T nbtSerializable = factory.get();
            nbtSerializable.read(tag);
            return nbtSerializable;
        };
    }
}
