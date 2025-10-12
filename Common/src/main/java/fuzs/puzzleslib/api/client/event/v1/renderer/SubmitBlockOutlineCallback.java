package fuzs.puzzleslib.api.client.event.v1.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.state.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@FunctionalInterface
public interface SubmitBlockOutlineCallback {
    EventInvoker<SubmitBlockOutlineCallback> EVENT = EventInvoker.lookup(SubmitBlockOutlineCallback.class);

    /**
     * Called when the block highlight outline for the current hit result is about to be submitted.
     * <p>
     * TODO include level render state so we can set the block outline state manually
     *
     * @param levelRenderer    the level renderer instance
     * @param clientLevel      the current client level
     * @param blockState       the block state from the hit result
     * @param hitResult        the hit result to render the outline for
     * @param collisionContext the collision context for the camera entity
     * @param camera           the camera
     * @return <ul>
     *         <li>{@link EventResultHolder#allow(Object)} to pass a {@link CustomBlockOutlineRenderer}</li>
     *         <li>{@link EventResultHolder#deny(Object)} to prevent any outline from rendering at all, simply pass {@code null}</li>
     *         <li>{@link EventResultHolder#pass()} to allow vanilla outline rendering to happen without changes</li>
     *         </ul>
     */
    EventResultHolder<@Nullable CustomBlockOutlineRenderer> onSubmitBlockOutline(LevelRenderer levelRenderer, ClientLevel clientLevel, BlockState blockState, BlockHitResult hitResult, CollisionContext collisionContext, Camera camera);

    /**
     * @see net.minecraft.client.renderer.SubmitNodeCollector.CustomGeometryRenderer
     * @see LevelRenderer#renderBlockOutline(MultiBufferSource.BufferSource, PoseStack, boolean, LevelRenderState)
     */
    @FunctionalInterface
    interface CustomBlockOutlineRenderer {

        /**
         * Called when the block outline described by the provided {@link BlockOutlineRenderState} is about to be
         * rendered.
         * <p>
         * TODO only pass camera render state from level here
         *
         * @param renderState      the block outline render state
         * @param bufferSource     the buffer source
         * @param poseStack        the pose stack
         * @param isTranslucent    is this the translucent render pass
         * @param levelRenderState the level render state
         * @return is rendering the vanilla block outline disallowed
         */
        boolean render(BlockOutlineRenderState renderState, MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, boolean isTranslucent, LevelRenderState levelRenderState);

        /**
         * @param renderState      the block outline render state
         * @param bufferSource     the buffer source
         * @param poseStack        the pose stack
         * @param isTranslucent    is this the translucent render pass
         * @param levelRenderState the level render state
         * @see LevelRenderer#renderBlockOutline(MultiBufferSource.BufferSource, PoseStack, boolean,
         *         LevelRenderState)
         */
        static void renderVanillaBlockOutline(BlockOutlineRenderState renderState, MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, boolean isTranslucent, LevelRenderState levelRenderState) {
            Objects.requireNonNull(renderState, "block outline render state is null");
            if (renderState.isTranslucent() == isTranslucent) {
                LevelRenderer levelRenderer = Minecraft.getInstance().levelRenderer;
                Vec3 cameraPosition = levelRenderState.cameraRenderState.pos;
                if (renderState.highContrast()) {
                    VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.secondaryBlockOutline());
                    levelRenderer.renderHitOutline(poseStack,
                            vertexConsumer,
                            cameraPosition.x,
                            cameraPosition.y,
                            cameraPosition.z,
                            renderState,
                            0XFF000000);
                }

                VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
                int color = renderState.highContrast() ? 0XFF57FFE1 : ARGB.color(102, 0XFF000000);
                levelRenderer.renderHitOutline(poseStack,
                        vertexConsumer,
                        cameraPosition.x,
                        cameraPosition.y,
                        cameraPosition.z,
                        renderState,
                        color);
                bufferSource.endLastBatch();
            }
        }
    }
}
