package fuzs.puzzleslib.impl.core;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import fuzs.puzzleslib.api.core.v1.utility.NbtSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.RegistryOps;

import java.util.function.Supplier;

/**
 * A codec adapter for {@link NbtSerializable}. Uses {@link TagParser#AS_CODEC} internally.
 *
 * @param factory the nbt serializable factory
 * @param <T>     the nbt serializable type
 */
public record NbtSerializableCodec<T extends NbtSerializable>(Supplier<T> factory) implements Codec<T> {

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        if (ops instanceof RegistryOps<T1> registryOps &&
                registryOps.lookupProvider instanceof RegistryOps.HolderLookupAdapter adapter) {
            return TagParser.AS_CODEC.decode(ops, input).map((Pair<CompoundTag, T1> pair) -> {
                return pair.mapFirst((CompoundTag compoundTag) -> {
                    T nbtSerializable = this.factory.get();
                    nbtSerializable.read(compoundTag, adapter.lookupProvider);
                    return nbtSerializable;
                });
            });
        } else {
            return DataResult.error(() -> "Can't decode element " + input + " without registry");
        }
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        if (ops instanceof RegistryOps<T1> registryOps &&
                registryOps.lookupProvider instanceof RegistryOps.HolderLookupAdapter adapter) {
            return TagParser.AS_CODEC.encode(input.toCompoundTag(adapter.lookupProvider), ops, prefix);
        } else {
            return DataResult.error(() -> "Can't encode element " + input + " without registry");
        }
    }
}
