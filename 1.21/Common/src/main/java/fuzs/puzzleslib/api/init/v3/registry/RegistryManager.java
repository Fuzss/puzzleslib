package fuzs.puzzleslib.api.init.v3.registry;

import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.arguments.ArgumentType;
import fuzs.puzzleslib.api.core.v1.utility.EnvironmentAwareBuilder;
import fuzs.puzzleslib.api.item.v2.ItemEquipmentFactories;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.item.RecipeTypeImpl;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

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
     * Create a new {@link ResourceLocation} for the set mod id.
     *
     * @param path path for location
     * @return resource location for set namespace
     */
    ResourceLocation makeKey(String path);

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
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return holder reference
     */
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
    default Holder.Reference<Item> registerItem(String path, Supplier<Item> entry) {
        return this.register(Registries.ITEM, path, entry);
    }

    /**
     * Registers a block item for a block.
     *
     * @param blockReference reference for block to register item variant for
     * @return holder reference
     */
    default Holder.Reference<Item> registerBlockItem(Holder<Block> blockReference) {
        return this.registerBlockItem(blockReference, new Item.Properties());
    }

    /**
     * Registers a block item for a block.
     *
     * @param blockReference reference for block to register item variant for
     * @param itemProperties properties for item
     * @return holder reference
     */
    default Holder.Reference<Item> registerBlockItem(Holder<Block> blockReference, Item.Properties itemProperties) {
        return this.registerItem(blockReference.unwrapKey().orElseThrow().location().getPath(), () -> {
            return new BlockItem(blockReference.value(), itemProperties);
        });
    }

    /**
     * Registers a spawn egg item for an entity type.
     *
     * @param entityTypeReference reference for the entity type to register a spawn egg for
     * @param backgroundColor     background color of the spawn egg item
     * @param highlightColor      spots color pf the spawn egg item
     * @return holder reference
     */
    default Holder.Reference<Item> registerSpawnEggItem(Holder<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor) {
        return this.registerSpawnEggItem(entityTypeReference, backgroundColor, highlightColor, new Item.Properties());
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
    Holder.Reference<Item> registerSpawnEggItem(Holder<? extends EntityType<? extends Mob>> entityTypeReference, int backgroundColor, int highlightColor, Item.Properties itemProperties);

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
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return holder reference
     */
    default Holder.Reference<Potion> registerPotion(String path, Supplier<Potion> entry) {
        return this.register(Registries.POTION, path, entry);
    }

    /**
     * Register an enchantment.
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @return holder reference
     */
    default ResourceKey<Enchantment> registerEnchantment(String path) {
        return this.makeResourceKey(Registries.ENCHANTMENT, path);
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
                }
        );
    }

    /**
     * Register a block entity type.
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @param <T>   block entity type parameter
     * @return holder reference
     */
    @SuppressWarnings("unchecked")
    default <T extends BlockEntity> Holder.Reference<BlockEntityType<T>> registerBlockEntityType(String path, Supplier<BlockEntityType.Builder<T>> entry) {
        return this.register((ResourceKey<Registry<BlockEntityType<T>>>) (ResourceKey<?>) Registries.BLOCK_ENTITY_TYPE,
                path,
                () -> {
                    return entry.get().build(null);
                }
        );
    }

    /**
     * Register a menu type.
     *
     * @param path  path for new entry
     * @param entry supplier for entry to register
     * @param <T>   container menu type parameter
     * @return holder reference
     */
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
    <T extends AbstractContainerMenu> Holder.Reference<MenuType<T>> registerExtendedMenuType(String path, Supplier<ExtendedMenuSupplier<T>> entry);

    /**
     * Register a poi type.
     *
     * @param path  path for new entry
     * @param block block valid for this poi type
     * @return holder reference
     */
    default Holder.Reference<PoiType> registerPoiType(String path, Holder<Block> block) {
        return this.registerPoiType(path, block::value);
    }

    /**
     * Register a poi type.
     *
     * @param path  path for new entry
     * @param block block valid for this poi type
     * @return holder reference
     */
    default Holder.Reference<PoiType> registerPoiType(String path, Supplier<Block> block) {
        return this.registerPoiType(path, () -> {
            return ImmutableSet.copyOf(block.get().getStateDefinition().getPossibleStates());
        }, 0, 1);
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
    Holder.Reference<PoiType> registerPoiType(String path, Supplier<Set<BlockState>> matchingStates, int maxTickets, int validRange);

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
            return new RecipeTypeImpl<>(this.makeKey(path));
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
    default Holder.Reference<ArmorMaterial> registerArmorMaterial(String path, Map<ArmorItem.Type, Integer> defense, int enchantmentValue, Holder<Item> repairItem) {
        return this.registerArmorMaterial(path,
                defense,
                enchantmentValue,
                SoundEvents.ARMOR_EQUIP_GENERIC,
                () -> Ingredient.of(repairItem.value()),
                0.0F,
                0.0F
        );
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
    default Holder.Reference<ArmorMaterial> registerArmorMaterial(String path, Map<ArmorItem.Type, Integer> defense, int enchantmentValue, Holder<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient, float toughness, float knockbackResistance) {
        return this.register(Registries.ARMOR_MATERIAL,
                path,
                () -> new ArmorMaterial(defense,
                        enchantmentValue,
                        equipSound,
                        repairIngredient,
                        Collections.singletonList(new ArmorMaterial.Layer(this.makeKey(path))),
                        toughness,
                        knockbackResistance
                )
        );
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
}
