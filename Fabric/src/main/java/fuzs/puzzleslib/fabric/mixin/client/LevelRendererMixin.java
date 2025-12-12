package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricRendererEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(LevelRenderer.class)
abstract class LevelRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Nullable private ClientLevel level;

    @Inject(method = "extractBlockOutline",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"),
            cancellable = true)
    private void extractBlockOutline(Camera camera, LevelRenderState levelRenderState, CallbackInfo callback, @Local BlockState blockState, @Local CollisionContext collisionContext) {
        BlockHitResult hitResult = (BlockHitResult) this.minecraft.hitResult;
        Objects.requireNonNull(hitResult, "hit result is null");
        EventResultHolder<@Nullable VoxelShape> eventResult = FabricRendererEvents.EXTRACT_BLOCK_OUTLINE.invoker()
                .onExtractBlockOutline(this.level, hitResult.getBlockPos(), blockState, hitResult, collisionContext);
        eventResult.ifInterrupt((@Nullable VoxelShape voxelShape) -> {
            callback.cancel();
            if (voxelShape != null) {
                boolean isTranslucent = ItemBlockRenderTypes.getChunkRenderType(blockState).sortOnUpload();
                boolean highContrast = this.minecraft.options.highContrastBlockOutline().get();
                levelRenderState.blockOutlineRenderState = new BlockOutlineRenderState(hitResult.getBlockPos(),
                        isTranslucent,
                        highContrast,
                        voxelShape);
            }
        });
    }
}
