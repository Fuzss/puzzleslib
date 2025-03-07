package fuzs.puzzleslib.api.core.v1.utility;

import net.minecraft.core.HolderLookup;
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
     * @param compoundTag tag to write to
     * @param registries  the registry access
     */
    void write(CompoundTag compoundTag, HolderLookup.Provider registries);

    /**
     * Deserialize the component from a {@link CompoundTag}.
     *
     * @param compoundTag tag to read from
     * @param registries  the registry access
     */
    void read(CompoundTag compoundTag, HolderLookup.Provider registries);

    /**
     * Encode the serializable instance to a {@link CompoundTag}.
     *
     * @param registries the registry access
     * @return a tag containing the serialized instance
     */
    default CompoundTag toCompoundTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        this.write(tag, registries);
        return tag;
    }

    /**
     * Decode the serializable instance from a {@link CompoundTag}.
     *
     * @param factory    supplier for a new empty instance
     * @param registries the registry access
     * @param <T>        serializable instance type
     * @return a tag containing the serialized instance
     */
    static <T extends NbtSerializable> Function<CompoundTag, T> fromCompoundTag(Supplier<T> factory, HolderLookup.Provider registries) {
        return (CompoundTag compoundTag) -> {
            T nbtSerializable = factory.get();
            nbtSerializable.read(compoundTag, registries);
            return nbtSerializable;
        };
    }
}
