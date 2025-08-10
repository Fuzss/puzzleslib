package fuzs.puzzleslib.api.client.renderer.v1;

import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A key implementation for custom data attached to render states.
 * <p>
 * Implemented as a wrapper around {@link ResourceLocation} to allow for an additional type parameter.
 *
 * @param resourceLocation the resource location key
 * @param <T>              the stored object type
 */
public record RenderPropertyKey<T>(ResourceLocation resourceLocation) {

    /**
     * Get the partial tick time from a render state instance.
     *
     * @param renderState the render state
     * @return the partial tick time
     */
    public static float getPartialTick(EntityRenderState renderState) {
        return ClientProxyImpl.get().getPartialTick(renderState);
    }

    /**
     * Runs mod-loader-specific hooks that add custom data to an entity render state.
     *
     * @param renderer    the entity renderer
     * @param entity      the entity
     * @param renderState the render state
     * @param partialTick the partial tick time
     * @param <E>         the entity type
     * @param <S>         the render state type
     */
    public static <E extends Entity, S extends EntityRenderState> void onUpdateEntityRenderState(EntityRenderer<E, S> renderer, E entity, S renderState, float partialTick) {
        ClientProxyImpl.get().onUpdateEntityRenderState(renderer, entity, renderState, partialTick);
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
    public static <T> T getOrDefault(EntityRenderState renderState, RenderPropertyKey<T> key, T defaultProperty) {
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
    public static <T> boolean has(EntityRenderState renderState, RenderPropertyKey<T> key) {
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
    public static <T> void set(EntityRenderState renderState, RenderPropertyKey<T> key, T renderProperty) {
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
    public static <T> void remove(EntityRenderState renderState, RenderPropertyKey<T> key) {
        ClientProxyImpl.get().setRenderProperty(renderState, key, null);
    }
}
