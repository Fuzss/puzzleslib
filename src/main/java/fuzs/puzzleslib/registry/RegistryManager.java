package fuzs.puzzleslib.registry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.core.ReflectionHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * handles registering to forge registries
 * this is a mod specific instance now for Fabric compatibility, Forge would support retrieving current namespace from mod loading context
 * heavily inspired by RegistryHelper found in Vazkii's AutoRegLib mod
 */
@SuppressWarnings("unchecked")
public class RegistryManager {
    /**
     * registry data is stored for each mod separately so when registry events are fired every mod is responsible for registering their own stuff
     * this is important so that entries are registered for the proper namespace
     */
    private static final Map<String, RegistryManager> MOD_TO_REGISTRY = Maps.newConcurrentMap();
    /**
     * namespace for this instance
     */
    private final String namespace;
    /**
     * internal storage for collecting and registering registry entries
     */
    private final Multimap<Class<?>, RegistryEntryHolder<?>> registryToFactory = ArrayListMultimap.create();
    /**
     * reference method for {@link #tryUpdateRegistryReference}
     */
    private static final Method UPDATE_REFERENCE_METHOD = ReflectionHelper.getDeclaredMethod(RegistryObject.class, "updateReference", IForgeRegistry.class);

    /**
     * private constructor
     * @param namespace namespace for this instance
     */
    private RegistryManager(String namespace) {
        this.namespace = namespace;
    }

    /**
     * listener is added in main mod class, so it's always puzzles lib itself and not the first mod registering something
     * @param evt all forge registry events
     */
    @SubscribeEvent
    @Deprecated
    public void onRegistryRegister(RegistryEvent.Register<?> evt) {
        this.addAllToRegistry(evt.getRegistry());
    }

    /**
     * add all entries for registry type to registry
     * @param registry active registry
     * @param <T> type of registry entry
     */
    @SuppressWarnings("unchecked")
    public <T extends IForgeRegistryEntry<T>> void addAllToRegistry(IForgeRegistry<T> registry) {
        final Collection<RegistryEntryHolder<?>> suppliers = this.registryToFactory.get(registry.getRegistrySuperType());
        if (!suppliers.isEmpty()) {
            PuzzlesLib.LOGGER.info("Registering {} element(s) to registry of type {} for mod id {}", suppliers.size(), registry.getRegistryName(), this.namespace);
            suppliers.forEach(entry -> {
                RegistryEntryHolder<T> holder = (RegistryEntryHolder<T>) entry;
                registry.register(holder.factory().get());
                holder.updateReference().accept(registry);
            });
        }
    }

    /**
     * register any type of registry entry with a path
     * @param registry type for this registry entry
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> registry type
     * @param <U> entry type
     * @deprecated use method with class argument instead
     */
    @Deprecated
    public <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject<U> register(IForgeRegistry<T> registry, String path, Supplier<U> entry) {
        return this.register(registry.getRegistrySuperType(), path, entry);
    }

    /**
     * register any type of registry entry with a path
     * @param baseType type for this registry entry
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> registry type
     * @param <U> entry type
     */
    public <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject<U> register(Class<T> baseType, String path, Supplier<U> entry) {
        RegistryObject<U> registryObject = RegistryObject.of(this.locate(path), baseType, this.namespace);
        this.registryToFactory.put(baseType, RegistryEntryHolder.of(() -> {
            U e = entry.get();
            Objects.requireNonNull(e, "Can't register null object");
            e.setRegistryName(this.locate(path));
            return e;
        }, registry -> {
            tryUpdateRegistryReference(registryObject, registry);
        }));
        return registryObject;
    }

