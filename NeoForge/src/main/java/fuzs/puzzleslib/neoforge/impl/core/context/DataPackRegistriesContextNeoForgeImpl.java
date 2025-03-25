package fuzs.puzzleslib.neoforge.impl.core.context;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.core.v1.context.DataPackRegistriesContext;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.Objects;

public record DataPackRegistriesContextNeoForgeImpl(DataPackRegistryEvent.NewRegistry evt) implements DataPackRegistriesContext {

    @Override
    public <T> void registerRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
        Objects.requireNonNull(registryKey, "registry key is null");
        Objects.requireNonNull(codec, "codec is null");
        this.evt.dataPackRegistry(registryKey, codec);
    }

    @Override
    public <T> void registerSyncedRegistry(ResourceKey<Registry<T>> registryKey, Codec<T> codec, Codec<T> networkCodec) {
        Objects.requireNonNull(registryKey, "registry key is null");
        Objects.requireNonNull(codec, "codec is null");
        Objects.requireNonNull(networkCodec, "network codec is null");
        this.evt.dataPackRegistry(registryKey, codec, networkCodec);
    }
}
