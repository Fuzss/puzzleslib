package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public interface RenderNameTagCallback {
    EventInvoker<RenderNameTagCallback> EVENT = EventInvoker.lookup(RenderNameTagCallback.class);

    /**
     * Fires before the name tag of an entity is tried to be rendered, in addition to preventing the name tag from rendering, rendering can also be forced.
     *
     * @param entity            the entity the name tag is rendered for
     * @param content           the entity display name retrieved from {@link Entity#getDisplayName()}, controls the name that is actually rendered for both {@link EventResult#ALLOW} and {@link EventResult#PASS}
     * @param entityRenderer    {@link EntityRenderer} instance
     * @param poseStack         the current {@link PoseStack}
     * @param multiBufferSource the current {@link MultiBufferSource}
     * @param packedLight       packet light the entity is rendered with
     * @param partialTick       current partial tick time
     * @return {@link EventResult#ALLOW} to force the name tag to render, ignoring the need for a custom name to be set, as well as ignoring other restrictions such as distance and team affiliation,
     * {@link EventResult#DENY} to prevent any name tag from showing,
     * {@link EventResult#PASS} vanilla checks required for the name tag to render are performed
     */
    EventResult onRenderNameTag(Entity entity, DefaultedValue<Component> content, EntityRenderer<?> entityRenderer, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, float partialTick);
}
