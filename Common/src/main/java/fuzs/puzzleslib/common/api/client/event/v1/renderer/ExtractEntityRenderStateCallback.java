package fuzs.puzzleslib.common.api.client.event.v1.renderer;

import fuzs.puzzleslib.common.api.client.renderer.v1.RenderStateExtraData;
import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

@FunctionalInterface
public interface ExtractEntityRenderStateCallback {
    EventInvoker<ExtractEntityRenderStateCallback> EVENT = EventInvoker.lookup(ExtractEntityRenderStateCallback.class);

    /**
     * Called during {@link EntityRenderer#extractRenderState(Entity, EntityRenderState, float)}, for setting up the
     * render state of an entity for future rendering.
     * <p>
     * Use methods found in {@link RenderStateExtraData} for attaching custom render state data.
     *
     * @param entity      the entity
     * @param renderState the entity render state
     * @param partialTick the partial tick
     */
    void onExtractEntityRenderState(Entity entity, EntityRenderState renderState, float partialTick);
}
