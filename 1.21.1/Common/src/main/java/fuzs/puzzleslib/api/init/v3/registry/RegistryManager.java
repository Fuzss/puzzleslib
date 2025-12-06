package fuzs.puzzleslib.api.init.v3.registry;

import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.serialization.MapCodec;
import fuzs.puzzleslib.api.core.v1.utility.EnvironmentAwareBuilder;
import fuzs.puzzleslib.api.item.v2.ItemEquipmentFactories;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.item.CreativeModeTabHelper;
import net.minecraft.Util;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Handles registering to game registries. Registration is performed instantly on Fabric and is deferred on Forge.
 */
public interface RegistryManager extends EnvironmentAwareBuilder<RegistryManager> {

    /**
     * Creates a new registry manager for <code>modId</code> or returns an existing one.
     *
     * @param modId namespace used for registration
     * @return mod specific registry manager
     */
    static RegistryManager from(String modId) {
        return ModContext.get(modId).getRegistryManager();
    }

    /**
     * Creates a new {@link ResourceKey} for a provided registry from the given path.
     *
     * @param registryKey key for registry to create {@link ResourceKey} for
     * @param path        path for new entry
     * @param <T>         registry type
     * @return {@link ResourceKey} for path
     */
    @SuppressWarnings("unchecked")
    default <T> ResourceKey<T> makeResourceKey(ResourceKey<? extends Registry<? super T>> registryKey, String path) {
        return ResourceKey.create((ResourceKey<Registry<T>>) registryKey, this.makeKey(path));
    }

    /**
     * Create a new {@link ResourceLocation} for the set mod id.
     *
     * @param path path for location
     * @return resource location for set namespace
     */
    ResourceLocation makeKey(String path);

    /**
     * Creates a new description id used in translations for a provided registry from the given path.
     *
     * @param registryKey key for registry to create description id for
     * @param path        path for new entry
     * @return the description id
     */
    default String makeDescriptionId(ResourceKey<? extends Registry<?>> registryKey, String path) {
        return Util.makeDescriptionId(Registries.elementsDirPath(registryKey), this.makeKey(path));
    }

    /**
     * Creates a lazy holder reference that will update when used.
     *
     * @param registryKey key for registry to register to
     * @param path        path for new entry
     * @param <T>         registry type
     * @return new placeholder registry object
     */
    <T> Holder.Reference<T> registerLazily(ResourceKey<? extends Registry<? super T>> registryKey, String path);

    /**
     * Register a supplied value to a given registry of that type returning a holder reference.
     *
     * @param registryKey key for registry to register to
     * @param path        path for new entry
     * @param supplier    supplier for entry to register
     * @param <T>         registry type
     * @return holder reference
     */
    <T> Holder.Reference<T> register(ResourceKey<? extends Registry<? super T>> registryKey, String path, Supplier<T> supplier);

    /**
     * Register a block.
     *
     * @param path                    path for new entry
     * @param blockPropertiesSupplier supplier for block properties
     * @return holder reference
     */
    default Holder.Reference<Block> registerSimpleBlock(String path, Supplier<BlockBehaviour.Properties> blockPropertiesSupplier) {
        return this.registerBlock(path, Block::new, blockPropertiesSupplier);
    }

    /**
     * Register a block.
     *
     * @param path                    path for new entry
     * @param factory                 factory for new block
     * @param blockPropertiesSupplier supplier for block properties
     * @return holder reference
     */
    default Holder.Reference<Block> registerBlock(String path, Function<BlockBehaviour.Properties, Block> factory, Supplier<BlockBehaviour.Properties> blockPropertiesSupplier) {
        return this.register(Registries.BLOCK, path, () -> factory.apply(blockPropertiesSupplier.get()));
    }

    /**
     * Register an item.
     *
     * @param path path for new entry
     * @return holder reference
     */
    default Holder.Reference<Item> registerItem(String path) {
        return this.registerSimpleItem(path, Item.Properties::new);
    }

    /**
     * Register an item.
     *
     * @param path                   path for new entry
     * @param itemPropertiesSupplier supplier for new item properties
     * @return holder reference
     */
    default Holder.Reference<Item> registerSimpleItem(String path, Supplier<Item.Properties> itemPropertiesSupplier) {
        return this.registerItem(path, Item::new, itemPropertiesSupplier);
    }

