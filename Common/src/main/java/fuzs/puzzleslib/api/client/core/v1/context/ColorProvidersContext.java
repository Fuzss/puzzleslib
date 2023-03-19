package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Register custom item/block color providers, like tint getters for leaves or grass.
 *
 * @param <T> provider type, either {@link BlockColor} or {@link ItemColor}
 * @param <P> object type supported by provider, either {@link Block} or {@link Item}
 */
public interface ColorProvidersContext<T, P> {

    /**
     * Register a new <code>provider</code> for a number of <code>objects</code>.
     *
     * @param provider provider type, either {@link BlockColor} or {@link ItemColor}
     * @param object   object type supported by provider, either {@link Block} or {@link Item}
     * @param objects  more object types supported by provider, either {@link Block} or {@link Item}
     */
    @SuppressWarnings("unchecked")
    void registerColorProvider(P provider, T object, T... objects);

    /**
     * Provides access to already registered providers, might be incomplete during registration,
     * but is good to use in either {@link BlockColor} or {@link ItemColor}.
     *
     * @return access to {@link net.minecraft.client.color.block.BlockColors} or {@link net.minecraft.client.color.item.ItemColors}
     */
    P getProviders();
}
