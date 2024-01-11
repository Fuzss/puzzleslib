package fuzs.puzzleslib.impl.init;

import fuzs.puzzleslib.api.core.v1.ModLoader;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.init.v2.RegistryReference;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public abstract class RegistryManagerImpl implements RegistryManager {
    protected final String modId;
    protected Set<ModLoader> allowedModLoaders = EnumSet.allOf(ModLoader.class);

    protected RegistryManagerImpl(String modId) {
        this.modId = modId;
    }

    @Override
    public ResourceLocation makeKey(String path) {
        if (StringUtils.isEmpty(path)) throw new IllegalArgumentException("path is invalid");
        return new ResourceLocation(this.modId, path);
    }

    @Override
    public RegistryManager whenOn(ModLoader... allowedModLoaders) {
        Objects.checkIndex(0, allowedModLoaders.length);
        this.allowedModLoaders = EnumSet.copyOf(Arrays.asList(allowedModLoaders));
        return this;
    }

    @Override
    public final <T> RegistryReference<T> register(final ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier) {
        RegistryReference<T> registryReference;
        if (!this.allowedModLoaders.contains(ModLoaderEnvironment.INSTANCE.getModLoader())) {
            registryReference = this.placeholder(registryKey, path);
        } else {
            registryReference = this.actuallyRegister(registryKey, path, supplier);
        }
        this.allowedModLoaders = EnumSet.allOf(ModLoader.class);
        return registryReference;
    }

    protected abstract  <T> RegistryReference<T> actuallyRegister(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier);
}
