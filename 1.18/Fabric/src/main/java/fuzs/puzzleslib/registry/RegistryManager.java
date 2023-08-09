package fuzs.puzzleslib.registry;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.PuzzlesLib;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.core.Registry;
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
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * handles registering to forge registries
 * this is a mod specific instance now for Fabric compatibility, Forge would support retrieving current namespace from mod loading context
 * heavily inspired by RegistryHelper found in Vazkii's AutoRegLib mod
 */
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
     * private constructor
     * @param namespace namespace for this instance
     */
    private RegistryManager(String namespace) {
        this.namespace = namespace;
    }

    /**
     * register any type of registry entry with a path
     * @param registry type for this registry entry
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> registry type
     */
    public <T> T register(Registry<? super T> registry, String path, Supplier<T> entry) {
        T e = entry.get();
        Objects.requireNonNull(e, "Can't register null object");
        return Registry.register(registry, this.locate(path), e);
    }

    /**
     * register block entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public Block registerBlock(String path, Supplier<Block> entry) {
        return this.register(Registry.BLOCK, path, entry);
    }

    /**
     * register block entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @param creativeTab creative tab for item
     * @return registry object for <code>entry</code> block
     */
    public Block registerBlockWithItem(String path, Supplier<Block> entry, CreativeModeTab creativeTab) {
        return this.registerBlockWithItem(path, entry, new Item.Properties().tab(creativeTab));
    }

    /**
     * register block entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @param properties properties for item, should include tab
     * @return registry object for <code>entry</code> block
     */
    public Block registerBlockWithItem(String path, Supplier<Block> entry, Item.Properties properties) {
        // order doesn't matter on Forge, but will do on Fabric
        final Block block = this.registerBlock(path, entry);
        this.registerBlockItem(path, properties);
        return block;
    }

    /**
     * register fluid entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public Fluid registerFluid(String path, Supplier<Fluid> entry) {
        return this.register(Registry.FLUID, path, entry);
    }

    /**
     * register item entry with a path
     * @param path path for new entry
     * @param entry function for entry to register
     * @param creativeTab creative tab for item
     * @return registry object for <code>entry</code>
     */
    public Item registerItem(String path, Function<Item.Properties, Item> entry, CreativeModeTab creativeTab) {
        return this.registerItem(path, () -> entry.apply(new Item.Properties().tab(creativeTab)));
    }

    /**
     * register item entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public Item registerItem(String path, Supplier<Item> entry) {
        return this.register(Registry.ITEM, path, entry);
    }

    /**
     * register block entry with a path
     * @param path path for new entry
     * @param creativeTab creative tab for item
     * @return registry object for <code>entry</code>
     */
    public Item registerBlockItem(String path, CreativeModeTab creativeTab) {
        return this.registerBlockItem(path, new Item.Properties().tab(creativeTab));
    }

    /**
     * register block entry with a path
     * @param path path for new entry
     * @param properties properties for item, should include tab
     * @return registry object for <code>entry</code>
     */
    public Item registerBlockItem(String path, Item.Properties properties) {
        return this.registerItem(path, () -> {
            Block block = Registry.BLOCK.get(this.locate(path));
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
    public MobEffect registerMobEffect(String path, Supplier<MobEffect> entry) {
        return this.register(Registry.MOB_EFFECT, path, entry);
    }

    /**
     * register sound event entry with a path
     * @param path path for new entry
     * @return registry object for <code>entry</code>
     */
    public SoundEvent registerRawSoundEvent(String path) {
        return this.registerSoundEvent(path, () -> new SoundEvent(this.locate(path)));
    }

    /**
     * register sound event entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public SoundEvent registerSoundEvent(String path, Supplier<SoundEvent> entry) {
        return this.register(Registry.SOUND_EVENT, path, entry);
    }

    /**
     * register potion entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public Potion registerPotion(String path, Supplier<Potion> entry) {
        return this.register(Registry.POTION, path, entry);
    }

    /**
     * register enchantment entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    public Enchantment registerEnchantment(String path, Supplier<Enchantment> entry) {
        return this.register(Registry.ENCHANTMENT, path, entry);
    }

    /**
     * register entity type entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> entity type type
     */
    public <T extends Entity> EntityType<T> registerRawEntityType(String path, Supplier<EntityType.Builder<T>> entry) {
        return this.registerEntityType(path, () -> entry.get().build(path));
    }

    /**
     * register entity type entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> entity type type
     */
    public <T extends Entity> EntityType<T> registerEntityType(String path, Supplier<EntityType<T>> entry) {
        return this.register(Registry.ENTITY_TYPE, path, entry);
    }

    /**
     * register tile entity type entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> block entity type
     */
    public <T extends BlockEntity> BlockEntityType<T> registerRawBlockEntityType(String path, Supplier<FabricBlockEntityTypeBuilder<T>> entry) {
        return this.registerBlockEntityType(path, () -> entry.get().build());
    }

    /**
     * register tile entity type entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> block entity type
     */
    public <T extends BlockEntity> BlockEntityType<T> registerBlockEntityType(String path, Supplier<BlockEntityType<T>> entry) {
        return this.register(Registry.BLOCK_ENTITY_TYPE, path, entry);
    }

    /**
     * register container type entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> container menu type
     */
    public <T extends AbstractContainerMenu> MenuType<T> registerRawMenuType(String path, Supplier<ScreenHandlerRegistry.SimpleClientHandlerFactory<T>> entry) {
        final ScreenHandlerRegistry.SimpleClientHandlerFactory<T> e = entry.get();
        Objects.requireNonNull(e, "Can't register null object");
        return ScreenHandlerRegistry.registerSimple(this.locate(path), e);
    }

    /**
     * register container type entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> container menu type
     */
    public <T extends AbstractContainerMenu> MenuType<T> registerMenuType(String path, Supplier<MenuType<T>> entry) {
        return this.register(Registry.MENU, path, entry);
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
    public static RegistryManager of(String namespace) {
        return MOD_TO_REGISTRY.computeIfAbsent(namespace, namespace1 -> {
            PuzzlesLib.LOGGER.info("Creating registry manager for mod id {}", namespace);
            return new RegistryManager(namespace1);
        });
    }
}
