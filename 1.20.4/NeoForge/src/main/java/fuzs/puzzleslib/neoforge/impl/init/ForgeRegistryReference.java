package fuzs.puzzleslib.neoforge.impl.init;

import fuzs.puzzleslib.api.init.v2.RegistryReference;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;

/**
 * implementation for Forge
 * @param <T> type of the referenced object
 */
public class ForgeRegistryReference<T> implements RegistryReference<T> {
    /**
     * Forge's {@link RegistryObject} we are wrapping
     */
    private final RegistryObject<T> registryObject;
    /**
     * registry this object has been registered to
     */
    private final ResourceKey<? extends Registry<T>> registryKey;

    /**
     * @param registryObject Forge's {@link RegistryObject} we are wrapping
     * @param registryKey registry this object has been registered to
     */
    public ForgeRegistryReference(RegistryObject<T> registryObject, ResourceKey<? extends Registry<T>> registryKey) {
        this.registryObject = registryObject;
        this.registryKey = registryKey;
    }

    @Override
    public ResourceKey<? extends Registry<? super T>> getRegistryKey() {
        return this.registryKey;
    }

    @Override
    public ResourceKey<T> getResourceKey() {
        return this.registryObject.getKey();
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return this.registryObject.getId();
    }

    @Override
    public T get() {
        return this.registryObject.get();
    }

    @Override
    public Holder<T> holder() {
        return this.registryObject.getHolder().orElseThrow();
    }

    @Override
    public boolean isPresent() {
        return this.registryObject.isPresent();
    }
}
