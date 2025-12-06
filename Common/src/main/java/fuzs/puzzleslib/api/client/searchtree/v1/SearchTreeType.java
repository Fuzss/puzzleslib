package fuzs.puzzleslib.api.client.searchtree.v1;

import net.minecraft.resources.ResourceLocation;

/**
 * A search tree type token for identity references. Used in {@link SearchRegistryHelper}.
 * <p>
 * Similar to the vanilla search tree key class, which we do not utilize as it has changed in-between versions.
 * <p>
 * TODO replace this with {@link net.minecraft.util.context.ContextKey}
 *
 * @param resourceLocation resource location
 * @param <T>              search item type
 */
@Deprecated(forRemoval = true)
public record SearchTreeType<T>(ResourceLocation resourceLocation) {

}