    /**
     * Register an item.
     *
     * @param path    path for new entry
     * @param factory factory for new item
     * @return holder reference
     */
    default Holder.Reference<Item> registerItem(String path, Function<Item.Properties, Item> factory) {
        return this.registerItem(path, factory, Item.Properties::new);
    }

    /**
     * Register an item.
     *
     * @param path                   path for new entry
     * @param factory                factory for new item
     * @param itemPropertiesSupplier supplier for new item properties
     * @return holder reference
     */
    default Holder.Reference<Item> registerItem(String path, Function<Item.Properties, Item> factory, Supplier<Item.Properties> itemPropertiesSupplier) {
        return this.register(Registries.ITEM, path, () -> factory.apply(itemPropertiesSupplier.get()));
    }

    /**
     * Registers a block item for a block.
     *
     * @param block reference for block to register item variant for
     * @return holder reference
     */
    default Holder.Reference<Item> registerBlockItem(Holder<Block> block) {
        return this.registerBlockItem(block, Item.Properties::new);
    }

    /**
     * Registers a block item for a block.
     *
     * @param block                  reference for block to register item variant for
     * @param itemPropertiesSupplier supplier for new item properties
     * @return holder reference
     */
    default Holder.Reference<Item> registerBlockItem(Holder<Block> block, Supplier<Item.Properties> itemPropertiesSupplier) {
        return this.registerBlockItem(block, BlockItem::new, itemPropertiesSupplier);
    }

    /**
     * Registers a block item for a block.
     *
     * @param block       reference for block to register item variant for
     * @param itemFactory factory for new item
     * @return holder reference
     */
    default Holder.Reference<Item> registerBlockItem(Holder<Block> block, BiFunction<Block, Item.Properties, ? extends BlockItem> itemFactory) {
        return this.registerBlockItem(block, itemFactory, Item.Properties::new);
    }

    /**
     * Registers a block item for a block.
     *
     * @param block                  reference for block to register item variant for
     * @param factory                factory for new item
     * @param itemPropertiesSupplier supplier for new item properties
     * @return holder reference
     */
    default Holder.Reference<Item> registerBlockItem(Holder<Block> block, BiFunction<Block, Item.Properties, ? extends BlockItem> factory, Supplier<Item.Properties> itemPropertiesSupplier) {
        return this.registerItem(block.unwrapKey().orElseThrow().location().getPath(),
                (Item.Properties itemProperties) -> {
                    return factory.apply(block.value(), itemProperties);
                },
                itemPropertiesSupplier);
    }

    /**
     * Register a block.
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return holder reference
     */
    @Deprecated
    default Holder.Reference<Block> registerBlock(String path, Supplier<Block> entry) {
        return this.register(Registries.BLOCK, path, entry);
    }

    /**
     * Register an item.
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return holder reference
     */
    @Deprecated
    default Holder.Reference<Item> registerItem(String path, Supplier<Item> entry) {
        return this.register(Registries.ITEM, path, entry);
    }

    /**
     * Registers a block item for a block.
     *
     * @param blockReference reference for block to register item variant for
     * @param itemProperties properties for item
     * @return holder reference
     */
    @Deprecated
    default Holder.Reference<Item> registerBlockItem(Holder<Block> blockReference, Item.Properties itemProperties) {
        return this.registerBlockItem(blockReference, () -> itemProperties);
    }

    /**
     * Registers a spawn egg item for an entity type.
     *
     * @param entityTypeHolder the entity type holder
     * @return the holder reference
     */
    default Holder.Reference<Item> registerSpawnEggItem(Holder<? extends EntityType<? extends Mob>> entityTypeHolder) {
        return this.registerLegacySpawnEggItem(entityTypeHolder, -1, -1);
    }

    /**
     * Registers a spawn egg item for an entity type.
     *
     * @param entityTypeHolder the entity type holder
     * @param backgroundColor  the background color for the spawn egg texture
     * @return the holder reference
     */
    Holder.Reference<Item> registerLegacySpawnEggItem(Holder<? extends EntityType<? extends Mob>> entityTypeHolder, int backgroundColor);

    /**
     * Registers a spawn egg item for an entity type.
     *
     * @param entityTypeHolder the entity type holder
     * @param backgroundColor  the background color for the spawn egg texture
     * @param highlightColor   the spots color for the spawn egg texture
     * @return the holder reference
     */
    Holder.Reference<Item> registerLegacySpawnEggItem(Holder<? extends EntityType<? extends Mob>> entityTypeHolder, int backgroundColor, int highlightColor);

