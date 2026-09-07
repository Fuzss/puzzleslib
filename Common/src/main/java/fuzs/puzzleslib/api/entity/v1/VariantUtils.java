package fuzs.puzzleslib.api.entity.v1;

import fuzs.puzzleslib.api.util.v1.CompoundTagHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.Optional;

/**
 * Copied from Minecraft 26.2.
 */
public final class VariantUtils {
    public static final String TAG_VARIANT = "variant";

    private VariantUtils() {
        // NO-OP
    }

    public static <T> Holder<T> getDefaultOrAny(RegistryAccess registryAccess, ResourceKey<T> key) {
        Registry<T> registry = registryAccess.registryOrThrow(key.registryKey());
        return registry.getHolder(key).or(registry::getAny).orElseThrow();
    }

    public static <T> Holder<T> getAny(RegistryAccess registryAccess, ResourceKey<? extends Registry<T>> registryKey) {
        return registryAccess.registryOrThrow(registryKey).getAny().orElseThrow();
    }

    public static <T> void writeVariant(CompoundTag output, Holder<T> variant) {
        variant.unwrapKey()
                .ifPresent((ResourceKey<T> resourceKey) -> CompoundTagHelper.store(output,
                        TAG_VARIANT,
                        ResourceLocation.CODEC,
                        resourceKey.location()));
    }

    public static <T> Optional<Holder<T>> readVariant(CompoundTag input, ResourceKey<? extends Registry<T>> registryKey, HolderLookup.Provider lookup) {
        return CompoundTagHelper.read(input, TAG_VARIANT, ResourceLocation.CODEC)
                .map((ResourceLocation id) -> ResourceKey.create(registryKey, id))
                .flatMap((ResourceKey<T> key) -> lookup.lookupOrThrow(key.registryKey()).get(key));
    }

    public static <T> Optional<Holder.Reference<T>> selectVariantToSpawn(ServerLevelAccessor serverLevel, ResourceKey<Registry<T>> registryKey) {
        return serverLevel.registryAccess().registryOrThrow(registryKey).getRandom(serverLevel.getRandom());
    }

    public static <T> Optional<Holder<T>> selectVariantToSpawn(ServerLevelAccessor serverLevel, ResourceKey<Registry<T>> registryKey, TagKey<T> tagKey) {
        return serverLevel.registryAccess()
                .registryOrThrow(registryKey)
                .getRandomElementOf(tagKey, serverLevel.getRandom());
    }
}
