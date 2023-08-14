package fuzs.puzzleslib.api.init.v2;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.api.core.v1.ModLoader;
import fuzs.puzzleslib.api.init.v2.builder.ExtendedMenuSupplier;
import fuzs.puzzleslib.api.init.v2.builder.PoiTypeBuilder;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * handles registering to game registries
 * this is a mod specific instance now for Fabric compatibility, Forge would support retrieving current namespace from mod loading context
 * originally based on RegistryHelper found in Vazkii's AutoRegLib mod
 */
public interface RegistryManager {

    /**
     * Creates a new registry manager for <code>namespace</code> or returns an existing one.
     * <p>Registration happens instantly for this manager.
     *
     * @param modId namespace used for registration
     * @return new mod specific registry manager
     */
    static RegistryManager instant(String modId) {
        return ModContext.get(modId).getRegistryManager(false);
    }

    /**
     * Creates a new registry manager for <code>namespace</code> or returns an existing one.
     * <p>Registration is deferred for this manager until {@link RegistryManager#applyRegistration()} is called.
     *
     * @param modId namespace used for registration
     * @return new mod specific registry manager
     */
    static RegistryManager deferred(String modId) {
        return ModContext.get(modId).getRegistryManager(true);
    }

    /**
     * @return namespace for this instance
     */
    String namespace();

    /**
     * allows for registering content in the common project for only a few mod loaders
     *
     * @param allowedModLoaders the mod loaders to register on, every mod loader not registered to should handle this in the loader specific subproject
     * @return this manager as a builder
     */
    RegistryManager whenOn(ModLoader... allowedModLoaders);

    /**
     * allows for registering content in the common project for only a few mod loaders
     *
     * @param forbiddenModLoaders the mod loaders to not register on
     * @return this manager as a builder
     */
    default RegistryManager whenNotOn(ModLoader... forbiddenModLoaders) {
        Preconditions.checkPositionIndex(0, forbiddenModLoaders.length - 1, "mod loaders is empty");
        return this.whenOn(EnumSet.complementOf(Sets.newEnumSet(Arrays.asList(forbiddenModLoaders), ModLoader.class)).toArray(ModLoader[]::new));
    }

    /**
     * allow for deferring registration on Fabric, required when e.g. registering blocks in Fabric project, but related block entity is registered in common
     * <p>follows the same order as Forge: blocks, items, everything else
     */
    default void applyRegistration() {
        // registration is always deferred on Forge, so make this have a default variant
    }

    /**
     * creates a placeholder registry reference for this {@link #namespace()}
     *
     * @param registryKey key for registry to register to
     * @param path        path for new entry
     * @param <T>         registry type
     * @return placeholder registry object for <code>entry</code>
     */
    default <T> RegistryReference<T> placeholder(final ResourceKey<? extends Registry<? super T>> registryKey, String path) {
        return RegistryReference.placeholder(registryKey, this.makeKey(path));
    }

    /**
     * register any type of registry entry with a path
     *
     * @param registryKey key for registry to register to
     * @param path        path for new entry
     * @param supplier    supplier for entry to register
     * @param <T>         registry type
     * @return registry object for <code>entry</code>
     */
    <T> RegistryReference<T> register(final ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier);

    /**
     * register block entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<Block> registerBlock(String path, Supplier<Block> entry) {
        return this.register(Registries.BLOCK, path, entry);
    }

    /**
     * register item entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<Item> registerItem(String path, Supplier<Item> entry) {
        return this.register(Registries.ITEM, path, entry);
    }

    /**
     * Registers a block item for a given block.
     *
     * @param blockReference reference for block to register item variant for
     * @return registry object for the new block item
     */
    default RegistryReference<Item> registerBlockItem(RegistryReference<Block> blockReference) {
        return this.registerBlockItem(blockReference, new Item.Properties());
    }

    /**
     * Registers a block item for a given block.
     *
     * @param blockReference reference for block to register item variant for
     * @param itemProperties properties for item
     * @return registry object for the new block item
     */
    default RegistryReference<Item> registerBlockItem(RegistryReference<Block> blockReference, Item.Properties itemProperties) {
        return this.registerItem(blockReference.getResourceLocation().getPath(), () -> new BlockItem(blockReference.get(), itemProperties));
    }