    /**
     * Registers a spawn egg item for an entity type.
     *
     * @param entityTypeReference reference for the entity type to register a spawn egg for
     * @param backgroundColor     background color of the spawn egg item
     * @param highlightColor      spots color pf the spawn egg item
     * @return holder reference
     */
    @Deprecated
    default Holder.Reference<Item> registerSpawnEggItem(Holder<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor) {
        return this.registerLegacySpawnEggItem(entityTypeReference, backgroundColor, highlightColor);
    }

    /**
     * Registers a spawn egg item for an entity type.
     *
     * @param entityTypeReference reference for the entity type to register a spawn egg for
     * @param backgroundColor     background color of the spawn egg item
     * @param highlightColor      spots color pf the spawn egg item
     * @param itemProperties      properties for the item
     * @return holder reference
     */
    @Deprecated
    default Holder.Reference<Item> registerSpawnEggItem(Holder<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor, Item.Properties itemProperties) {
        return this.registerLegacySpawnEggItem(entityTypeReference, backgroundColor, highlightColor);
    }

    /**
     * Register a creative mode tab.
     *
     * @param iconHolder the tab icon item stack
     * @return the holder reference
     */
    default Holder.Reference<CreativeModeTab> registerCreativeModeTab(Holder<? extends ItemLike> iconHolder) {
        return this.registerCreativeModeTab(() -> new ItemStack(iconHolder.value()));
    }

    /**
     * Register a creative mode tab.
     *
     * @param iconSupplier the tab icon item stack
     * @return the holder reference
     */
    default Holder.Reference<CreativeModeTab> registerCreativeModeTab(Supplier<ItemStack> iconSupplier) {
        ResourceLocation resourceLocation = this.makeKey("main");
        return this.registerCreativeModeTab(resourceLocation.getPath(),
                iconSupplier,
                CreativeModeTabHelper.getDisplayItems(resourceLocation.getNamespace()),
                false);
    }

    /**
     * Register a creative mode tab.
     *
     * @param iconSupplier the tab icon item stack
     * @param displayItems the display items generator
     * @return the holder reference
     */
    default Holder.Reference<CreativeModeTab> registerCreativeModeTab(Supplier<ItemStack> iconSupplier, CreativeModeTab.DisplayItemsGenerator displayItems) {
        return this.registerCreativeModeTab("main", iconSupplier, displayItems, false);
    }

    /**
     * Register a creative mode tab.
     *
     * @param path          path for new entry
     * @param iconSupplier  the tab icon item stack
     * @param displayItems  the display items generator
     * @param withSearchBar should the tab include a search bar (only supported for NeoForge)
     * @return the holder reference
     */
    Holder.Reference<CreativeModeTab> registerCreativeModeTab(String path, Supplier<ItemStack> iconSupplier, CreativeModeTab.DisplayItemsGenerator displayItems, boolean withSearchBar);

    /**
     * Register a data component type.
     *
     * @param path     path for new entry
     * @param operator supplier for entry to register
     * @return holder reference
     */
    default <T> Holder.Reference<DataComponentType<T>> registerDataComponentType(String path, UnaryOperator<DataComponentType.Builder<T>> operator) {
        return this.register(Registries.DATA_COMPONENT_TYPE,
                path,
                () -> operator.apply(DataComponentType.builder()).build());
    }

    /**
     * Register a fluid.
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return holder reference
     */
    default Holder.Reference<Fluid> registerFluid(String path, Supplier<Fluid> entry) {
        return this.register(Registries.FLUID, path, entry);
    }

    /**
     * Register a mob effect.
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return holder reference
     */
    default Holder.Reference<MobEffect> registerMobEffect(String path, Supplier<MobEffect> entry) {
        return this.register(Registries.MOB_EFFECT, path, entry);
    }

    /**
     * Register a sound event.
     *
     * @param path path for new entry
     * @return holder reference
     */
    default Holder.Reference<SoundEvent> registerSoundEvent(String path) {
        return this.register(Registries.SOUND_EVENT, path, () -> {
            return SoundEvent.createVariableRangeEvent(this.makeKey(path));
        });
    }

