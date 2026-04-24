package fuzs.puzzleslib.common.api.client.renderer.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * An extension of {@link ChestRenderer} that allows specifying custom chest texture materials for single chests (e.g.
 * ender chest).
 *
 * @param <T> the chest block entity type
 * @param <M> the chest model type
 */
public abstract class SingleChestRenderer<T extends BlockEntity & LidBlockEntity, M extends ChestModel, S extends SingleChestRenderer.SingleChestRenderState> extends ChestRenderer<T> {
    /**
     * The sprite set.
     */
    protected final SpriteGetter sprites;
    /**
     * The chest model.
     */
    protected final M chestModel;

    /**
     * @param context    the renderer context
     * @param chestModel the single chest model
     */
    public SingleChestRenderer(BlockEntityRendererProvider.Context context, M chestModel) {
        super(context);
        this.sprites = context.sprites();
        this.chestModel = chestModel;
    }

    @Override
    public S createRenderState() {
        return (S) new SingleChestRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, ChestRenderState chestRenderState, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(blockEntity, chestRenderState, partialTick, cameraPosition, crumblingOverlay);
        ((S) chestRenderState).chestMaterial = this.getChestMaterial(blockEntity, this.xmasTextures);
    }

    @Override
    public void submit(ChestRenderState chestRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.mulPose(modelTransformation(chestRenderState.facing));
        this.submitChestModel((S) chestRenderState, poseStack, submitNodeCollector);
        poseStack.popPose();
    }

    /**
     * Submit the single chest model after everything on the pose stack has been set up.
     *
     * @param chestRenderState    the chest render state
     * @param poseStack           the pose stack
     * @param submitNodeCollector the submit node collector
     */
    protected void submitChestModel(S chestRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        SpriteId spriteId = chestRenderState.chestMaterial;
        submitNodeCollector.submitModel(this.chestModel,
                chestRenderState.getOpenness(),
                poseStack,
                chestRenderState.lightCoords,
                OverlayTexture.NO_OVERLAY,
                -1,
                spriteId,
                this.sprites,
                0,
                chestRenderState.breakProgress);
    }

    /**
     * Get the single chest texture material for the chest type.
     *
     * @param blockEntity  the block entity
     * @param xmasTextures should use holiday textures
     * @return the single chest texture material
     */
    protected abstract SpriteId getChestMaterial(T blockEntity, boolean xmasTextures);

    /**
     * A custom chest render state that stores the {@link Material} directly, to allow for working around vanilla's
     * {@link net.minecraft.client.renderer.blockentity.state.ChestRenderState.ChestMaterialType} enum class.
     */
    public static class SingleChestRenderState extends ChestRenderState {
        /**
         * The chest material.
         */
        public SpriteId chestMaterial = Sheets.CHEST_REGULAR.single();

        /**
         * @return the chest lid openness
         */
        public float getOpenness() {
            float openness = this.open;
            openness = 1.0F - openness;
            return 1.0F - openness * openness * openness;
        }
    }
}