    /**
     * Registers a spawn egg item for a given entity type.
     *
     * @param entityTypeReference reference for the entity type to register a spawn egg for
     * @param backgroundColor     background color of the spawn egg item
     * @param highlightColor      spots color pf the spawn egg item
     * @return registry reference for the new spawn egg item
     */
    default RegistryReference<Item> registerSpawnEggItem(RegistryReference<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor) {
        return this.registerSpawnEggItem(entityTypeReference, backgroundColor, highlightColor, new Item.Properties());
    }

    /**
     * Registers a spawn egg item for a given entity type.
     *
     * @param entityTypeReference reference for the entity type to register a spawn egg for
     * @param backgroundColor     background color of the spawn egg item
     * @param highlightColor      spots color pf the spawn egg item
     * @param itemProperties      properties for the item
     * @return registry reference for the new spawn egg item
     */
    RegistryReference<Item> registerSpawnEggItem(RegistryReference<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor, Item.Properties itemProperties);

    /**
     * register fluid entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<Fluid> registerFluid(String path, Supplier<Fluid> entry) {
        return this.register(Registries.FLUID, path, entry);
    }

    /**
     * register mob effect entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<MobEffect> registerMobEffect(String path, Supplier<MobEffect> entry) {
        return this.register(Registries.MOB_EFFECT, path, entry);
    }

    /**
     * register sound event entry with a path
     *
     * @param path path for new entry
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<SoundEvent> registerSoundEvent(String path) {
        return this.register(Registries.SOUND_EVENT, path, () -> SoundEvent.createVariableRangeEvent(this.makeKey(path)));
    }

    /**
     * register potion entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<Potion> registerPotion(String path, Supplier<Potion> entry) {
        return this.register(Registries.POTION, path, entry);
    }

    /**
     * register enchantment entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<Enchantment> registerEnchantment(String path, Supplier<Enchantment> entry) {
        return this.register(Registries.ENCHANTMENT, path, entry);
    }

    /**
     * register entity type entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @param <T>   entity type parameter
     * @return registry object for <code>entry</code>
     */
    @SuppressWarnings("unchecked")
    default <T extends Entity> RegistryReference<EntityType<T>> registerEntityType(String path, Supplier<EntityType.Builder<T>> entry) {
        return this.register((ResourceKey<Registry<EntityType<T>>>) (ResourceKey<?>) Registries.ENTITY_TYPE, path, () -> entry.get().build(path));
    }

    /**
     * register tile entity type entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @param <T>   block entity type parameter
     * @return registry object for <code>entry</code>
     */
    @SuppressWarnings("unchecked")
    default <T extends BlockEntity> RegistryReference<BlockEntityType<T>> registerBlockEntityType(String path, Supplier<BlockEntityType.Builder<T>> entry) {
        return this.register((ResourceKey<Registry<BlockEntityType<T>>>) (ResourceKey<?>) Registries.BLOCK_ENTITY_TYPE, path, () -> entry.get().build(null));
    }

    /**
     * register container type entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @param <T>   container menu type parameter
     * @return registry object for <code>entry</code>
     */
    @SuppressWarnings("unchecked")
    default <T extends AbstractContainerMenu> RegistryReference<MenuType<T>> registerMenuType(String path, Supplier<MenuType.MenuSupplier<T>> entry) {
        return this.register((ResourceKey<Registry<MenuType<T>>>) (ResourceKey<?>) Registries.MENU, path, () -> new MenuType<>(entry.get(), FeatureFlags.VANILLA_SET));
    }

    /**
     * register container type entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @param <T>   container menu type
     * @return registry object for <code>entry</code>
     */
    <T extends AbstractContainerMenu> RegistryReference<MenuType<T>> registerExtendedMenuType(String path, Supplier<ExtendedMenuSupplier<T>> entry);

    /**
     * register poi type from custom builder
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return registry object for <code>entry</code>
     */
    RegistryReference<PoiType> registerPoiTypeBuilder(String path, Supplier<PoiTypeBuilder> entry);

    default RegistryReference<PoiType> registerPoiType(String path, int maxTickets, int validRange, Block... blocks) {
        return this.registerPoiType(path, maxTickets, validRange, Stream.of(blocks).flatMap(t -> t.getStateDefinition().getPossibleStates().stream()).collect(Collectors.toSet()));
    }

    RegistryReference<PoiType> registerPoiType(String path, int maxTickets, int validRange, Set<BlockState> matchingStates);

