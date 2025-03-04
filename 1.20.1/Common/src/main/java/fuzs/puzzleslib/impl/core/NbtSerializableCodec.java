package fuzs.puzzleslib.impl.core;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import fuzs.puzzleslib.api.core.v1.utility.NbtSerializable;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.mixin.accessor.RegistryOpsAccessor;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.RegistryOps;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.function.Supplier;

/**
 * A codec adapter for {@link NbtSerializable}.
 *
 * @param factory the nbt serializable factory
 * @param <T>     the nbt serializable type
 */
public record NbtSerializableCodec<T extends NbtSerializable>(Supplier<T> factory) implements Codec<T> {
    /**
     * Copied from Minecraft 1.21.4's {@code TagParser#AS_CODEC}.
     */
    public static final Codec<CompoundTag> AS_CODEC = Codec.STRING.comapFlatMap(string -> {
        try {
            return DataResult.success(TagParser.parseTag(string), Lifecycle.stable());
        } catch (CommandSyntaxException var2) {
            return DataResult.error(var2::getMessage);
        }
    }, CompoundTag::toString);

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        HolderLookup.Provider registries = getRegistries(ops);
        if (registries != null) {
            return AS_CODEC.decode(ops, input).map((Pair<CompoundTag, T1> pair) -> {
                return pair.mapFirst((CompoundTag compoundTag) -> {
                    T nbtSerializable = this.factory.get();
                    nbtSerializable.read(compoundTag, registries);
                    return nbtSerializable;
                });
            });
        } else {
            return DataResult.error(() -> "Can't decode element " + input + " without registry");
        }
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        HolderLookup.Provider registries = getRegistries(ops);
        if (registries != null) {
            return AS_CODEC.encode(input.toCompoundTag(registries), ops, prefix);
        } else {
            return DataResult.error(() -> "Can't encode element " + input + " without registry");
        }
    }

    @Nullable
    static HolderLookup.Provider getRegistries(DynamicOps<?> dynamicOps) {
        return dynamicOps instanceof RegistryOps<?> registryOps ?
                getRegistries(((RegistryOpsAccessor) registryOps).puzzleslib$getLookupProvider()) : null;
    }

    @Nullable
    static HolderLookup.Provider getRegistries(RegistryOps.RegistryInfoLookup registryInfoLookup) {
        if (registryInfoLookup != null) {
            try {
                for (Field field : registryInfoLookup.getClass().getDeclaredFields()) {
                    if (field.getType().isAssignableFrom(RegistryOps.RegistryInfoLookup.class)) {
                        field.setAccessible(true);
                        HolderLookup.Provider registries = getRegistries((RegistryOps.RegistryInfoLookup) MethodHandles.lookup()
                                .unreflectGetter(field)
                                .invoke(registryInfoLookup));
                        if (registries != null) {
                            return registries;
                        }
                    } else if (field.getType().isAssignableFrom(HolderLookup.Provider.class)) {
                        field.setAccessible(true);
                        return (HolderLookup.Provider) MethodHandles.lookup()
                                .unreflectGetter(field)
                                .invoke(registryInfoLookup);
                    }
                }
            } catch (Throwable throwable) {
                PuzzlesLibMod.LOGGER.warn("Failed extracting registries from RegistryInfoLookup object", throwable);
            }
        }
        return null;
    }
}
