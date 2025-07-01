package fuzs.puzzleslib.impl.init;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.ModLoader;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.impl.item.CreativeModeTabHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
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
        return ResourceLocationHelper.fromNamespaceAndPath(this.modId, path);
    }

    @Override
    public RegistryManager whenOn(ModLoader... allowedModLoaders) {
        Preconditions.checkState(allowedModLoaders.length > 0, "mod loaders is empty");
        this.allowedModLoaders = EnumSet.copyOf(Arrays.asList(allowedModLoaders));
        return this;
    }

    @Override
    public <T> Holder.Reference<T> registerLazily(ResourceKey<? extends Registry<? super T>> registryKey, String path) {
        Registry<T> registry = LookupHelper.getRegistry(registryKey).orElseThrow();
        ResourceKey<T> resourceKey = this.makeResourceKey(registryKey, path);
        return new LazyHolder<>(registryKey, resourceKey, () -> {
            Holder.Reference<T> holder = registry.getOrThrow(resourceKey);
            if (!holder.isBound()) {
                T value = registry.getValue(resourceKey);
                Objects.requireNonNull(value, "value is null");
                holder.bindValue(value);
            }
            return holder;
        });
    }

    @Override
    public final <T> Holder.Reference<T> register(final ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier) {
        return this.register(registryKey, path, supplier, false);
    }

    public final <T> Holder.Reference<T> register(final ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier, boolean skipRegistration) {
        Objects.requireNonNull(registryKey, "registry key is null");
        Objects.requireNonNull(supplier, "supplier is null");
        Holder.Reference<T> holder;
        if (!this.allowedModLoaders.contains(ModLoaderEnvironment.INSTANCE.getModLoader())) {
            holder = this.registerLazily(registryKey, path);
        } else {
            holder = this.getHolderReference(registryKey, path, supplier, skipRegistration);
        }
        this.allowedModLoaders = EnumSet.allOf(ModLoader.class);
        return holder;
    }

    protected abstract <T> Holder.Reference<T> getHolderReference(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier, boolean skipRegistration);

    @Override
    public Holder.Reference<CreativeModeTab> registerCreativeModeTab(String path, Supplier<ItemStack> iconSupplier, CreativeModeTab.DisplayItemsGenerator displayItems, boolean withSearchBar) {
        return this.register(Registries.CREATIVE_MODE_TAB, path, () -> {
            CreativeModeTab.Builder builder = this.getCreativeModeTabBuilder(withSearchBar);
            ResourceLocation resourceLocation = this.makeKey(path);
            builder.title(CreativeModeTabHelper.getTitle(resourceLocation));
            builder.icon(iconSupplier);
            builder.displayItems(displayItems);
            return builder.build();
        });
    }

    protected abstract CreativeModeTab.Builder getCreativeModeTabBuilder(boolean withSearchBar);
}
