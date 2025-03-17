package fuzs.puzzleslib.api.client.renderer.v1;

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
    @Deprecated(forRemoval = true)
    @NotNull
    public static <T> T getRenderProperty(EntityRenderState renderState, RenderPropertyKey<T> key) {
        T renderProperty = get(renderState, key);
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
    @Deprecated(forRemoval = true)
    public static <T> boolean containsRenderProperty(EntityRenderState renderState, RenderPropertyKey<T> key) {
        return has(renderState, key);
    }

    /**
     * Sets a value for a key to the provided render state.
     *
     * @param renderState the render state
     * @param key         the render property key
     * @param t           the render property value
     * @param <T>         the key &amp; value type
     */
    @Deprecated(forRemoval = true)
    public static <T> void setRenderProperty(EntityRenderState renderState, RenderPropertyKey<T> key, @Nullable T t) {
        if (t == null) {
            remove(renderState, key);
        } else {
            set(renderState, key, t);
        }
    }

    /**
     * Gets a nullable value for a key from the provided render state.
     *
     * @param renderState the render state
     * @param key         the render property key
     * @param <T>         the value type
     * @return the render property value associated with the key
     */
    @Nullable
    public static <T> T get(EntityRenderState renderState, RenderPropertyKey<T> key) {
        return ClientFactories.INSTANCE.getRenderProperty(renderState, key);
    }

    /**
     * Gets a value for a key from the provided render state, or the provided fallback when not present.
     *
     * @param renderState     the render state
     * @param key             the render property key
     * @param defaultProperty the fallback render property value
     * @param <T>             the value type
     * @return the render property value associated with the key
     */
    public static <T> T getOrDefault(EntityRenderState renderState, RenderPropertyKey<T> key, T defaultProperty) {
        T renderProperty = ClientFactories.INSTANCE.getRenderProperty(renderState, key);
        return renderProperty != null ? renderProperty : defaultProperty;
    }

    /**
     * Does the provided render state contain a value for a key.
     *
     * @param renderState the render state
     * @param key         the render property key
     * @param <T>         the value type
     * @return is there a render property value associated with the key
     */
    public static <T> boolean has(EntityRenderState renderState, RenderPropertyKey<T> key) {
        return ClientFactories.INSTANCE.getRenderProperty(renderState, key) != null;
    }

    /**
     * Sets a non-null value for a key to the provided render state.
     *
     * @param renderState    the render state
     * @param key            the render property key
     * @param renderProperty the render property value
     * @param <T>            the value type
     */
    public static <T> void set(EntityRenderState renderState, RenderPropertyKey<T> key, T renderProperty) {
        Objects.requireNonNull(renderProperty, "render property is null");
        ClientFactories.INSTANCE.setRenderProperty(renderState, key, renderProperty);
    }

    /**
     * Removes a value for a key from the provided render state.
     *
     * @param renderState the render state
     * @param key         the render property key
     * @param <T>         the value type
     */
    public static <T> void remove(EntityRenderState renderState, RenderPropertyKey<T> key) {
        ClientFactories.INSTANCE.setRenderProperty(renderState, key, null);
    }
}
