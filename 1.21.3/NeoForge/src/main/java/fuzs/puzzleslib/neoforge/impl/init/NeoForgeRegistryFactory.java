package fuzs.puzzleslib.neoforge.impl.init;

import fuzs.puzzleslib.api.init.v3.registry.RegistryFactory;
import fuzs.puzzleslib.neoforge.mixin.accessor.NewRegistryEventNeoForgeAccessor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.Objects;

public final class NeoForgeRegistryFactory implements RegistryFactory {

    @Override
    public <T> Registry<T> create(ResourceKey<Registry<T>> registryKey, boolean synced) {
        Objects.requireNonNull(registryKey, "registry key is null");
        return this.register(new RegistryBuilder<>(registryKey).sync(synced).create());
    }

    @Override
    public <T> Registry<T> create(ResourceKey<Registry<T>> registryKey, ResourceLocation defaultKey, boolean synced) {
        Objects.requireNonNull(registryKey, "registry key is null");
        Objects.requireNonNull(defaultKey, "default key is null");
        return this.register(new RegistryBuilder<>(registryKey).defaultKey(defaultKey).sync(synced).create());
    }

    @Override
    public <T> Registry<T> register(Registry<T> registry) {
        // the event is very odd and might as well just be a static method as it does not require any context
        // so just invoke it manually to utilize the methods it provides
        synchronized (BuiltInRegistries.REGISTRY) {
            NewRegistryEvent evt = NewRegistryEventNeoForgeAccessor.puzzleslib$callInit();
            evt.register(registry);
            ((NewRegistryEventNeoForgeAccessor) evt).puzzleslib$callFill();
        }
        return registry;
    }
}
