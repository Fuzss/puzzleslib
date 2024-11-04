package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

public final class RenderLivingEvents {
    public static final EventInvoker<Before> BEFORE = EventInvoker.lookup(Before.class);
    public static final EventInvoker<After> AFTER = EventInvoker.lookup(After.class);

    private RenderLivingEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Before {

        /**
         * Called before a living entity model is rendered, allows for applying transformations to the {@link PoseStack}, or for completely taking over rendering as a whole.
         *
         * @param entity            the entity that is rendering
         * @param renderer          the used {@link LivingEntityRenderer} instance
         * @param poseStack         the current {@link PoseStack}
         * @param multiBufferSource the current {@link MultiBufferSource}
         * @param packedLight       packet light the entity is rendered with
         * @param partialTick       current partial tick time
         * @return {@link EventResult#INTERRUPT} to prevent the player model from rendering, this allows for taking over complete player rendering,
         * {@link EventResult#PASS} to allow the player model to render
         */
        <T extends LivingEntity, M extends EntityModel<T>> EventResult onBeforeRenderEntity(T entity, LivingEntityRenderer<T, M> renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight);
    }

    @FunctionalInterface
    public interface After {

        /**
         * Called after a living entity model is rendered, allows for cleaning up transformations applied to the {@link PoseStack}.
         *
         * @param entity            the entity that is rendering
         * @param renderer          the used {@link LivingEntityRenderer} instance
         * @param poseStack         the current {@link PoseStack}
         * @param multiBufferSource the current {@link MultiBufferSource}
         * @param packedLight       packet light the entity is rendered with
         * @param partialTick       current partial tick time
         */
        <T extends LivingEntity, M extends EntityModel<T>> void onAfterRenderEntity(T entity, LivingEntityRenderer<T, M> renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight);
    }
}
