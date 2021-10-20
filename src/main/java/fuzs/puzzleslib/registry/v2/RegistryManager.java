package fuzs.puzzleslib.registry.v2;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.util.NamespaceUtil;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
 * heavily inspired by RegistryHelper found in Vazkii's AutoRegLib mod
 */
public enum RegistryManager {

    INSTANCE;

    /**
     * registry data is stored for each mod separately so when registry events are fired every mod is responsible for registering their own stuff
     * this is important so that entries are registered for the proper namespace
     */
    private final Map<String, ModRegistry> modToRegistry = Maps.newConcurrentMap();

    /**
     * private constructor
     */
    RegistryManager() {

    }

    /**
     * listener is added in main mod class so it's always puzzles lib itself and not the first mod registering something
     * @param evt all forge registry events
     */
    @SubscribeEvent
    public void onRegistryRegister(RegistryEvent.Register<?> evt) {
        this.getCurrentModRegistry().addAllToRegistry(evt.getRegistry());
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
     * @param <T>          registry entry type
     * @return registry object for <code>entry</code>
     */
    public <T extends IForgeRegistryEntry<T>> RegistryObject<T> register(IForgeRegistry<T> registry, @Nullable String path, Supplier<T> entry) {
        this.getCurrentModRegistry().register(registry, () -> {
            T e = entry.get();
            Objects.requireNonNull(e, "Can't register null object");
            if (e.getRegistryName() == null) {
                Objects.requireNonNull(path, "Can't register object without name");
                e.setRegistryName(NamespaceUtil.locate(path));
            }
            return e;
        });
        if (path != null) {
            return RegistryObject.of(NamespaceUtil.locate(path), registry);
        }
        return null;
    }

    /**
     * @return mod registry for active namespace
     */
    private ModRegistry getCurrentModRegistry() {
        return this.getModRegistry(NamespaceUtil.namespace());
    }

    /**
     * @param namespace namespace for mod registry
     * @return mod registry for namespace
     */
    private ModRegistry getModRegistry(String namespace) {
        return this.modToRegistry.computeIfAbsent(namespace, key -> {
            FMLJavaModLoadingContext.get().getModEventBus().register(this);
            return new ModRegistry();
        });
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
     */
    public void registerBlockWithItem(@Nonnull String path, Supplier<Block> entry, ItemGroup creativeTab) {
        this.registerBlockWithItem(path, entry, new Item.Properties().tab(creativeTab));
    }

    /**
     * register block entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @param properties properties for item, should include tab
     */
    public void registerBlockWithItem(@Nonnull String path, Supplier<Block> entry, Item.Properties properties) {
        this.registerBlock(path, entry);
        this.registerBlockItem(path, properties);
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
    public RegistryObject<Item> registerBlockItem(@Nonnull String path, ItemGroup creativeTab) {
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
            Block block = ForgeRegistries.BLOCKS.getValue(NamespaceUtil.locate(path));
            Objects.requireNonNull(block, "Can't register item for null block");
            return new BlockItem(block, properties);
        });
    }

    /**
     * register effect entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<Effect> registerEffect(@Nullable String path, Supplier<Effect> entry) {
        return this.register(ForgeRegistries.POTIONS, path, entry);
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
        return this.register(ForgeRegistries.POTION_TYPES, path, entry);
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
     */
    public RegistryObject<EntityType<?>> registerEntityTypeBuilder(@Nonnull String path, Supplier<EntityType.Builder<?>> entry) {
        return this.registerEntityType(path, () -> entry.get().build(path));
    }

    /**
     * register entity type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<EntityType<?>> registerEntityType(@Nullable String path, Supplier<EntityType<?>> entry) {
        return this.register(ForgeRegistries.ENTITIES, path, entry);
    }

    /**
     * register tile entity type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    @SuppressWarnings("ConstantConditions")
    public RegistryObject<TileEntityType<?>> registerTileEntityTypeBuilder(@Nullable String path, Supplier<TileEntityType.Builder<?>> entry) {
        return this.registerTileEntityType(path, () -> entry.get().build(null));
    }

    /**
     * register tile entity type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<TileEntityType<?>> registerTileEntityType(@Nullable String path, Supplier<TileEntityType<?>> entry) {
        return this.register(ForgeRegistries.TILE_ENTITIES, path, entry);
    }

    /**
     * register container type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<ContainerType<?>> registerContainer(@Nullable String path, ContainerType.IFactory<?> entry) {
        return this.registerContainerType(path, () -> new ContainerType<>(entry));
    }

    /**
     * register container type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public RegistryObject<ContainerType<?>> registerContainerType(@Nullable String path, Supplier<ContainerType<?>> entry) {
        return this.register(ForgeRegistries.CONTAINERS, path, entry);
    }

    /**
     * mod registry container
     */
    private static class ModRegistry {

        /**
         * internal storage for collecting and registering registry entries
         */
        private final Multimap<IForgeRegistry<? extends IForgeRegistryEntry<?>>, Supplier<? extends IForgeRegistryEntry<?>>> registryToFactory = ArrayListMultimap.create();

        /**
         * register any type of registry entry with a path
         * @param registry type for this registry entry
         * @param entry supplier for entry to register
         * @param <T>          registry entry type
         */
        public <T extends IForgeRegistryEntry<T>> void register(IForgeRegistry<T> registry, Supplier<T> entry) {
            this.registryToFactory.put(registry, entry);
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

    }

}
