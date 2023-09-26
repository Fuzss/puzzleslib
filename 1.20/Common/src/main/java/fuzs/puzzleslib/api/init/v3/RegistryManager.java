package fuzs.puzzleslib.api.init.v3;

import com.google.common.collect.Sets;
import fuzs.puzzleslib.api.core.v1.ModLoader;
import fuzs.puzzleslib.api.init.v2.builder.ExtendedMenuSupplier;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Handles registering to game registries. Registration is performed instantly on Fabric and is deferred on Forge.
 */
public interface RegistryManager {

    /**
     * Creates a new registry manager for <code>modId</code> or returns an existing one.
     *
     * @param modId namespace used for registration
     * @return mod specific registry manager
     */
    static RegistryManager from(String modId) {
        return ModContext.get(modId).getRegistryManagerV3();
    }

    /**
     * Creates a new {@link ResourceKey} for a provided registry from the given <code>path</code>.
     *
     * @param registryKey key for registry to create {@link ResourceKey} for
     * @param path        path for new entry
     * @param <T>         registry type
     * @return {@link ResourceKey} for <code>path</code>
     */
    @SuppressWarnings("unchecked")
    default <T> ResourceKey<T> makeResourceKey(ResourceKey<? extends Registry<? super T>> registryKey, String path) {
        return ResourceKey.create((ResourceKey<Registry<T>>) registryKey, this.makeKey(path));
    }

