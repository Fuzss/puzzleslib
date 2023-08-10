package fuzs.puzzleslib.impl.init;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.api.core.v1.ModLoader;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.init.v2.RegistryReference;
import fuzs.puzzleslib.api.init.v2.builder.ExtendedMenuSupplier;
import fuzs.puzzleslib.api.init.v2.builder.PoiTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * handles registering to forge registries
 * <p>this is a mod specific instance now for Fabric compatibility, Forge would support retrieving current namespace from mod loading context
 * <p>originally heavily inspired by RegistryHelper found in Vazkii's AutoRegLib mod
 */
public class ForgeRegistryManager implements RegistryManager {
    /**
     * namespace for this instance
     */
    private final String namespace;
    /**
     * the mod event bus required for registering {@link DeferredRegister}
     */
    private final IEventBus eventBus;
    /**
     * storage for {@link DeferredRegister} required for registering data on Forge
     */
    private final Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> deferredRegisters = Maps.newIdentityHashMap();
    /**
     * mod loader sto register the next entry to, null by default for registering to any
     */
    @Nullable
    private Set<ModLoader> allowedModLoaders;

    public ForgeRegistryManager(String modId) {
        this.namespace = modId;
        this.eventBus = ModContainerHelper.findModEventBus(modId).orElseThrow();
    }

    @Override
    public String namespace() {
        return this.namespace;
    }

    @Override
    public RegistryManager whenOn(ModLoader... allowedModLoaders) {
        Preconditions.checkPositionIndex(0, allowedModLoaders.length - 1, "mod loaders is empty");
        this.allowedModLoaders = Sets.immutableEnumSet(Arrays.asList(allowedModLoaders));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> RegistryReference<T> register(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier) {
        Set<ModLoader> allowedModLoaders = this.allowedModLoaders;
        this.allowedModLoaders = null;
        if (allowedModLoaders != null && !allowedModLoaders.contains(ModLoader.FORGE)) {
            return this.placeholder(registryKey, path);
        }
        DeferredRegister<?> register = this.deferredRegisters.computeIfAbsent(registryKey, key -> {
            DeferredRegister<T> deferredRegister = DeferredRegister.create((ResourceKey<? extends Registry<T>>) registryKey, this.namespace);
            deferredRegister.register(this.eventBus);
            return deferredRegister;
        });
        if (StringUtils.isEmpty(path)) throw new IllegalArgumentException("Can't register object without name");
        RegistryObject<T> registryObject = ((DeferredRegister<T>) register).register(path, supplier);
        return new ForgeRegistryReference<>(registryObject, (ResourceKey<? extends Registry<T>>) registryKey);
    }

    @Override
    public RegistryReference<Item> registerSpawnEggItem(RegistryReference<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor, Item.Properties itemProperties) {
        return this.registerItem(entityTypeReference.getResourceLocation().getPath() + "_spawn_egg", () -> new ForgeSpawnEggItem(entityTypeReference::get, backgroundColor, highlightColor, itemProperties));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends AbstractContainerMenu> RegistryReference<MenuType<T>> registerExtendedMenuType(String path, Supplier<ExtendedMenuSupplier<T>> entry) {
        return this.register((ResourceKey<Registry<MenuType<T>>>) (ResourceKey<?>) Registry.MENU_REGISTRY, path, () -> new MenuType<>((IContainerFactory<T>) (containerId, inventory, data) -> entry.get().create(containerId, inventory, data)));
    }

    @Override
    public RegistryReference<PoiType> registerPoiTypeBuilder(String path, Supplier<PoiTypeBuilder> entry) {
        return this.register(Registry.POINT_OF_INTEREST_TYPE_REGISTRY, path, () -> {
            PoiTypeBuilder builder = entry.get();
            return new PoiType(path, ImmutableSet.copyOf(builder.blocks()), builder.ticketCount(), builder.searchDistance());
        });
    }
}