    /**
     * we manually call updateReference here to make sure our <code>registryObject</code> has a valid reference
     * forge should do this for us, but it does not seem to work reliably and sometimes leads to the reference not having been updated when it is used on the client (e.g. when registering menu provider/block entity renderer)
     * we use reflection as the required method is marked to go package private in the future
     * @param registryObject registry object to call updateReference method on
     * @param registry the registry the object belongs to
     * @param <T> registry type
     * @param <U> entry type
     */
    private static <T extends IForgeRegistryEntry<T>, U extends T> void tryUpdateRegistryReference(RegistryObject<U> registryObject, IForgeRegistry<T> registry) {
        try {
            UPDATE_REFERENCE_METHOD.invoke(registryObject, registry);
        } catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
            PuzzlesLib.LOGGER.warn("Unable to update registry object reference for {}. This might cause a start-up crash!", registryObject.getId(), e);
        }
    }

    /**
     * register block entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<Block> registerBlock(String path, Supplier<Block> entry) {
        return this.register(Block.class, path, entry);
    }

    /**
     * register block entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @param creativeTab creative tab for item
     * @return registry object for <code>entry</code> block
     */
    public RegistryObject<Block> registerBlockWithItem(String path, Supplier<Block> entry, CreativeModeTab creativeTab) {
        return this.registerBlockWithItem(path, entry, new Item.Properties().tab(creativeTab));
    }

    /**
     * register block entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @param properties properties for item, should include tab
     * @return registry object for <code>entry</code> block
     */
    public RegistryObject<Block> registerBlockWithItem(String path, Supplier<Block> entry, Item.Properties properties) {
        // order doesn't matter on Forge, but will do on Fabric
        final RegistryObject<Block> block = this.registerBlock(path, entry);
        this.registerBlockItem(path, properties);
        return block;
    }

    /**
     * register fluid entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<Fluid> registerFluid(String path, Supplier<Fluid> entry) {
        return this.register(Fluid.class, path, entry);
    }

    /**
     * register item entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<Item> registerItem(String path, Supplier<Item> entry) {
        return this.register(Item.class, path, entry);
    }

    /**
     * register item entry with a path
     * @param path path for new entry
     * @param entry function for entry to register
     * @param creativeTab creative tab for item
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<Item> registerItem(String path, Function<Item.Properties, Item> entry, CreativeModeTab creativeTab) {
        return this.registerItem(path, () -> entry.apply(new Item.Properties().tab(creativeTab)));
    }

    /**
     * register block entry with a path
     * @param path path for new entry
     * @param creativeTab creative tab for item
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<Item> registerBlockItem(String path, CreativeModeTab creativeTab) {
        return this.registerBlockItem(path, new Item.Properties().tab(creativeTab));
    }

    /**
     * register block entry with a path
     * @param path path for new entry
     * @param properties properties for item, should include tab
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<Item> registerBlockItem(String path, Item.Properties properties) {
        return this.registerItem(path, () -> {
            Block block = ForgeRegistries.BLOCKS.getValue(this.locate(path));
            Objects.requireNonNull(block, "Can't register item for null block");
            return new BlockItem(block, properties);
        });
    }

    /**
     * register mob effect entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<MobEffect> registerMobEffect(String path, Supplier<MobEffect> entry) {
        return this.register(MobEffect.class, path, entry);
    }

    /**
     * register sound event entry with a path
     * @param path path for new entry
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<SoundEvent> registerRawSoundEvent(String path) {
        return this.registerSoundEvent(path, () -> new SoundEvent(this.locate(path)));
    }

    /**
     * register sound event entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<SoundEvent> registerSoundEvent(String path, Supplier<SoundEvent> entry) {
        return this.register(SoundEvent.class, path, entry);
    }

    /**
     * register potion entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<Potion> registerPotion(String path, Supplier<Potion> entry) {
        return this.register(Potion.class, path, entry);
    }

    /**
     * register enchantment entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<Enchantment> registerEnchantment(String path, Supplier<Enchantment> entry) {
        return this.register(Enchantment.class, path, entry);
    }

    /**
     * register entity type entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> entity type type
     */
    public <T extends Entity> RegistryObject<EntityType<T>> registerRawEntityType(String path, Supplier<EntityType.Builder<T>> entry) {
        return this.registerEntityType(path, () -> entry.get().build(path));
    }

    /**
     * register entity type entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> entity type type
     */
    public <T extends Entity> RegistryObject<EntityType<T>> registerEntityType(String path, Supplier<EntityType<T>> entry) {
        return this.register((Class<EntityType<?>>) (Class<?>) EntityType.class, path, entry);
    }

    /**
     * register tile entity type entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> block entity type
     */
    @SuppressWarnings("ConstantConditions")
    public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerRawBlockEntityType(String path, Supplier<BlockEntityType.Builder<T>> entry) {
        return this.registerBlockEntityType(path, () -> entry.get().build(null));
    }

    /**
     * register tile entity type entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> block entity type
     */
    public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntityType(String path, Supplier<BlockEntityType<T>> entry) {
        return this.register((Class<BlockEntityType<?>>) (Class<?>) BlockEntityType.class, path, entry);
    }

    /**
     * register container type entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> container menu type
     */
    public <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerRawMenuType(String path, Supplier<MenuType.MenuSupplier<T>> entry) {
        return this.registerMenuType(path, () -> new MenuType<>(entry.get()));
    }

    /**
     * register container type entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> container menu type
     */
    public <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(String path, Supplier<MenuType<T>> entry) {
        return this.register((Class<MenuType<?>>) (Class<?>) MenuType.class, path, entry);
    }

    /**
     * @param path path for location
     * @return resource location for {@link #namespace}
     */
    private ResourceLocation locate(String path) {
        if (StringUtils.isEmpty(path)) throw new IllegalArgumentException("Can't register object without name");
        return new ResourceLocation(this.namespace, path);
    }

    /**
     * creates a new registry manager for <code>namespace</code> or returns an existing one
     * @param namespace namespace used for registration
     * @return new mod specific registry manager
     */
    public static synchronized RegistryManager of(String namespace) {
        return MOD_TO_REGISTRY.computeIfAbsent(namespace, key -> {
//            PuzzlesLib.LOGGER.info("Creating registry manager for mod id {}", namespace);
            final RegistryManager manager = new RegistryManager(namespace);
            FMLJavaModLoadingContext.get().getModEventBus().register(manager);
            return manager;
        });
    }

    private static record RegistryEntryHolder<T extends IForgeRegistryEntry<T>>(Supplier<T> factory, Consumer<IForgeRegistry<T>> updateReference) {

        public static <T extends IForgeRegistryEntry<T>> RegistryEntryHolder<T> of(Supplier<T> factory, Consumer<IForgeRegistry<T>> updateReference) {
            return new RegistryEntryHolder<>(factory, updateReference);
        }
    }
}