    /**
     * @param path path for location
     * @return resource location for set namespace
     */
    ResourceLocation makeKey(String path);

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
        Objects.checkIndex(0, forbiddenModLoaders.length);
        return this.whenOn(EnumSet.complementOf(Sets.newEnumSet(Arrays.asList(forbiddenModLoaders), ModLoader.class)).toArray(ModLoader[]::new));
    }

    /**
     * Creates a placeholder registry reference.
     *
     * @param registryKey key for registry to register to
     * @param path        path for new entry
     * @param <T>         registry type
     * @return new placeholder registry object
     */
    <T> Holder.Reference<T> getHolder(ResourceKey<? extends Registry<? super T>> registryKey, String path);

    /**
     * register any type of registry entry with a path
     *
     * @param registryKey key for registry to register to
     * @param path        path for new entry
     * @param supplier    supplier for entry to register
     * @param <T>         registry type
     * @return new registry object
     */
    <T> Holder.Reference<T> register(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier);

    /**
     * register block entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return new registry object
     */
    default Holder.Reference<Block> registerBlock(String path, Supplier<Block> entry) {
        return this.register(Registries.BLOCK, path, entry);
    }

    /**
     * register item entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return new registry object
     */
    default Holder.Reference<Item> registerItem(String path, Supplier<Item> entry) {
        return this.register(Registries.ITEM, path, entry);
    }

    /**
     * Registers a block item for a given block.
     *
     * @param blockReference reference for block to register item variant for
     * @return registry object for the new block item
     */
    default Holder.Reference<Item> registerBlockItem(Holder.Reference<Block> blockReference) {
        return this.registerBlockItem(blockReference, new Item.Properties());
    }

    /**
     * Registers a block item for a given block.
     *
     * @param blockReference reference for block to register item variant for
     * @param itemProperties properties for item
     * @return registry object for the new block item
     */
    default Holder.Reference<Item> registerBlockItem(Holder.Reference<Block> blockReference, Item.Properties itemProperties) {
        return this.registerItem(blockReference.key().location().getPath(), () -> new BlockItem(blockReference.value(), itemProperties));
    }

    /**
     * Registers a spawn egg item for a given entity type.
     *
     * @param entityTypeReference reference for the entity type to register a spawn egg for
     * @param backgroundColor     background color of the spawn egg item
     * @param highlightColor      spots color pf the spawn egg item
     * @return registry reference for the new spawn egg item
     */
    default Holder.Reference<Item> registerSpawnEggItem(Holder.Reference<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor) {
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
    Holder.Reference<Item> registerSpawnEggItem(Holder.Reference<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor, Item.Properties itemProperties);

    /**
     * register fluid entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return new registry object
     */
    default Holder.Reference<Fluid> registerFluid(String path, Supplier<Fluid> entry) {
        return this.register(Registries.FLUID, path, entry);
    }

    /**
     * register mob effect entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return new registry object
     */
    default Holder.Reference<MobEffect> registerMobEffect(String path, Supplier<MobEffect> entry) {
        return this.register(Registries.MOB_EFFECT, path, entry);
    }

    /**
     * register sound event entry with a path
     *
     * @param path path for new entry
     * @return new registry object
     */
    default Holder.Reference<SoundEvent> registerSoundEvent(String path) {
        return this.register(Registries.SOUND_EVENT, path, () -> SoundEvent.createVariableRangeEvent(this.makeKey(path)));
    }

    /**
     * register potion entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return new registry object
     */
    default Holder.Reference<Potion> registerPotion(String path, Supplier<Potion> entry) {
        return this.register(Registries.POTION, path, entry);
    }

    /**
     * register enchantment entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return new registry object
     */
    default Holder.Reference<Enchantment> registerEnchantment(String path, Supplier<Enchantment> entry) {
        return this.register(Registries.ENCHANTMENT, path, entry);
    }

    /**
     * register entity type entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @param <T>   entity type parameter
     * @return new registry object
     */
    @SuppressWarnings("unchecked")
    default <T extends Entity> Holder.Reference<EntityType<T>> registerEntityType(String path, Supplier<EntityType.Builder<T>> entry) {
        return this.register((ResourceKey<Registry<EntityType<T>>>) (ResourceKey<?>) Registries.ENTITY_TYPE, path, () -> entry.get().build(path));
    }

    /**
     * register tile entity type entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @param <T>   block entity type parameter
     * @return new registry object
     */
    @SuppressWarnings("unchecked")
    default <T extends BlockEntity> Holder.Reference<BlockEntityType<T>> registerBlockEntityType(String path, Supplier<BlockEntityType.Builder<T>> entry) {
        return this.register((ResourceKey<Registry<BlockEntityType<T>>>) (ResourceKey<?>) Registries.BLOCK_ENTITY_TYPE, path, () -> entry.get().build(null));
    }

    /**
     * register container type entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @param <T>   container menu type parameter
     * @return new registry object
     */
    @SuppressWarnings("unchecked")
    default <T extends AbstractContainerMenu> Holder.Reference<MenuType<T>> registerMenuType(String path, Supplier<MenuType.MenuSupplier<T>> entry) {
        return this.register((ResourceKey<Registry<MenuType<T>>>) (ResourceKey<?>) Registries.MENU, path, () -> new MenuType<>(entry.get(), FeatureFlags.DEFAULT_FLAGS));
    }

    /**
     * register container type entry with a path
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @param <T>   container menu type
     * @return new registry object
     */
    <T extends AbstractContainerMenu> Holder.Reference<MenuType<T>> registerExtendedMenuType(String path, Supplier<ExtendedMenuSupplier<T>> entry);

    /**
     * Creates and registers a new poi type entry.
     *
     * @param path   path for new entry
     * @param blocks blocks valid for this poi type
     * @return new registry object
     */
    default Holder.Reference<PoiType> registerPoiType(String path, Supplier<Set<Block>> blocks) {
        return this.registerPoiType(path, () -> blocks.get().stream().flatMap(t -> t.getStateDefinition().getPossibleStates().stream()).collect(Collectors.toSet()), 0, 1);
    }

    /**
     * Creates and registers a new poi type entry.
     *
     * @param path           path for new entry
     * @param matchingStates blocks states valid for this poi type
     * @param maxTickets     max amount of accessor tickets
     * @param validRange     distance to search for this poi type
     * @return new registry object
     */
    Holder.Reference<PoiType> registerPoiType(String path, Supplier<Set<BlockState>> matchingStates, int maxTickets, int validRange);

    /**
     * register a new type of recipe
     *
     * @param path path for new entry
     * @param <T>  recipe type
     * @return new registry object
     */
    default <T extends Recipe<?>> Holder.Reference<RecipeType<T>> registerRecipeType(String path) {
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
     * @return new registry object
     */
    default Holder.Reference<GameEvent> registerGameEvent(String path, int notificationRadius) {
        return this.register(Registries.GAME_EVENT, path, () -> new GameEvent(path, notificationRadius));
    }

    /**
     * Register a new simple particle type.
     *
     * @param path path for new entry
     * @return new registry object
     */
    default Holder.Reference<SimpleParticleType> registerParticleType(String path) {
        return this.register(Registries.PARTICLE_TYPE, path, () -> new SimpleParticleType(false));
    }

    /**
     * Creates a new {@link ResourceKey} for a {@link DamageType}.
     *
     * @param path path for new resource key
     * @return new {@link ResourceKey}
     */
    default ResourceKey<DamageType> registerDamageType(String path) {
        return this.makeResourceKey(Registries.DAMAGE_TYPE, path);
    }
}
