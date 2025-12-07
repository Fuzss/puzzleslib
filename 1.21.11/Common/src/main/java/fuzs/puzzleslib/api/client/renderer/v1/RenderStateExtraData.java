package fuzs.puzzleslib.api.client.renderer.v1;

import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.context.ContextKey;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * An implementation for custom data attached to render states.
 */
public final class RenderStateExtraData {

    /**
     * Gets a nullable value for a key from the provided render state.
     *
     * @param renderState the render state
     * @param key         the render property key
     * @param <T>         the value type
     * @return the render property value associated with the key
     */
    @Nullable
    public static <T> T get(EntityRenderState renderState, ContextKey<T> key) {
        return ClientProxyImpl.get().getRenderProperty(renderState, key);
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
    public static <T> T getOrDefault(EntityRenderState renderState, ContextKey<T> key, T defaultProperty) {
        T renderProperty = ClientProxyImpl.get().getRenderProperty(renderState, key);
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
    public static <T> boolean has(EntityRenderState renderState, ContextKey<T> key) {
        return ClientProxyImpl.get().getRenderProperty(renderState, key) != null;
    }

    /**
     * Sets a non-null value for a key to the provided render state.
     *
     * @param renderState    the render state
     * @param key            the render property key
     * @param renderProperty the render property value
     * @param <T>            the value type
     */
    public static <T> void set(EntityRenderState renderState, ContextKey<T> key, T renderProperty) {
        Objects.requireNonNull(renderProperty, "render property is null");
        ClientProxyImpl.get().setRenderProperty(renderState, key, renderProperty);
    }

    /**
     * Removes a value for a key from the provided render state.
     *
     * @param renderState the render state
     * @param key         the render property key
     * @param <T>         the value type
     */
    public static <T> void remove(EntityRenderState renderState, ContextKey<T> key) {
        ClientProxyImpl.get().setRenderProperty(renderState, key, null);
    }
}
