package fuzs.puzzleslib.init;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.init.builder.ModBlockEntityTypeBuilder;
import fuzs.puzzleslib.init.builder.ModMenuSupplier;
import fuzs.puzzleslib.init.builder.ModPoiTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
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
 * handles registering to game registries
 * this is a mod specific instance now for Fabric compatibility, Forge would support retrieving current namespace from mod loading context
 * originally based on RegistryHelper found in Vazkii's AutoRegLib mod
 */
public interface RegistryManager {
    /**
     * registry data is stored for each mod separately so when registry events are fired every mod is responsible for registering their own stuff
     * this is important so that entries are registered for the proper namespace
     */
    Map<String, RegistryManager> MOD_TO_REGISTRY = Maps.newConcurrentMap();

    /**
     * @return namespace for this instance
     */
    String namespace();

    /**
     * allow for deferring registration on Fabric, required when e.g. registering blocks in Fabric project, but related block entity is registered in common
     * <p>follows the same order as Forge: blocks, items, everything else
     */
    default void applyRegistration() {
        // registration is always deferred on Forge, so make this have a default variant
    }

    /**
     * creates a placeholder registry reference for this {@link #namespace()}
     * @param registryKey key for registry to register to
     * @param path path for new entry
     * @param <T> registry type
     * @return placeholder registry object for <code>entry</code>
     */
    default <T> RegistryReference<T> placeholder(final ResourceKey<? extends Registry<? super T>> registryKey, String path) {
        return RegistryReference.placeholder(registryKey, this.makeKey(path));
    }

    /**
     * register any type of registry entry with a path
     * @param registryKey key for registry to register to
     * @param path path for new entry
     * @param supplier supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> registry type
     */
    <T> RegistryReference<T> register(final ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier);

    /**
     * register block entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<Block> registerBlock(String path, Supplier<Block> entry) {
        return this.register(Registry.BLOCK_REGISTRY, path, entry);
    }

    /**
     * register block entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @param creativeTab creative tab for item
     * @return registry object for <code>entry</code> block
     */
    default RegistryReference<Block> registerBlockWithItem(String path, Supplier<Block> entry, CreativeModeTab creativeTab) {
        return this.registerBlockWithItem(path, entry, new Item.Properties().tab(creativeTab));
    }

    /**
     * register block entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @param properties properties for item, should include tab
     * @return registry object for <code>entry</code> block
     */
    default RegistryReference<Block> registerBlockWithItem(String path, Supplier<Block> entry, Item.Properties properties) {
        // order doesn't matter on Forge, but will do on Fabric
        final RegistryReference<Block> block = this.registerBlock(path, entry);
        this.registerBlockItem(path, properties);
        return block;
    }

    /**
     * register fluid entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<Fluid> registerFluid(String path, Supplier<Fluid> entry) {
        return this.register(Registry.FLUID_REGISTRY, path, entry);
    }

    /**
     * register item entry with a path
     * @param path path for new entry
     * @param entry function for entry to register
     * @param creativeTab creative tab for item
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<Item> registerItem(String path, Function<Item.Properties, Item> entry, CreativeModeTab creativeTab) {
        return this.registerItem(path, () -> entry.apply(new Item.Properties().tab(creativeTab)));
    }

    /**
     * register item entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<Item> registerItem(String path, Supplier<Item> entry) {
        return this.register(Registry.ITEM_REGISTRY, path, entry);
    }

    /**
     * register block entry with a path
     * @param path path for new entry
     * @param creativeTab creative tab for item
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<Item> registerBlockItem(String path, CreativeModeTab creativeTab) {
        return this.registerBlockItem(path, new Item.Properties().tab(creativeTab));
    }

    /**
     * register block entry with a path
     * @param path path for new entry
     * @param properties properties for item, should include tab
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<Item> registerBlockItem(String path, Item.Properties properties) {
        return this.registerItem(path, () -> {
            Block block = Registry.BLOCK.get(this.makeKey(path));
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
    default RegistryReference<MobEffect> registerMobEffect(String path, Supplier<MobEffect> entry) {
        return this.register(Registry.MOB_EFFECT_REGISTRY, path, entry);
    }

    /**
     * register sound event entry with a path
     * @param path path for new entry
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<SoundEvent> registerRawSoundEvent(String path) {
        return this.registerSoundEvent(path, () -> new SoundEvent(this.makeKey(path)));
    }

    /**
     * register sound event entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<SoundEvent> registerSoundEvent(String path, Supplier<SoundEvent> entry) {
        return this.register(Registry.SOUND_EVENT_REGISTRY, path, entry);
    }

    /**
     * register potion entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<Potion> registerPotion(String path, Supplier<Potion> entry) {
        return this.register(Registry.POTION_REGISTRY, path, entry);
    }

    /**
     * register enchantment entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<Enchantment> registerEnchantment(String path, Supplier<Enchantment> entry) {
        return this.register(Registry.ENCHANTMENT_REGISTRY, path, entry);
    }

    /**
     * register entity type entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> entity type type
     */
    @SuppressWarnings("unchecked")
    default <T extends Entity> RegistryReference<EntityType<T>> registerEntityType(String path, Supplier<EntityType<T>> entry) {
        return this.register((ResourceKey<Registry<EntityType<T>>>) (ResourceKey<?>) Registry.ENTITY_TYPE_REGISTRY, path, entry);
    }

    /**
     * register entity type entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> entity type type
     */
    default <T extends Entity> RegistryReference<EntityType<T>> registerEntityTypeBuilder(String path, Supplier<EntityType.Builder<T>> entry) {
        return this.registerEntityType(path, () -> entry.get().build(path));
    }

    /**
     * register tile entity type entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> block entity type
     */
    @SuppressWarnings("unchecked")
    default <T extends BlockEntity> RegistryReference<BlockEntityType<T>> registerBlockEntityType(String path, Supplier<BlockEntityType<T>> entry) {
        return this.register((ResourceKey<Registry<BlockEntityType<T>>>) (ResourceKey<?>) Registry.BLOCK_ENTITY_TYPE_REGISTRY, path, entry);
    }

    /**
     * register tile entity type entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> block entity type
     */
    <T extends BlockEntity> RegistryReference<BlockEntityType<T>> registerBlockEntityTypeBuilder(String path, Supplier<ModBlockEntityTypeBuilder<T>> entry);

    /**
     * register container type entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> container menu type
     */
    @SuppressWarnings("unchecked")
    default <T extends AbstractContainerMenu> RegistryReference<MenuType<T>> registerMenuType(String path, Supplier<MenuType<T>> entry) {
        return this.register((ResourceKey<Registry<MenuType<T>>>) (ResourceKey<?>) Registry.MENU_REGISTRY, path, entry);
    }

    /**
     * register container type entry with a path
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     * @param <T> container menu type
     */
    <T extends AbstractContainerMenu> RegistryReference<MenuType<T>> registerMenuTypeSupplier(String path, Supplier<ModMenuSupplier<T>> entry);

    /**
     * register poi type from custom builder
     * @param path path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    RegistryReference<PoiType> registerPoiTypeBuilder(String path, Supplier<ModPoiTypeBuilder> entry);

    /**
     * @param path path for location
     * @return resource location for {@link #namespace}
     */
    default ResourceLocation makeKey(String path) {
        if (StringUtils.isEmpty(path)) throw new IllegalArgumentException("Can't register object without name");
        return new ResourceLocation(this.namespace(), path);
    }
}