    /**
     * Register a potion.
     *
     * @param path           the registered name
     * @param potionSupplier supplier for entry to register
     * @return the holder reference
     */
    default Holder.Reference<Potion> registerPotion(String path, Supplier<Potion> potionSupplier) {
        return this.registerPotion(path, (String name) -> potionSupplier.get());
    }

    /**
     * Register a potion.
     *
     * @param path          the registered name
     * @param potionFactory function for entry to register, receiving the passed path parameter
     * @return the holder reference
     */
    default Holder.Reference<Potion> registerPotion(String path, Function<String, Potion> potionFactory) {
        return this.register(Registries.POTION, path, () -> potionFactory.apply(path));
    }

    /**
     * Register an enchantment.
     *
     * @param path path for new entry
     * @return holder reference
     */
    default ResourceKey<Enchantment> registerEnchantment(String path) {
        return this.makeResourceKey(Registries.ENCHANTMENT, path);
    }

    /**
     * Register an enchantment effect component type.
     *
     * @param path     the registered name
     * @param operator the supplier for entry to register
     * @param <T>      the component type
     * @return the holder reference
     */
    default <T> Holder.Reference<DataComponentType<T>> registerEnchantmentEffectComponentType(String path, UnaryOperator<DataComponentType.Builder<T>> operator) {
        return this.register(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE,
                path,
                () -> operator.apply(DataComponentType.builder()).build());
    }

    /**
     * Register an entity type.
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @param <T>   entity type parameter
     * @return holder reference
     */
    @SuppressWarnings("unchecked")
    default <T extends Entity> Holder.Reference<EntityType<T>> registerEntityType(String path, Supplier<EntityType.Builder<T>> entry) {
        return this.register((ResourceKey<Registry<EntityType<T>>>) (ResourceKey<?>) Registries.ENTITY_TYPE,
                path,
                () -> {
                    return entry.get().build(path);
                });
    }

    /**
     * Register a block entity type.
     *
     * @param path               the registered name
     * @param blockEntityFactory factory for every newly created block entity instance
     * @param validBlock         block allowed to use this block entity
     * @param <T>                block entity type parameter
     * @return the holder reference
     */
    default <T extends BlockEntity> Holder.Reference<BlockEntityType<T>> registerBlockEntityType(String path, BiFunction<BlockPos, BlockState, T> blockEntityFactory, Holder<Block> validBlock) {
        return this.registerBlockEntityType(path, blockEntityFactory, () -> Collections.singleton(validBlock.value()));
    }

    /**
     * Register a block entity type.
     *
     * @param path               the registered name
     * @param blockEntityFactory factory for every newly created block entity instance
     * @param validBlocks        blocks allowed to use this block entity
     * @param <T>                block entity type parameter
     * @return the holder reference
     */
    default <T extends BlockEntity> Holder.Reference<BlockEntityType<T>> registerBlockEntityType(String path, BiFunction<BlockPos, BlockState, T> blockEntityFactory, Supplier<Set<Block>> validBlocks) {
        return this.register((ResourceKey<Registry<BlockEntityType<T>>>) (ResourceKey<?>) Registries.BLOCK_ENTITY_TYPE,
                path,
                () -> {
                    return BlockEntityType.Builder.of(blockEntityFactory::apply,
                            validBlocks.get().toArray(Block[]::new)).build(null);
                });
    }

    /**
     * Register a block entity type.
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @param <T>   block entity type parameter
     * @return holder reference
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    default <T extends BlockEntity> Holder.Reference<BlockEntityType<T>> registerBlockEntityType(String path, Supplier<BlockEntityType.Builder<T>> entry) {
        return this.register((ResourceKey<Registry<BlockEntityType<T>>>) (ResourceKey<?>) Registries.BLOCK_ENTITY_TYPE,
                path,
                () -> {
                    return entry.get().build(null);
                });
    }

    /**
     * Register a menu type.
     *
     * @param path         the registered name
     * @param menuSupplier the menu supplier
     * @param <T>          the menu type
     * @return the holder reference
     */
    @SuppressWarnings("unchecked")
    default <T extends AbstractContainerMenu> Holder.Reference<MenuType<T>> registerMenuType(String path, MenuType.MenuSupplier<T> menuSupplier) {
        return this.register((ResourceKey<Registry<MenuType<T>>>) (ResourceKey<?>) Registries.MENU, path, () -> {
            return new MenuType<>(menuSupplier, FeatureFlags.DEFAULT_FLAGS);
        });
    }

