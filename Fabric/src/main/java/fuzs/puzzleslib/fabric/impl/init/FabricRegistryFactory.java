package fuzs.puzzleslib.fabric.impl.init;

import fuzs.puzzleslib.common.api.init.v3.registry.RegistryFactory;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class FabricRegistryFactory implements RegistryFactory {

    @Override
    public <T> Registry<T> create(ResourceKey<Registry<T>> registryKey, @Nullable Identifier defaultKey) {
        Objects.requireNonNull(registryKey, "registry key is null");
        return this.builder(registryKey, defaultKey).buildAndRegister();
    }

    @Override
    public <T> Registry<T> createSynced(ResourceKey<Registry<T>> registryKey, @Nullable Identifier defaultKey) {
        Objects.requireNonNull(registryKey, "registry key is null");
        return this.builder(registryKey, defaultKey).attribute(RegistryAttribute.SYNCED).buildAndRegister();
    }

    private <T> FabricRegistryBuilder<T, ? extends Registry<T>> builder(ResourceKey<Registry<T>> registryKey, @Nullable Identifier defaultKey) {
        if (defaultKey != null) {
            return FabricRegistryBuilder.createDefaulted(registryKey, defaultKey).attribute(RegistryAttribute.OPTIONAL);
        } else {
            return FabricRegistryBuilder.create(registryKey).attribute(RegistryAttribute.OPTIONAL);
        }
    }
}
