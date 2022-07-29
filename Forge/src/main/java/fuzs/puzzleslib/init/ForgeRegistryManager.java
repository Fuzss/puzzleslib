package fuzs.puzzleslib.init;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.PuzzlesLibForge;
import fuzs.puzzleslib.init.builder.ModBlockEntityTypeBuilder;
import fuzs.puzzleslib.init.builder.ModMenuSupplier;
import fuzs.puzzleslib.init.builder.ModPoiTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.function.Supplier;

/**
 * handles registering to forge registries
 * this is a mod specific instance now for Fabric compatibility, Forge would support retrieving current namespace from mod loading context
 * originally heavily inspired by RegistryHelper found in Vazkii's AutoRegLib mod
 */
public class ForgeRegistryManager implements RegistryManager {
    /**
     * namespace for this instance
     */
    private final String namespace;
    /**
     * the mod event bus required for registering {@link DeferredRegister}
     */
    private final IEventBus modEventBus;
    /**
     * storage for {@link DeferredRegister} required for registering data on Forge
     */
    private final Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> deferredRegisters = Maps.newIdentityHashMap();

    /**
     * private constructor
     * @param namespace namespace for this instance
     */
    private ForgeRegistryManager(String namespace) {
        this.namespace = namespace;
        this.modEventBus = PuzzlesLibForge.findModEventBus(namespace);
    }

    @Override
    public String namespace() {
        return this.namespace;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> RegistryReference<T> register(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier) {
        DeferredRegister<?> register = this.deferredRegisters.computeIfAbsent(registryKey, key -> {
            DeferredRegister<T> deferredRegister = DeferredRegister.create((ResourceKey<? extends Registry<T>>) registryKey, this.namespace);
            deferredRegister.register(this.modEventBus);
            return deferredRegister;
        });
        if (StringUtils.isEmpty(path)) throw new IllegalArgumentException("Can't register object without name");
        RegistryObject<T> registryObject = ((DeferredRegister<T>) register).register(path, supplier);
        return new ForgeRegistryReference<>(registryObject, (ResourceKey<? extends Registry<T>>) registryKey);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public <T extends BlockEntity> RegistryReference<BlockEntityType<T>> registerBlockEntityTypeBuilder(String path, Supplier<ModBlockEntityTypeBuilder<T>> entry) {
        return this.registerBlockEntityType(path, () -> {
            ModBlockEntityTypeBuilder<T> builder = entry.get();
            return BlockEntityType.Builder.of(builder.factory()::create, builder.blocks()).build(null);
        });
    }

    @Override
    public <T extends AbstractContainerMenu> RegistryReference<MenuType<T>> registerMenuTypeSupplier(String path, Supplier<ModMenuSupplier<T>> entry) {
        return this.registerMenuType(path, () -> new MenuType<>(entry.get()::create));
    }

    @Override
    public RegistryReference<PoiType> registerPoiTypeBuilder(String path, Supplier<ModPoiTypeBuilder> entry) {
        return this.register(Registry.POINT_OF_INTEREST_TYPE_REGISTRY, path, () -> {
            ModPoiTypeBuilder builder = entry.get();
            return new PoiType(ImmutableSet.copyOf(builder.blocks()), builder.ticketCount(), builder.searchDistance());
        });
    }

    /**
     * creates a new registry manager for <code>namespace</code> or returns an existing one
     * @param namespace namespace used for registration
     * @return new mod specific registry manager
     */
    public synchronized static RegistryManager of(String namespace) {
        return MOD_TO_REGISTRY.computeIfAbsent(namespace, ForgeRegistryManager::new);
    }
}
