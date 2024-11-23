package fuzs.puzzleslib.api.client.event.v1.renderer;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

@FunctionalInterface
public interface ExtractRenderStateCallback {
    EventInvoker<ExtractRenderStateCallback> EVENT = EventInvoker.lookup(ExtractRenderStateCallback.class);

    /**
     * Called during {@link EntityRenderer#extractRenderState(Entity, EntityRenderState, float)}, for setting up the
     * render state of an entity for future rendering.
     * <p>
     * Use methods found in {@link fuzs.puzzleslib.api.client.util.v1.RenderPropertyKey} for attaching custom render
     * state data.
     *
     * @param entity         the entity
     * @param renderState    the entity render state, might not be fully initialized
     * @param entityRenderer the entity renderer
     * @param partialTick    the current partial tick
     */
    void onExtractRenderState(Entity entity, EntityRenderState renderState, EntityRenderer<?, ?> entityRenderer, float partialTick);
}
