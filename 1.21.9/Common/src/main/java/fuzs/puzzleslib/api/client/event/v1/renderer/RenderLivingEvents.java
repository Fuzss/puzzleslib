package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
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
         * Called before a living entity model is rendered, allows for applying transformations to the
         * {@link PoseStack}, or for completely taking over rendering as a whole.
         *
         * @param entityRenderState   the entity render state
         * @param entityRenderer      the living entity renderer
         * @param partialTick         the partial tick time
         * @param poseStack           the pose stack
         * @param submitNodeCollector the submit node collector
         * @param <T>                 the entity type
         * @param <S>                 the render state type
         * @param <M>                 the entity model type
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT} to prevent the player model from rendering, this allows for taking over complete player rendering</li>
         *         <li>{@link EventResult#PASS} to allow the player model to render</li>
         *         </ul>
         */
        <T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> EventResult onBeforeRenderEntity(S entityRenderState, LivingEntityRenderer<T, S, M> entityRenderer, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector);
    }

    @FunctionalInterface
    public interface After {

        /**
         * Called after a living entity model is rendered, allows for cleaning up transformations applied to the
         * {@link PoseStack}.
         *
         * @param entityRenderState   the entity render state
         * @param entityRenderer      the living entity renderer
         * @param partialTick         the partial tick time
         * @param poseStack           the pose stack
         * @param submitNodeCollector the submit node collector
         * @param <T>                 the entity type
         * @param <S>                 the render state type
         * @param <M>                 the entity model type
         */
        <T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> void onAfterRenderEntity(S entityRenderState, LivingEntityRenderer<T, S, M> entityRenderer, float partialTick, PoseStack poseStack, SubmitNodeCollector submitNodeCollector);
    }
}