    /**
     * Register a menu type with additional data for constructing the menu on the client.
     *
     * @param path         the registered name
     * @param menuSupplier the menu supplier
     * @param streamCodec  the stream codec for additional data
     * @param <T>          the menu type
     * @param <S>          the data type
     * @return the holder reference
     */
    <T extends AbstractContainerMenu, S> Holder.Reference<MenuType<T>> registerMenuType(String path, MenuSupplierWithData<T, S> menuSupplier, StreamCodec<? super RegistryFriendlyByteBuf, S> streamCodec);

    /**
     * Register a menu type.
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @param <T>   container menu type parameter
     * @return holder reference
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    default <T extends AbstractContainerMenu> Holder.Reference<MenuType<T>> registerMenuType(String path, Supplier<MenuType.MenuSupplier<T>> entry) {
        return this.register((ResourceKey<Registry<MenuType<T>>>) (ResourceKey<?>) Registries.MENU, path, () -> {
            return new MenuType<>(entry.get(), FeatureFlags.DEFAULT_FLAGS);
        });
    }

    /**
     * Register a menu type.
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @param <T>   container menu type
     * @return holder reference
     */
    @Deprecated
    <T extends AbstractContainerMenu> Holder.Reference<MenuType<T>> registerExtendedMenuType(String path, Supplier<ExtendedMenuSupplier<T>> entry);

    /**
     * Register a poi type.
     *
     * @param path  path for new entry
     * @param block block valid for this poi type
     * @return holder reference
     */
    default Holder.Reference<PoiType> registerPoiType(String path, Holder<Block> matchingBlock) {
        return this.registerSetPoiType(path, () -> Collections.singleton(matchingBlock.value()));
    }

    /**
     * Register a poi type.
     *
     * @param path           the registered name
     * @param matchingBlocks blocks valid for this poi type
     * @return the holder reference
     */
    default Holder.Reference<PoiType> registerSetPoiType(String path, Supplier<Set<Block>> matchingBlocks) {
        return this.registerPoiType(path, 0, 1, () -> {
            return matchingBlocks.get()
                    .stream()
                    .flatMap((Block block) -> block.getStateDefinition().getPossibleStates().stream())
                    .collect(ImmutableSet.toImmutableSet());
        });
    }

    /**
     * Register a poi type.
     *
     * @param path                the registered name
     * @param maxTickets          max amount of accessor tickets
     * @param validRange          distance to search for this poi type
     * @param matchingBlockStates blocks states valid for this poi type
     * @return the holder reference
     */
    Holder.Reference<PoiType> registerPoiType(String path, int maxTickets, int validRange, Supplier<Set<BlockState>> matchingBlockStates);

    /**
     * Register a poi type.
     *
     * @param path  path for new entry
     * @param block block valid for this poi type
     * @return holder reference
     */
    @Deprecated
    default Holder.Reference<PoiType> registerPoiType(String path, Supplier<Block> matchingBlock) {
        return this.registerSetPoiType(path, () -> Collections.singleton(matchingBlock.get()));
    }

    /**
     * Register a poi type.
     *
     * @param path           path for new entry
     * @param matchingStates blocks states valid for this poi type
     * @param maxTickets     max amount of accessor tickets
     * @param validRange     distance to search for this poi type
     * @return holder reference
     */
    @Deprecated
    default Holder.Reference<PoiType> registerPoiType(String path, Supplier<Set<BlockState>> matchingStates, int maxTickets, int validRange) {
        return this.registerPoiType(path, maxTickets, validRange, matchingStates);
    }

    /**
     * Register an argument type.
     *
     * @param path          path for new entry
     * @param argumentClass argument type class
     * @param argumentType  argument type factory
     * @param <A>           argument type
     * @return holder reference
     */
    default <A extends ArgumentType<?>> Holder.Reference<ArgumentTypeInfo<?, ?>> registerArgumentType(String path, Class<? extends A> argumentClass, Supplier<A> argumentType) {
        return this.registerArgumentType(path, argumentClass, SingletonArgumentInfo.contextFree(argumentType));
    }

