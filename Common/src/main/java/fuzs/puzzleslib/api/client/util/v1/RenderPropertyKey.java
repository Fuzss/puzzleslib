package fuzs.puzzleslib.api.client.util.v1;

import fuzs.puzzleslib.impl.client.core.ClientFactories;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A key implementation for custom data attached to render states.
 * <p>
 * Implemented as a wrapper around resource location to allow for an additional type parameter.
 * <p>
 * TODO move to {@link fuzs.puzzleslib.api.client.renderer.v1}
 *
 * @param resourceLocation the resource location key
 * @param <T>              the stored object type
 */
public record RenderPropertyKey<T>(ResourceLocation resourceLocation) {

    /**
     * Gets a value for a key from the provided render state.
     * <p>
     * Will throw when the key is not found, additionally use
     * {@link #containsRenderProperty(EntityRenderState, RenderPropertyKey)}.
     *
     * @param entityRenderState the render state
     * @param key               the render property key
     * @param <T>               the key &amp; value type
     * @return the render property value associated with the key
     */
    @NotNull
    public static <T> T getRenderProperty(EntityRenderState entityRenderState, RenderPropertyKey<T> key) {
        T renderProperty = ClientFactories.INSTANCE.getRenderProperty(entityRenderState, key);
        Objects.requireNonNull(renderProperty, "render property " + key + " is null");
        return renderProperty;
    }

    /**
     * Does the provided render state contain a value for a key.
     *
     * @param entityRenderState the render state
     * @param key               the render property key
     * @param <T>               the key &amp; value type
     * @return is there a render property value associated with the key
     */
    public static <T> boolean containsRenderProperty(EntityRenderState entityRenderState, RenderPropertyKey<T> key) {
        return ClientFactories.INSTANCE.getRenderProperty(entityRenderState, key) != null;
    }

    /**
     * Sets a value for a key to the provided render state.
     *
     * @param entityRenderState the render state
     * @param key               the render property key
     * @param t                 the render property value
     * @param <T>               the key &amp; value type
     */
    public static <T> void setRenderProperty(EntityRenderState entityRenderState, RenderPropertyKey<T> key, @Nullable T t) {
        ClientFactories.INSTANCE.setRenderProperty(entityRenderState, key, t);
    }
}
