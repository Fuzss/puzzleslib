package fuzs.puzzleslib.fabric.mixin.client;

import com.google.common.base.Preconditions;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.event.v1.renderer.SubmitBlockOutlineCallback;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(LevelRenderer.class)
abstract class LevelRendererMixin {
    @Unique
    private static final RenderStateDataKey<SubmitBlockOutlineCallback.CustomBlockOutlineRenderer> PUZZLESLIB_$_CUSTOM_BLOCK_OUTLINE_RENDERER_KEY = RenderStateDataKey.create(
            PuzzlesLibMod.id("custom_block_outline_renderer")::toString);

    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Nullable
    private ClientLevel level;

    @Inject(method = "extractBlockOutline",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/state/LevelRenderState;blockOutlineRenderState:Lnet/minecraft/client/renderer/state/BlockOutlineRenderState;",
                    shift = At.Shift.AFTER))
    private void extractBlockOutline(Camera camera, LevelRenderState levelRenderState, CallbackInfo callback, @Local BlockState blockState, @Local CollisionContext collisionContext) {
        if (levelRenderState.blockOutlineRenderState == null) {
            return;
        }

        EventResultHolder<SubmitBlockOutlineCallback.@Nullable CustomBlockOutlineRenderer> eventResult = FabricRendererEvents.SUBMIT_BLOCK_OUTLINE.invoker()
                .onSubmitBlockOutline(LevelRenderer.class.cast(this),
                        this.level,
                        blockState,
                        (BlockHitResult) this.minecraft.hitResult,
                        collisionContext,
                        camera);
        eventResult.ifDeny((SubmitBlockOutlineCallback.@Nullable CustomBlockOutlineRenderer customRenderer) -> {
            Preconditions.checkArgument(customRenderer == null, "custom block outline renderer is not null");
            levelRenderState.blockOutlineRenderState = null;
        });
        eventResult.ifAllow((SubmitBlockOutlineCallback.@Nullable CustomBlockOutlineRenderer customRenderer) -> {
            Objects.requireNonNull(customRenderer, "custom block outline renderer is null");
            levelRenderState.blockOutlineRenderState.setData(PUZZLESLIB_$_CUSTOM_BLOCK_OUTLINE_RENDERER_KEY,
                    customRenderer);
        });
    }

    @Inject(method = "renderBlockOutline", at = @At("HEAD"))
    private void renderBlockOutline(MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, boolean isTranslucentPass, LevelRenderState levelRenderState, CallbackInfo callback) {
        if (levelRenderState.blockOutlineRenderState == null) {
            return;
        }

        SubmitBlockOutlineCallback.CustomBlockOutlineRenderer customRenderer = levelRenderState.blockOutlineRenderState.getData(
                PUZZLESLIB_$_CUSTOM_BLOCK_OUTLINE_RENDERER_KEY);
        if (customRenderer != null && customRenderer.render(levelRenderState.blockOutlineRenderState,
                bufferSource,
                poseStack,
                isTranslucentPass,
                levelRenderState)) {
            callback.cancel();
        }
    }
}