    /**
     * Register an argument type.
     *
     * @param path             path for new entry
     * @param argumentClass    argument type class
     * @param argumentTypeInfo argument type info
     * @param <A>              argument type
     * @param <T>              argument type info
     * @return holder reference
     */
    <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> Holder.Reference<ArgumentTypeInfo<?, ?>> registerArgumentType(String path, Class<? extends A> argumentClass, ArgumentTypeInfo<A, T> argumentTypeInfo);

    /**
     * Register a type of recipe.
     *
     * @param path path for new entry
     * @param <T>  recipe type
     * @return holder reference
     */
    default <T extends Recipe<?>> Holder.Reference<RecipeType<T>> registerRecipeType(String path) {
        return this.register(Registries.RECIPE_TYPE, path, () -> {
            ResourceLocation resourceLocation = this.makeKey(path);
            return new RecipeType<>() {
                @Override
                public String toString() {
                    return resourceLocation.toString();
                }
            };
        });
    }

    /**
     * Register a game event.
     *
     * @param path               path for new entry
     * @param notificationRadius range in blocks in which this event will be listened to
     * @return holder reference
     */
    default Holder.Reference<GameEvent> registerGameEvent(String path, int notificationRadius) {
        return this.register(Registries.GAME_EVENT, path, () -> {
            return new GameEvent(notificationRadius);
        });
    }

    /**
     * Register a simple particle type.
     *
     * @param path path for new entry
     * @return holder reference
     */
    default Holder.Reference<SimpleParticleType> registerParticleType(String path) {
        return this.register(Registries.PARTICLE_TYPE, path, () -> {
            return new SimpleParticleType(false);
        });
    }

