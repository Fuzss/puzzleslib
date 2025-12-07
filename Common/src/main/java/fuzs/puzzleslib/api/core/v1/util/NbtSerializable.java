package fuzs.puzzleslib.api.core.v1.util;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.impl.core.NbtSerializableCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Supplier;

/**
 * A basic template for an object that is serializable to Minecraft's nbt format.
 */
public interface NbtSerializable {

    /**
     * A codec adapter for {@link NbtSerializable}.
     *
     * @param factory the nbt serializable factory
     * @param <T>     the nbt serializable type
     */
    static <T extends NbtSerializable> Codec<T> codec(Supplier<T> factory) {
        return new NbtSerializableCodec<>(factory);
    }

    /**
     * Serialise the component to a {@link CompoundTag}.
     *
     * @param compoundTag tag to write to
     * @param registries  the registry access
     */
    void write(CompoundTag compoundTag, HolderLookup.Provider registries);

    /**
     * Deserialise the component from a {@link CompoundTag}.
     *
     * @param compoundTag tag to read from
     * @param registries  the registry access
     */
    void read(CompoundTag compoundTag, HolderLookup.Provider registries);

    /**
     * Encode the serialisable instance to a {@link CompoundTag}.
     *
     * @param registries the registry access
     * @return a tag containing the serialized instance
     */
    default CompoundTag toCompoundTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        this.write(tag, registries);
        return tag;
    }
}
