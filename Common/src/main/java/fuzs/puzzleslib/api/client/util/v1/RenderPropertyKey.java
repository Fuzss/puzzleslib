package fuzs.puzzleslib.api.client.util.v1;

import fuzs.puzzleslib.impl.client.util.EntityRenderStateExtension;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A key implementation for custom data attached to render states.
 * <p>
 * Implemented as a wrapper around resource location to allow for an additional type parameter.
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
     * @param renderState the render state
     * @param key         the render property key
     * @param <T>         the key &amp; value type
     * @return the render property value associated with the key
     */
    @NotNull
    public static <T> T getRenderProperty(EntityRenderState renderState, RenderPropertyKey<T> key) {
        T renderProperty = ((EntityRenderStateExtension) renderState).mobplaques$getRenderProperty(key);
        Objects.requireNonNull(renderProperty, "render property " + key + " is null");
        return renderProperty;
    }

    /**
     * Does the provided render state contain a value for a key.
     *
     * @param renderState the render state
     * @param key         the render property key
     * @param <T>         the key &amp; value type
     * @return is there a render property value associated with the key
     */
    public static <T> boolean containsRenderProperty(EntityRenderState renderState, RenderPropertyKey<T> key) {
        T renderProperty = ((EntityRenderStateExtension) renderState).mobplaques$getRenderProperty(key);
        return renderProperty != null;
    }

    /**
     * Sets a value for a key to the provided render state.
     *
     * @param renderState the render state
     * @param key         the render property key
     * @param t           the render property value
     * @param <T>         the key &amp; value type
     */
    public static <T> void setRenderProperty(EntityRenderState renderState, RenderPropertyKey<T> key, @Nullable T t) {
        ((EntityRenderStateExtension) renderState).mobplaques$setRenderProperty(key, t);
    }
}
