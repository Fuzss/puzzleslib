package fuzs.puzzleslib.fabric.impl.init;

import fuzs.puzzleslib.api.init.v3.registry.RegistryFactory;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class FabricRegistryFactory implements RegistryFactory {

    @Override
    public <T> Registry<T> create(ResourceKey<Registry<T>> registryKey, @Nullable Identifier defaultKey) {
        Objects.requireNonNull(registryKey, "registry key is null");
        FabricRegistryBuilder<T, ? extends Registry<T>> builder;
        if (defaultKey != null) {
            builder = FabricRegistryBuilder.createDefaulted(registryKey, defaultKey);
        } else {
            builder = FabricRegistryBuilder.createSimple(registryKey);
        }

        return builder.attribute(RegistryAttribute.OPTIONAL).buildAndRegister();
    }

    @Override
    public <T> Registry<T> createSynced(ResourceKey<Registry<T>> registryKey, @Nullable Identifier defaultKey) {
        Objects.requireNonNull(registryKey, "registry key is null");
        FabricRegistryBuilder<T, ? extends Registry<T>> builder;
        if (defaultKey != null) {
            builder = FabricRegistryBuilder.createDefaulted(registryKey, defaultKey);
        } else {
            builder = FabricRegistryBuilder.createSimple(registryKey);
        }

        return builder.attribute(RegistryAttribute.OPTIONAL).attribute(RegistryAttribute.SYNCED).buildAndRegister();
    }
}
