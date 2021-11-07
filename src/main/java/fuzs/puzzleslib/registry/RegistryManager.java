package fuzs.puzzleslib.registry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
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
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
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
    private final Multimap<IForgeRegistry<? extends IForgeRegistryEntry<?>>, Supplier<? extends IForgeRegistryEntry<?>>> registryToFactory = ArrayListMultimap.create();

    /**
     * private constructor
     * @param namespace namespace for this instance
     */
    private RegistryManager(String namespace) {
        this.namespace = namespace;
    }

    /**
     * listener is added in main mod class so it's always puzzles lib itself and not the first mod registering something
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
        this.registryToFactory.get(registry).forEach(entry -> {
            registry.register((T) entry.get());
        });
    }

    /**
     * register any type of registry entry with a preset path
     * @param registry type for this registry entry
     * @param entry supplier for entry to register
     * @param <T>      entry type
     */
    public <T extends IForgeRegistryEntry<T>> void register(IForgeRegistry<T> registry, Supplier<T> entry) {
        this.register(registry, null, entry);
    }

    /**
     * register any type of registry entry with a path
     * @param registry type for this registry entry
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> registry type
     * @param <U> entry type
     */
    public <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject<U> register(IForgeRegistry<T> registry, @Nullable String path, Supplier<U> entry) {
        this.registryToFactory.put(registry, () -> {
            T e = entry.get();
            Objects.requireNonNull(e, "Can't register null object");
            if (e.getRegistryName() == null) {
                Objects.requireNonNull(path, "Can't register object without name");
                e.setRegistryName(this.locate(path));
            }
            return e;
        });
        if (path != null) {
            return RegistryObject.of(this.locate(path), registry);
        }
        return null;
    }

    /**
     * register block entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<Block> registerBlock(@Nullable String path, Supplier<Block> entry) {
        return this.register(ForgeRegistries.BLOCKS, path, entry);
    }

    /**
     * register block entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @param creativeTab creative tab for item
     * @return registry object for <code>entry</code> block
     */
    public RegistryObject<Block> registerBlockWithItem(@Nonnull String path, Supplier<Block> entry, CreativeModeTab creativeTab) {
        return this.registerBlockWithItem(path, entry, new Item.Properties().tab(creativeTab));
    }

    /**
     * register block entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @param properties properties for item, should include tab
     * @return registry object for <code>entry</code> block
     */
    public RegistryObject<Block> registerBlockWithItem(@Nonnull String path, Supplier<Block> entry, Item.Properties properties) {
        // order doesn't matter on Forge, but will do on Fabric
        final RegistryObject<Block> block = this.registerBlock(path, entry);
        this.registerBlockItem(path, properties);
        return block;
    }

    /**
     * register fluid entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<Fluid> registerFluid(@Nullable String path, Supplier<Fluid> entry) {
        return this.register(ForgeRegistries.FLUIDS, path, entry);
    }

    /**
     * register item entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<Item> registerItem(@Nullable String path, Supplier<Item> entry) {
        return this.register(ForgeRegistries.ITEMS, path, entry);
    }

    /**
     * register block entry with a path
     * @param path optional path for new entry
     * @param creativeTab creative tab for item
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<Item> registerBlockItem(@Nonnull String path, CreativeModeTab creativeTab) {
        return this.registerBlockItem(path, new Item.Properties().tab(creativeTab));
    }

    /**
     * register block entry with a path
     * @param path optional path for new entry
     * @param properties properties for item, should include tab
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<Item> registerBlockItem(@Nonnull String path, Item.Properties properties) {
        return this.registerItem(path, () -> {
            Block block = ForgeRegistries.BLOCKS.getValue(this.locate(path));
            Objects.requireNonNull(block, "Can't register item for null block");
            return new BlockItem(block, properties);
        });
    }

    /**
     * register mob effect entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<MobEffect> registerMobEffect(@Nullable String path, Supplier<MobEffect> entry) {
        return this.register(ForgeRegistries.MOB_EFFECTS, path, entry);
    }

    /**
     * register sound event entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<SoundEvent> registerSoundEvent(@Nullable String path, Supplier<SoundEvent> entry) {
        return this.register(ForgeRegistries.SOUND_EVENTS, path, entry);
    }

    /**
     * register potion entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<Potion> registerPotion(@Nullable String path, Supplier<Potion> entry) {
        return this.register(ForgeRegistries.POTIONS, path, entry);
    }

    /**
     * register enchantment entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<Enchantment> registerEnchantment(@Nullable String path, Supplier<Enchantment> entry) {
        return this.register(ForgeRegistries.ENCHANTMENTS, path, entry);
    }

    /**
     * register entity type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> entity type type
     */
    public <T extends Entity> RegistryObject<EntityType<T>> registerRawEntityType(@Nonnull String path, Supplier<EntityType.Builder<T>> entry) {
        return this.registerEntityType(path, () -> entry.get().build(path));
    }

    /**
     * register entity type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> entity type type
     */
    public <T extends Entity> RegistryObject<EntityType<T>> registerEntityType(@Nullable String path, Supplier<EntityType<T>> entry) {
        return this.register(ForgeRegistries.ENTITIES, path, entry);
    }

    /**
     * register tile entity type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> block entity type
     */
    @SuppressWarnings("ConstantConditions")
    public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerRawBlockEntityType(@Nullable String path, Supplier<BlockEntityType.Builder<T>> entry) {
        return this.registerBlockEntityType(path, () -> entry.get().build(null));
    }

    /**
     * register tile entity type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> block entity type
     */
    public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntityType(@Nullable String path, Supplier<BlockEntityType<T>> entry) {
        return this.register(ForgeRegistries.BLOCK_ENTITIES, path, entry);
    }

    /**
     * register container type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> container menu type
     */
    public <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerRawMenuType(@Nullable String path, Supplier<MenuType.MenuSupplier<T>> entry) {
        return this.registerMenuType(path, () -> new MenuType<>(entry.get()));
    }

    /**
     * register container type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> container menu type
     */
    public <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(@Nullable String path, Supplier<MenuType<T>> entry) {
        return this.register(ForgeRegistries.CONTAINERS, path, entry);
    }

    /**
     * @param path path for location
     * @return resource location for {@link #namespace}
     */
    private ResourceLocation locate(String path) {
        return new ResourceLocation(this.namespace, path);
    }

    /**
     * creates a new registry manager for <code>namespace</code> or returns an existing one
     * @param namespace namespace used for registration
     * @return new mod specific registry manager
     */
    public static RegistryManager of(String namespace) {
        return MOD_TO_REGISTRY.computeIfAbsent(namespace, key -> {
            final RegistryManager manager = new RegistryManager(namespace);
            FMLJavaModLoadingContext.get().getModEventBus().register(manager);
            return manager;
        });
    }
}
