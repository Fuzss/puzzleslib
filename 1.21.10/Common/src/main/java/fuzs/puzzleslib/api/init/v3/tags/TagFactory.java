package fuzs.puzzleslib.api.init.v3.tags;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
 * A simple helper class for creating new {@link TagKey} instances with a set namespace.
 */
@FunctionalInterface
public interface TagFactory {
    /**
     * A built-in factory for the <code>minecraft</code> namespace.
     */
    TagFactory MINECRAFT = make("minecraft");
    /**
     * A built-in factory for the <code>common</code> namespace.
     */
    TagFactory COMMON = make("c");
    /**
     * A built-in factory for the <code>fabric</code> namespace.
     */
    TagFactory FABRIC = make("fabric");
    /**
     * A built-in factory for the <code>neoforge</code> namespace.
     */
    TagFactory NEOFORGE = make("neoforge");

    /**
     * Construct a new factory instance backed by a provided namespace.
     *
     * @param modId the namespace
     * @return new factory instance
     */
    static TagFactory make(String modId) {
        return () -> modId;
    }

    /**
     * @return the mod id
     */
    String modId();

    /**
     * Creates a new {@link TagKey} for any type of registry from a given path.
     *
     * @param registryKey key for registry to create key from
     * @param path        path for new tag key
     * @param <T>         registry type
     * @return new tag key
     */
    default <T> TagKey<T> registerTagKey(ResourceKey<? extends Registry<T>> registryKey, String path) {
        return TagKey.create(registryKey, ResourceLocation.fromNamespaceAndPath(this.modId(), path));
    }

    /**
     * Creates a new {@link TagKey} for blocks.
     *
     * @param path path for new tag key
     * @return new tag key
     */
    default TagKey<Block> registerBlockTag(String path) {
        return this.registerTagKey(Registries.BLOCK, path);
    }

    /**
     * Creates a new {@link TagKey} for items.
     *
     * @param path path for new tag key
     * @return new tag key
     */
    default TagKey<Item> registerItemTag(String path) {
        return this.registerTagKey(Registries.ITEM, path);
    }

    /**
     * Creates a new {@link TagKey} for fluids.
     *
     * @param path path for new tag key
     * @return new tag key
     */
    default TagKey<Fluid> registerFluidTag(String path) {
        return this.registerTagKey(Registries.FLUID, path);
    }

    /**
     * Creates a new {@link TagKey} for entity types.
     *
     * @param path path for new tag key
     * @return new tag key
     */
    default TagKey<EntityType<?>> registerEntityTypeTag(String path) {
        return this.registerTagKey(Registries.ENTITY_TYPE, path);
    }

    /**
     * Creates a new {@link TagKey} for enchantments.
     *
     * @param path path for new tag key
     * @return new tag key
     */
    default TagKey<Enchantment> registerEnchantmentTag(String path) {
        return this.registerTagKey(Registries.ENCHANTMENT, path);
    }

    /**
     * Creates a new {@link TagKey} for biomes.
     *
     * @param path path for new tag key
     * @return new tag key
     */
    default TagKey<Biome> registerBiomeTag(String path) {
        return this.registerTagKey(Registries.BIOME, path);
    }

    /**
     * Creates a new {@link TagKey} for game events.
     *
     * @param path path for new tag key
     * @return new tag key
     */
    default TagKey<GameEvent> registerGameEventTag(String path) {
        return this.registerTagKey(Registries.GAME_EVENT, path);
    }

    /**
     * Creates a new {@link TagKey} for damage types.
     *
     * @param path path for new tag key
     * @return new tag key
     */
    default TagKey<DamageType> registerDamageTypeTag(String path) {
        return this.registerTagKey(Registries.DAMAGE_TYPE, path);
    }
}
