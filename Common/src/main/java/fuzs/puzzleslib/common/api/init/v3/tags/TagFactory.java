package fuzs.puzzleslib.common.api.init.v3.tags;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockItemTagId;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;

/**
 * A simple helper class for creating new {@link TagKey TagKeys} with a provided namespace.
 */
@FunctionalInterface
public interface TagFactory {
    /**
     * A factory for the {@code minecraft} namespace.
     */
    TagFactory MINECRAFT = make("minecraft");
    /**
     * A factory for the {@code common} namespace.
     */
    TagFactory COMMON = make("c");
    /**
     * A factory for the {@code fabric} namespace.
     */
    TagFactory FABRIC = make("fabric");
    /**
     * A factory for the {@code neoforge} namespace.
     */
    TagFactory NEOFORGE = make("neoforge");

    /**
     * Construct a new factory backed by a provided namespace.
     *
     * @param modId the namespace
     * @return the new factory
     */
    static TagFactory make(String modId) {
        return () -> modId;
    }

    /**
     * @return the mod id
     */
    String modId();

    /**
     * Creates a new {@link TagKey} for any type of registry from a given name.
     *
     * @param registryKey the registry key
     * @param name        the tag name
     * @param <T>         the registry type
     * @return the tag key
     */
    default <T> TagKey<T> registerTagKey(ResourceKey<? extends Registry<T>> registryKey, String name) {
        return TagKey.create(registryKey, Identifier.fromNamespaceAndPath(this.modId(), name));
    }

    /**
     * Creates a new {@link TagKey} for {@link Block Blocks}.
     *
     * @param name the tag name
     * @return the tag key
     */
    default TagKey<Block> registerBlockTag(String name) {
        return this.registerTagKey(Registries.BLOCK, name);
    }

    /**
     * Creates a new {@link TagKey} for {@link Item Items}.
     *
     * @param name the tag name
     * @return the tag key
     */
    default TagKey<Item> registerItemTag(String name) {
        return this.registerTagKey(Registries.ITEM, name);
    }

    /**
     * Creates a new {@link BlockItemTagId} for {@link Block Blocks} &amp; {@link Item Items}.
     *
     * @param name the tag name
     * @return the block item tag id
     */
    default BlockItemTagId registerBlockItemTag(String name) {
        return this.registerBlockItemTag(name, name);
    }

    /**
     * Creates a new {@link BlockItemTagId} for {@link Block Blocks} &amp; {@link Item Items}.
     *
     * @param blockName the block tag name
     * @param itemName  the item tag name
     * @return the block item tag id
     */
    default BlockItemTagId registerBlockItemTag(String blockName, String itemName) {
        return new BlockItemTagId(this.registerBlockTag(blockName), this.registerItemTag(itemName));
    }

    /**
     * Creates a new {@link TagKey} for {@link Fluid Fluids}.
     *
     * @param name the tag name
     * @return the tag key
     */
    default TagKey<Fluid> registerFluidTag(String name) {
        return this.registerTagKey(Registries.FLUID, name);
    }

    /**
     * Creates a new {@link TagKey} for {@link EntityType EntityTypes}.
     *
     * @param name the tag name
     * @return the tag key
     */
    default TagKey<EntityType<?>> registerEntityTypeTag(String name) {
        return this.registerTagKey(Registries.ENTITY_TYPE, name);
    }

    /**
     * Creates a new {@link TagKey} for {@link Enchantment Enchantments}.
     *
     * @param name the tag name
     * @return the tag key
     */
    default TagKey<Enchantment> registerEnchantmentTag(String name) {
        return this.registerTagKey(Registries.ENCHANTMENT, name);
    }

    /**
     * Creates a new {@link TagKey} for {@link Biome Biomes}.
     *
     * @param name the tag name
     * @return the tag key
     */
    default TagKey<Biome> registerBiomeTag(String name) {
        return this.registerTagKey(Registries.BIOME, name);
    }

    /**
     * Creates a new {@link TagKey} for {@link GameEvent GameEvents}.
     *
     * @param name the tag name
     * @return the tag key
     */
    default TagKey<GameEvent> registerGameEventTag(String name) {
        return this.registerTagKey(Registries.GAME_EVENT, name);
    }

    /**
     * Creates a new {@link TagKey} for {@link DamageType DamageTypes}.
     *
     * @param name the tag name
     * @return the tag key
     */
    default TagKey<DamageType> registerDamageTypeTag(String name) {
        return this.registerTagKey(Registries.DAMAGE_TYPE, name);
    }
}
