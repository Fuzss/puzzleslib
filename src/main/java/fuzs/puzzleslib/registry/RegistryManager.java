package fuzs.puzzleslib.registry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * handles registering to forge registries
 * heavily inspired by RegistryHelper found in Vazkii's AutoRegLib mod
 */
@Deprecated
public class RegistryManager {

    /**
     * internal storage for collecting and registering registry entries
     * make this synchronized just in case
     */
    private final Multimap<Class<?>, Pair<ResourceLocation, Supplier<IForgeRegistryEntry<?>>>> registryEntryPairs = Multimaps.synchronizedListMultimap(ArrayListMultimap.create());

    /**
     * private singleton constructor
     */
    private RegistryManager() {

    }

    /**
     * listener is added in main mod class so it's always puzzles lib itself and not the first mod registering something
     * @param evt all forge registry events
     */
    @SubscribeEvent
    public void onRegistryRegister(RegistryEvent.Register<?> evt) {

        this.addAllToRegistry(evt.getRegistry());
    }

    /**
     * add all entries for registry type to registry
     * @param registry active registry
     * @param <T> type of registry entry
     */
    @SuppressWarnings("unchecked")
    private <T extends IForgeRegistryEntry<T>> void addAllToRegistry(IForgeRegistry<T> registry) {

        Class<T> type = registry.getRegistrySuperType();
        if (this.registryEntryPairs.containsKey(type)) {

            for (Pair<ResourceLocation, Supplier<IForgeRegistryEntry<?>>> registryPair : this.registryEntryPairs.get(type)) {

                ResourceLocation name = registryPair.getLeft();
                IForgeRegistryEntry<?> entry = registryPair.getRight().get();
                if (entry == null) {

                    throw new IllegalArgumentException("Can't register null object.");
                }

                if (entry.getRegistryName() == null) {

                    assert name != null;
                    entry.setRegistryName(name);
                }

                registry.register((T) entry);
            }

            this.registryEntryPairs.removeAll(type);
        }
    }

    /**
     * register any type of registry entry with a preset path
     * @param entry entry to register
     */
    @Deprecated
    public void register(IForgeRegistryEntry<?> entry) {

        this.register(null, entry);
    }

    /**
     * register any type of registry entry
     * @param path optional path for new entry
     * @param entry entry to register
     */
    @Deprecated
    public void register(@Nullable String path, IForgeRegistryEntry<?> entry) {

        this.register(entry.getRegistryType(), path, () -> entry);
    }

    /**
     * register any type of registry entry with a preset path
     * @param registryType type for this registry entry
     * @param entry supplier for entry to register
     */
    public void register(Class<?> registryType, Supplier<IForgeRegistryEntry<?>> entry) {

        this.register(registryType, null, entry);
    }

    /**
     * register any type of registry entry with a path
     * @param registryType type for this registry entry
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void register(Class<?> registryType, @Nullable String path, Supplier<IForgeRegistryEntry<?>> entry) {

        ResourceLocation name = null;
        if (path != null) {

            name = NamespaceUtil.locate(path);
        }

        this.registryEntryPairs.put(registryType, Pair.of(name, entry));
    }

    public void registerBlockWithItem(@Nullable String path, Supplier<IForgeRegistryEntry<?>> entry, ItemGroup creativeTab) {

        this.registerBlockWithItem(path, entry, new Item.Properties().tab(creativeTab));
    }

    public void registerBlockWithItem(@Nullable String path, Supplier<IForgeRegistryEntry<?>> entry, Item.Properties properties) {

        this.registerBlock(path, entry);
        // store this, will be puzzles when supplier is executed
        final ResourceLocation location = NamespaceUtil.locate(path);
        this.registerItem(path, () -> {

            Block block = path != null ? ForgeRegistries.BLOCKS.getValue(location) : (Block) entry.get();
            return block != null ? new BlockItem(block, properties) : null;
        });
    }

    /**
     * register block entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerBlock(@Nullable String path, Supplier<IForgeRegistryEntry<?>> entry) {

        this.register(Block.class, path, entry);
    }

    /**
     * register fluid entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerFluid(@Nullable String path, Supplier<IForgeRegistryEntry<?>> entry) {

        this.register(Fluid.class, path, entry);
    }

    /**
     * register item entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerItem(@Nullable String path, Supplier<IForgeRegistryEntry<?>> entry) {

        this.register(Item.class, path, entry);
    }

    /**
     * register effect entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerEffect(@Nullable String path, Supplier<IForgeRegistryEntry<?>> entry) {

        this.register(Effect.class, path, entry);
    }

    /**
     * register sound event entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerSoundEvent(@Nullable String path, Supplier<IForgeRegistryEntry<?>> entry) {

        this.register(SoundEvent.class, path, entry);
    }

    /**
     * register potion entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerPotion(@Nullable String path, Supplier<IForgeRegistryEntry<?>> entry) {

        this.register(Potion.class, path, entry);
    }

    /**
     * register enchantment entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerEnchantment(@Nullable String path, Supplier<IForgeRegistryEntry<?>> entry) {

        this.register(Enchantment.class, path, entry);
    }

    /**
     * register entity type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerEntityType(@Nullable String path, Supplier<IForgeRegistryEntry<?>> entry) {

        this.register(EntityType.class, path, entry);
    }

    /**
     * register tile entity type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerTileEntityType(@Nullable String path, Supplier<IForgeRegistryEntry<?>> entry) {

        this.register(TileEntityType.class, path, entry);
    }

    /**
     * register container type entry with a path
     * @param path optional path for new entry
     * @param entry supplier for entry to register
     */
    public void registerContainerType(@Nullable String path, Supplier<IForgeRegistryEntry<?>> entry) {

        this.register(ContainerType.class, path, entry);
    }

    /**
     * @return {@link RegistryManager} instance
     */
    public static RegistryManager getInstance() {

        return RegistryManager.RegistryManagerHolder.INSTANCE;
    }

    /**
     * instance holder class for lazy and thread-safe initialization
     */
    private static class RegistryManagerHolder {

        private static final RegistryManager INSTANCE = new RegistryManager();

    }

}