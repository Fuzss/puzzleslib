package fuzs.puzzleslib.api.core.v1.utility;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A codec adapter for {@link NbtSerializable}. Uses {@link TagParser#AS_CODEC} internally.
 * <p>
 * A very crude implementation, handle with caution.
 *
 * @param factory the nbt serializable factory
 * @param <T>     the nbt serializable type
 */
@ApiStatus.Experimental
public record NbtSerializableCodec<T extends NbtSerializable>(Supplier<T> factory) implements Codec<T> {

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        if (ops instanceof RegistryOps<T1> registryOps) {
            return TagParser.AS_CODEC.decode(ops, input).map((Pair<CompoundTag, T1> pair) -> {
                return pair.mapFirst((CompoundTag compoundTag) -> {
                    T nbtSerializable = this.factory.get();
                    nbtSerializable.read(compoundTag, new RegistryOpsProvider(registryOps));
                    return nbtSerializable;
                });
            });
        } else {
            return DataResult.error(() -> "Can't decode element " + input + " without registry");
        }
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        if (ops instanceof RegistryOps<T1> registryOps) {
            return TagParser.AS_CODEC.encode(input.toCompoundTag(new RegistryOpsProvider(registryOps)), ops, prefix);
        } else {
            return DataResult.error(() -> "Can't encode element " + input + " without registry");
        }
    }

    private record RegistryOpsProvider(RegistryOps<?> registryOps) implements HolderLookup.Provider {

        @Override
        public Stream<ResourceKey<? extends Registry<?>>> listRegistries() {
            return Stream.empty();
        }

        @Override
        public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> registryKey) {
            return this.registryOps.getter(registryKey)
                    .filter(holderGetter -> holderGetter instanceof HolderLookup.RegistryLookup<T>)
                    .map(holderGetter -> (HolderLookup.RegistryLookup<T>) holderGetter);
        }
    }
}
