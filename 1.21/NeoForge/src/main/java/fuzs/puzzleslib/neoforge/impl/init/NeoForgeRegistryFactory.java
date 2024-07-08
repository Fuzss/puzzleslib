package fuzs.puzzleslib.neoforge.impl.init;

import fuzs.puzzleslib.api.init.v3.registry.RegistryFactory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;

public final class NeoForgeRegistryFactory implements RegistryFactory {

    @Override
    public <T> Registry<T> create(ResourceKey<Registry<T>> registryKey, boolean synced) {
        Objects.requireNonNull(registryKey, "registry key is null");
        return register(new RegistryBuilder<>(registryKey).sync(synced).create());
    }

    @Override
    public <T> Registry<T> create(ResourceKey<Registry<T>> registryKey, ResourceLocation defaultKey, boolean synced) {
        Objects.requireNonNull(registryKey, "registry key is null");
        Objects.requireNonNull(defaultKey, "default key is null");
        return register(new RegistryBuilder<>(registryKey).defaultKey(defaultKey).sync(synced).create());
    }

    @Override
    public <T> Registry<T> register(Registry<T> registry) {
        // the event is very odd and might as well just be a static method as it does not require any context
        // so just invoke it manually to utilize the methods it provides
        try {
            Constructor<NewRegistryEvent> constructor = NewRegistryEvent.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            NewRegistryEvent evt = (NewRegistryEvent) MethodHandles.lookup().unreflectConstructor(constructor).invoke();
            evt.register(registry);
            Method method = NewRegistryEvent.class.getDeclaredMethod("fill");
            method.setAccessible(true);
            MethodHandles.lookup().unreflect(method).invoke(evt);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
        return registry;
    }
}
