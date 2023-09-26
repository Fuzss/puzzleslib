package fuzs.puzzleslib.api.init.v3.tags;

import com.google.common.collect.Maps;
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

import java.util.Map;

/**
 * A simple helper class for creating new {@link TagKey} instances with a pre-set registry via {@link ResourceKey}.
 *
 * @param <T> base type for tags produced by this factory instance
 */
public final class TypedTagFactory<T> {
    private static final Map<ResourceKey<Registry<?>>, TypedTagFactory<?>> VALUES = Maps.newConcurrentMap();
    /**
     * Factory instance of type {@link Block}.
     */
    public static final TypedTagFactory<Block> BLOCK = make(Registries.BLOCK);
    /**
     * Factory instance of type {@link Item}.
     */
    public static final TypedTagFactory<Item> ITEM = make(Registries.ITEM);
    /**
     * Factory instance of type {@link Fluid}.
     */
    public static final TypedTagFactory<Fluid> FLUID = make(Registries.FLUID);
    /**
     * Factory instance of type {@link EntityType}.
     */
    public static final TypedTagFactory<EntityType<?>> ENTITY_TYPE = make(Registries.ENTITY_TYPE);
    /**
     * Factory instance of type {@link Enchantment}.
     */
    public static final TypedTagFactory<Enchantment> ENCHANTMENT = make(Registries.ENCHANTMENT);
    /**
     * Factory instance of type {@link Biome}.
     */
    public static final TypedTagFactory<Biome> BIOME = make(Registries.BIOME);
    /**
     * Factory instance of type {@link GameEvent}.
     */
    public static final TypedTagFactory<GameEvent> GAME_EVENT = make(Registries.GAME_EVENT);
    /**
     * Factory instance of type {@link DamageType}.
     */
    public static final TypedTagFactory<DamageType> DAMAGE_TYPE = make(Registries.DAMAGE_TYPE);

    private final ResourceKey<Registry<T>> registryKey;

    private TypedTagFactory(ResourceKey<Registry<T>> registryKey) {
        this.registryKey = registryKey;
    }

    /**
     * Construct a new factory instance backed by a provided registry key.
     *
     * @param registryKey the registry key
     * @param <T>         tag base type
     * @return new factory instance
     */
    @SuppressWarnings("unchecked")
    public static <T> TypedTagFactory<T> make(ResourceKey<Registry<T>> registryKey) {
        return ((Map<ResourceKey<Registry<T>>, TypedTagFactory<T>>) (Map<?, ?>) VALUES).computeIfAbsent(registryKey, TypedTagFactory::new);
    }

    /**
     * Creates a new tag key using a custom namespace.
     *
     * @param namespace the tag key namespace
     * @param path      the tag key path
     * @return the tag key
     */
    public TagKey<T> make(String namespace, String path) {
        return TagKey.create(this.registryKey, new ResourceLocation(namespace, path));
    }

    /**
     * Creates a new tag key using the <code>minecraft</code> namespace.
     *
     * @param path the tag key path
     * @return the tag key
     */
    public TagKey<T> minecraft(String path) {
        return this.make("minecraft", path);
    }

    /**
     * Creates a new tag key using the <code>c</code> namespace.
     * <p>This namespace is used by Fabric Api.
     *
     * @param path the tag key path
     * @return the tag key
     */
    public TagKey<T> common(String path) {
        return this.make("c", path);
    }

    /**
     * Creates a new tag key using the <code>fabric</code> namespace.
     * <p>This namespace is present in Fabric Api, but is rarely used.
     *
     * @param path the tag key path
     * @return the tag key
     */
    public TagKey<T> fabric(String path) {
        return this.make("fabric", path);
    }

    /**
     * Creates a new tag key using the <code>forge</code> namespace.
     * <p>This namespace is used by Minecraft Forge.
     *
     * @param path the tag key path
     * @return the tag key
     */
    public TagKey<T> forge(String path) {
        return this.make("forge", path);
    }

    /**
     * Creates a new tag key using the <code>curios</code> namespace.
     * <p>This namespace is used by the Curios mod.
     *
     * @param path the tag key path
     * @return the tag key
     */
    public TagKey<T> curios(String path) {
        return this.make("curios", path);
    }

    /**
     * Creates a new tag key using the <code>trinkets</code> namespace.
     * <p>This namespace is used by the Trinkets mod.
     *
     * @param path the tag key path
     * @return the tag key
     */
    public TagKey<T> trinkets(String path) {
        return this.make("trinkets", path);
    }
}
