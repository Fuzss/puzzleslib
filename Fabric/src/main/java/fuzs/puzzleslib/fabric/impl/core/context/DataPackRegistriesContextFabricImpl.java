package fuzs.puzzleslib.fabric.impl.core.context;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.core.v1.context.DataPackRegistriesContext;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.Objects;

public final class DataPackRegistriesContextFabricImpl implements DataPackRegistriesContext {

    @Override
    public <T> void registerRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
        Objects.requireNonNull(registryKey, "registry key is null");
        Objects.requireNonNull(codec, "codec is null");
        DynamicRegistries.register(registryKey, codec);
    }

    @Override
    public <T> void registerSyncedRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, Codec<T> networkCodec) {
        Objects.requireNonNull(registryKey, "registry key is null");
        Objects.requireNonNull(codec, "codec is null");
        Objects.requireNonNull(networkCodec, "network codec is null");
        DynamicRegistries.registerSynced(registryKey, codec, networkCodec);
    }
}