    /**
     * register a new type of recipe
     *
     * @param path path for new entry
     * @param <T>  recipe type
     * @return registry object for <code>entry</code>
     */
    default <T extends Recipe<?>> RegistryReference<RecipeType<T>> registerRecipeType(String path) {
        return this.register(Registries.RECIPE_TYPE, path, () -> new RecipeType<>() {
            private final String id = RegistryManager.this.makeKey(path).toString();

            @Override
            public String toString() {
                return this.id;
            }
        });
    }

    /**
     * register a new game event that can be listened to
     *
     * @param path               path for new entry
     * @param notificationRadius range in blocks in which this event will be listened to
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<GameEvent> registerGameEvent(String path, int notificationRadius) {
        return this.register(Registries.GAME_EVENT, path, () -> new GameEvent(path, notificationRadius));
    }

    /**
     * Register a new simple particle type.
     *
     * @param path path for new entry
     * @return registry object for <code>entry</code>
     */
    default RegistryReference<SimpleParticleType> registerParticleType(String path) {
        return this.register(Registries.PARTICLE_TYPE, path, () -> new SimpleParticleType(false));
    }

    /**
     * Creates a new {@link TagKey} for any type of registry from a given path.
     *
     * @param registryKey key for registry to create key from
     * @param path        path for new tag key
     * @param <T>         registry type
     * @return new tag key
     */
    default <T> TagKey<T> registerTag(final ResourceKey<? extends Registry<T>> registryKey, String path) {
        return TagKey.create(registryKey, this.makeKey(path));
    }

    /**
     * Creates a new {@link TagKey} for blocks.
     *
     * @param path path for new tag key
     * @return new tag key
     */
    default TagKey<Block> registerBlockTag(String path) {
        return this.registerTag(Registries.BLOCK, path);
    }

    /**
     * Creates a new {@link TagKey} for items.
     *
     * @param path path for new tag key
     * @return new tag key
     */
    default TagKey<Item> registerItemTag(String path) {
        return this.registerTag(Registries.ITEM, path);
    }

    /**
     * Creates a new {@link TagKey} for entity types.
     *
     * @param path path for new tag key
     * @return new tag key
     */
    default TagKey<EntityType<?>> registerEntityTypeTag(String path) {
        return this.registerTag(Registries.ENTITY_TYPE, path);
    }

    /**
     * Creates a new {@link TagKey} for game events.
     *
     * @param path path for new tag key
     * @return new tag key
     */
    default TagKey<GameEvent> registerGameEventTag(String path) {
        return this.registerTag(Registries.GAME_EVENT, path);
    }

    /**
     * Creates a new {@link TagKey} for damage types.
     *
     * @param path path for new tag key
     * @return new tag key
     */
    default TagKey<DamageType> registerDamageTypeTag(String path) {
        return this.registerTag(Registries.DAMAGE_TYPE, path);
    }

    /**
     * Creates a new {@link TagKey} for enchantments.
     *
     * @param path path for new tag key
     * @return new tag key
     */
    default TagKey<Enchantment> registerEnchantmentTag(String path) {
        return this.registerTag(Registries.ENCHANTMENT, path);
    }

    /**
     * Creates a new {@link TagKey} for biomes.
     *
     * @param path path for new tag key
     * @return new tag key
     */
    default TagKey<Biome> registerBiomeTag(String path) {
        return this.registerTag(Registries.BIOME, path);
    }

    /**
     * Creates a new {@link ResourceKey} for a provided registry from the given <code>path</code>.
     * <p>Ideally used for content loading in via a dynamic registry.
     *
     * @param registryKey key for registry to create {@link ResourceKey} for
     * @param path        path for new entry
     * @param <T>         registry type
     * @return {@link ResourceKey} for <code>entry</code>
     */
    default <T> ResourceKey<T> registerResourceKey(final ResourceKey<? extends Registry<T>> registryKey, String path) {
        return ResourceKey.create(registryKey, this.makeKey(path));
    }

    /**
     * Creates a new {@link ResourceKey} for a {@link DamageType}.
     *
     * @param path path for new resource key
     * @return new {@link ResourceKey}
     */
    default ResourceKey<DamageType> registerDamageType(String path) {
        return this.registerResourceKey(Registries.DAMAGE_TYPE, path);
    }

    /**
     * @param path path for location
     * @return resource location for {@link #namespace}
     */
    default ResourceLocation makeKey(String path) {
        if (StringUtils.isEmpty(path)) throw new IllegalArgumentException("Can't register object without name");
        return new ResourceLocation(this.namespace(), path);
    }
}
