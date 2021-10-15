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
    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRegistryRegister(RegistryEvent.Register<?> evt) {
        this.getCurrentModRegistry().addAllToRegistry(evt.getRegistry());
    }

    /**
     * register any type of registry entry with a preset path
     * @param entry entry to register
     */
    @Deprecated
    public <T extends IForgeRegistryEntry<T>> void register(T entry) {

        this.register(null, entry);
    }

    /**
     * register any type of registry entry
     * @param path optional path for new entry
     * @param entry entry to register
     */
    @Deprecated
    public <T extends IForgeRegistryEntry<T>> void register(@Nullable String path, T entry) {

        this.register(entry.getRegistryType(), path, () -> entry);
    }

    /**
     * register any type of registry entry with a preset path
     * @param registryType type for this registry entry
     * @param entry supplier for entry to register
     */
    public <T extends IForgeRegistryEntry<T>> void register(Class<? extends T> registryType, Supplier<T> entry) {

        this.register(registryType, null, entry);
    }

    /**
     * register any type of registry entry with a path
     * @param registryType type for this registry entry
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     * @param <T>          registry entry type
     */
    public <T extends IForgeRegistryEntry<T>> void register(Class<? extends T> registryType, @Nullable String path, Supplier<T> entry) {
        this.getCurrentModRegistry().register(registryType, () -> {
            T e = entry.get();
            Objects.requireNonNull(e, "Can't register null object");
            if (e.getRegistryName() == null) {
                Objects.requireNonNull(path, "Can't register object without name");
                e.setRegistryName(NamespaceUtil.locate(path));
            }
            return e;
        });
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
     */
    public void registerBlock(@Nullable String path, Supplier<Block> entry) {
        this.register(Block.class, path, entry);
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
     */
    public void registerFluid(@Nullable String path, Supplier<Fluid> entry) {
        this.register(Fluid.class, path, entry);
    }

    /**
     * register item entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerItem(@Nullable String path, Supplier<Item> entry) {
        this.register(Item.class, path, entry);
    }

    /**
     * register block entry with a path
     * @param path optional path for new entry
     * @param creativeTab creative tab for item
     */
    public void registerBlockItem(@Nonnull String path, ItemGroup creativeTab) {
        this.registerBlockItem(path, new Item.Properties().tab(creativeTab));
    }

    /**
     * register block entry with a path
     * @param path optional path for new entry
     * @param properties properties for item, should include tab
     */
    public void registerBlockItem(@Nonnull String path, Item.Properties properties) {
        this.registerItem(path, () -> {
            Block block = ForgeRegistries.BLOCKS.getValue(NamespaceUtil.locate(path));
            Objects.requireNonNull(block, "Can't register item for null block");
            return new BlockItem(block, properties);
        });
    }

    /**
     * register effect entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerEffect(@Nullable String path, Supplier<Effect> entry) {

        this.register(Effect.class, path, entry);
    }

    /**
     * register sound event entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerSoundEvent(@Nullable String path, Supplier<SoundEvent> entry) {

        this.register(SoundEvent.class, path, entry);
    }

    /**
     * register potion entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerPotion(@Nullable String path, Supplier<Potion> entry) {

        this.register(Potion.class, path, entry);
    }

    /**
     * register enchantment entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerEnchantment(@Nullable String path, Supplier<Enchantment> entry) {

        this.register(Enchantment.class, path, entry);
    }

    /**
     * register entity type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerEntityTypeBuilder(@Nonnull String path, Supplier<EntityType.Builder<?>> entry) {

        this.register(EntityType.class, path, () -> entry.get().build(path));
    }

    /**
     * register entity type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerEntityType(@Nullable String path, Supplier<EntityType<?>> entry) {

        this.register(EntityType.class, path, entry);
    }

    /**
     * register tile entity type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    @SuppressWarnings("ConstantConditions")
    public void registerTileEntityTypeBuilder(@Nullable String path, Supplier<TileEntityType.Builder<?>> entry) {

        this.registerTileEntityType(path, () -> entry.get().build(null));
    }

    /**
     * register tile entity type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerTileEntityType(@Nullable String path, Supplier<TileEntityType<?>> entry) {

        this.register(TileEntityType.class, path, entry);
    }

    /**
     * register container type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerContainer(@Nullable String path, ContainerType.IFactory<?> entry) {

        this.registerContainerType(path, () -> new ContainerType<>(entry));
    }

    /**
     * register container type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerContainerType(@Nullable String path, Supplier<ContainerType<?>> entry) {

        this.register(ContainerType.class, path, entry);
    }

    /**
     * mod registry container
     */
    private static class ModRegistry {

        /**
         * internal storage for collecting and registering registry entries
         */
        private final Multimap<Class<? extends IForgeRegistryEntry<?>>, Supplier<? extends IForgeRegistryEntry<?>>> registryToFactory = ArrayListMultimap.create();

        /**
         * register any type of registry entry with a path
         * @param registryType type for this registry entry
         * @param entry supplier for entry to register
         * @param <T>          registry entry type
         */
        public <T extends IForgeRegistryEntry<T>> void register(Class<? extends T> registryType, Supplier<T> entry) {
            this.registryToFactory.put(registryType, entry);
        }

        /**
         * add all entries for registry type to registry
         * @param registry active registry
         * @param <T> type of registry entry
         */
        public <T extends IForgeRegistryEntry<T>> void addAllToRegistry(IForgeRegistry<T> registry) {
            Class<T> type = registry.getRegistrySuperType();
            this.registryToFactory.get(type).forEach(entry -> {
                registry.register((T) entry.get());
            });
        }

    }

}
