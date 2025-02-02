package fuzs.puzzleslib.fabric.impl.init;

import fuzs.puzzleslib.api.init.v3.registry.RegistryFactory;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class FabricRegistryFactory implements RegistryFactory {

    @Override
    public <T> Registry<T> create(ResourceKey<Registry<T>> registryKey, boolean synced) {
        Objects.requireNonNull(registryKey, "registry key is null");
        FabricRegistryBuilder<T, ? extends Registry<T>> builder = FabricRegistryBuilder.createSimple(registryKey);
        if (synced) builder.attribute(RegistryAttribute.SYNCED);
        return builder.buildAndRegister();
    }

    @Override
    public <T> Registry<T> create(ResourceKey<Registry<T>> registryKey, ResourceLocation defaultKey, boolean synced) {
        Objects.requireNonNull(registryKey, "registry key is null");
        Objects.requireNonNull(defaultKey, "default key is null");
        FabricRegistryBuilder<T, ? extends Registry<T>> builder = FabricRegistryBuilder.createDefaulted(registryKey,
                defaultKey
        );
        if (synced) builder.attribute(RegistryAttribute.SYNCED);
        return builder.buildAndRegister();
    }

    @Override
    public <T> Registry<T> register(Registry<T> registry) {
        return FabricRegistryBuilder.from((WritableRegistry<T>) registry).buildAndRegister();
    }
}
