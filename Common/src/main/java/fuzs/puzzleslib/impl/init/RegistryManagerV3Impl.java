package fuzs.puzzleslib.impl.init;

import fuzs.puzzleslib.api.core.v1.ModLoader;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.init.v3.RegistryManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public abstract class RegistryManagerV3Impl implements RegistryManager {
    protected final String modId;
    protected Set<ModLoader> allowedModLoaders = EnumSet.allOf(ModLoader.class);

    protected RegistryManagerV3Impl(String modId) {
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
    public final <T> Holder.Reference<T> register(final ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier) {
        Objects.requireNonNull(registryKey, "registry key is null");
        Objects.requireNonNull(supplier, "supplier is null");
        Holder.Reference<T> holder;
        if (!this.allowedModLoaders.contains(ModLoaderEnvironment.INSTANCE.getModLoader())) {
            holder = this.getHolder(registryKey, path);
        } else {
            holder = this.register$Internal(registryKey, path, supplier);
        }
        this.allowedModLoaders = EnumSet.allOf(ModLoader.class);
        return holder;
    }

    protected abstract <T> Holder.Reference<T> register$Internal(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier);
}