    /**
     * Register a particle type.
     *
     * @param path              path for new entry
     * @param overrideLimiter   allow this particle to spawn regardless of the global particle limit
     * @param codecGetter       the codec for serialization
     * @param streamCodecGetter the stream codec for network synchronization
     * @param <T>               the particle type
     * @return holder reference
     */
    default <T extends ParticleOptions> Holder.Reference<ParticleType<T>> registerParticleType(String path, boolean overrideLimiter, Function<ParticleType<T>, MapCodec<T>> codecGetter, Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter) {
        return this.register(Registries.PARTICLE_TYPE, path, () -> new ParticleType<T>(overrideLimiter) {
            @Override
            public MapCodec<T> codec() {
                return codecGetter.apply(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodecGetter.apply(this);
            }
        });
    }

    /**
     * Register an attribute.
     *
     * @param path         path for new entry
     * @param defaultValue the default value when no base value is otherwise specified
     * @param minValue     the min value
     * @param maxValue     the max value
     * @return holder reference
     */
    default Holder.Reference<Attribute> registerAttribute(String path, double defaultValue, double minValue, double maxValue) {
        return this.registerAttribute(path, defaultValue, minValue, maxValue, true, Attribute.Sentiment.POSITIVE);
    }

    /**
     * Register an attribute.
     *
     * @param path         path for new entry
     * @param defaultValue the default value when no base value is otherwise specified
     * @param minValue     the min value
     * @param maxValue     the max value
     * @param syncable     is this attribute synced to clients
     * @param sentiment    the sentiment used for the attribute color on tooltips
     * @return holder reference
     */
    default Holder.Reference<Attribute> registerAttribute(String path, double defaultValue, double minValue, double maxValue, boolean syncable, Attribute.Sentiment sentiment) {
        Objects.requireNonNull(sentiment, "sentiment is null");
        return this.register(Registries.ATTRIBUTE, path, () -> {
            return new RangedAttribute(this.makeDescriptionId(Registries.ATTRIBUTE, path),
                    defaultValue,
                    minValue,
                    maxValue).setSyncable(syncable).setSentiment(sentiment);
        });
    }

    /**
     * Register an entity data serializer.
     * <p>
     * Registration to a game registry is only required on NeoForge, therefore a direct holder is returned on other mod
     * loaders.
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return holder reference
     */
    <T> Holder.Reference<EntityDataSerializer<T>> registerEntityDataSerializer(String path, Supplier<EntityDataSerializer<T>> entry);

    /**
     * Register an armor material with default values.
     *
     * @param path       path for new entry
     * @param repairItem the repair material used in an anvil for restoring item durability
     * @return holder reference
     */
    @Deprecated
    default Holder.Reference<ArmorMaterial> registerArmorMaterial(String path, Holder<Item> repairItem) {
        return this.registerArmorMaterial(path, ItemEquipmentFactories.toArmorTypeMapWithFallback(1), 0, repairItem);
    }

    /**
     * Register an armor material with default values.
     *
     * @param path             path for new entry
     * @param defense          protection value for each slot type, use
     *                         {@link ItemEquipmentFactories#toArmorTypeMap(int...)} for converting a legacy protection
     *                         amount values array
     * @param enchantmentValue enchantment value (leather: 15, gold: 25, chain: 12, iron: 9, diamond: 10, turtle: 9,
     *                         netherite: 15)
     * @param repairItem       the repair material used in an anvil for restoring item durability
     * @return holder reference
     */
    @Deprecated
    default Holder.Reference<ArmorMaterial> registerArmorMaterial(String path, Map<ArmorItem.Type, Integer> defense, int enchantmentValue, Holder<Item> repairItem) {
        return this.registerArmorMaterial(path,
                defense,
                enchantmentValue,
                SoundEvents.ARMOR_EQUIP_GENERIC,
                () -> Ingredient.of(repairItem.value()),
                0.0F,
                0.0F);
    }

    /**
     * Register an armor material.
     *
     * @param path                path for new entry
     * @param defense             protection value for each slot type, use
     *                            {@link ItemEquipmentFactories#toArmorTypeMap(int...)} for converting a legacy
     *                            protection amount values array
     * @param enchantmentValue    enchantment value (leather: 15, gold: 25, chain: 12, iron: 9, diamond: 10, turtle: 9,
     *                            netherite: 15)
     * @param equipSound          the sound played when putting a piece of armor into the dedicated equipment slot
     * @param repairIngredient    the repair material used in an anvil for restoring item durability
     * @param toughness           armor toughness value for all slot types of this armor set
     * @param knockbackResistance knockback resistance value for all slot types of this armor set
     * @return holder reference
     */
    @Deprecated
    default Holder.Reference<ArmorMaterial> registerArmorMaterial(String path, Map<ArmorItem.Type, Integer> defense, int enchantmentValue, Holder<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient, float toughness, float knockbackResistance) {
        return this.registerArmorMaterial(path,
                () -> new ArmorMaterial(defense,
                        enchantmentValue,
                        equipSound,
                        repairIngredient,
                        Collections.singletonList(new ArmorMaterial.Layer(this.makeKey(path))),
                        toughness,
                        knockbackResistance));
    }

    /**
     * Register an armor material.
     *
     * @param path                  path for new entry
     * @param armorMaterialSupplier supplier for entry to register
     * @return holder reference
     */
    default Holder.Reference<ArmorMaterial> registerArmorMaterial(String path, Supplier<ArmorMaterial> armorMaterialSupplier) {
        return this.register(Registries.ARMOR_MATERIAL, path, armorMaterialSupplier);
    }

    /**
     * Creates a new {@link ResourceKey} for a {@link DamageType}.
     *
     * @param path path for new resource key
     * @return new resource key
     */
    default ResourceKey<DamageType> registerDamageType(String path) {
        return this.makeResourceKey(Registries.DAMAGE_TYPE, path);
    }

    /**
     * Creates a new {@link ResourceKey} for a {@link TrimMaterial}.
     *
     * @param path path for new resource key
     * @return new resource key
     */
    default ResourceKey<TrimMaterial> registerTrimMaterial(String path) {
        return this.makeResourceKey(Registries.TRIM_MATERIAL, path);
    }

    /**
     * Creates a new {@link ResourceKey} for a {@link LootTable}.
     *
     * @param path path for new resource key
     * @return new resource key
     */
    default ResourceKey<LootTable> registerLootTable(String path) {
        return this.makeResourceKey(Registries.LOOT_TABLE, path);
    }

    /**
     * Creates an empty tag in the corresponding registry, so that the tag can be used during data generation via
     * {@link net.minecraft.core.HolderGetter#get(TagKey)} and
     * {@link net.minecraft.core.HolderGetter#getOrThrow(TagKey)}.
     *
     * @param registryKey the registry key
     * @param tagKey      the tag key
     * @param <T>         the registry type
     */
    <T> void prepareTag(ResourceKey<? extends Registry<? super T>> registryKey, TagKey<T> tagKey);
}
