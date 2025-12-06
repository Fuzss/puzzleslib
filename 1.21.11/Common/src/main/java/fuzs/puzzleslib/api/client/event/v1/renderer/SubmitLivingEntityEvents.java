package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;

public final class SubmitLivingEntityEvents {
    public static final EventInvoker<Before> BEFORE = EventInvoker.lookup(Before.class);
    public static final EventInvoker<After> AFTER = EventInvoker.lookup(After.class);

    private SubmitLivingEntityEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Before {

        /**
         * Called before a living entity model is submitted for rendering.
         * <p>
         * This allows for completely taking over rendering as a whole.
         *
         * @param <T>            the entity type
         * @param <S>            the render state type
         * @param <M>            the entity model type
         * @param renderState    the entity render state
         * @param entityRenderer the living entity renderer
         * @param poseStack      the pose stack
         * @param nodeCollector  the submit node collector
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the player model from rendering, this allows for taking over complete player rendering</li>
         *         <li>{@link EventResult#PASS PASS} to allow the player model to render</li>
         *         </ul>
         */
        <T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> EventResult onBeforeSubmitLivingEntity(S renderState, LivingEntityRenderer<T, S, M> entityRenderer, PoseStack poseStack, SubmitNodeCollector nodeCollector);
    }

    @FunctionalInterface
    public interface After {

        /**
         * Called after a living entity model is submitted for rendering.
         *
         * @param <T>            the entity type
         * @param <S>            the render state type
         * @param <M>            the entity model type
         * @param renderState    the entity render state
         * @param entityRenderer the living entity renderer
         * @param poseStack      the pose stack
         * @param nodeCollector  the submit node collector
         */
        <T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> void onAfterSubmitLivingEntity(S renderState, LivingEntityRenderer<T, S, M> entityRenderer, PoseStack poseStack, SubmitNodeCollector nodeCollector);
    }
}
