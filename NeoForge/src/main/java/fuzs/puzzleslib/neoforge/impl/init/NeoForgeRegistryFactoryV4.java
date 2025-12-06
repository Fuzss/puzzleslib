package fuzs.puzzleslib.neoforge.impl.init;

import fuzs.puzzleslib.api.init.v4.registry.RegistryFactory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class NeoForgeRegistryFactoryV4 implements RegistryFactory {

    @Override
    public <T> Registry<T> create(ResourceKey<Registry<T>> registryKey, @Nullable ResourceLocation defaultKey) {
        Objects.requireNonNull(registryKey, "registry key is null");
        RegistryBuilder<T> builder = new RegistryBuilder<>(registryKey);
        if (defaultKey != null) builder.defaultKey(defaultKey);
        return builder.create();
    }

    @Override
    public <T> Registry<T> createSynced(ResourceKey<Registry<T>> registryKey, @Nullable ResourceLocation defaultKey) {
        Objects.requireNonNull(registryKey, "registry key is null");
        RegistryBuilder<T> builder = new RegistryBuilder<>(registryKey).sync(true);
        if (defaultKey != null) builder.defaultKey(defaultKey);
        return builder.create();
    }
}
