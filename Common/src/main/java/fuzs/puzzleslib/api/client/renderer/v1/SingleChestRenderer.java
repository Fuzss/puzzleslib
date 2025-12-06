package fuzs.puzzleslib.api.client.renderer.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * An extension of {@link ChestRenderer} that allows specifying custom chest texture materials for single chests (e.g.
 * ender chest).
 *
 * @param <T> the chest block entity type
 * @param <M> the chest model type
 */
public abstract class SingleChestRenderer<T extends BlockEntity & LidBlockEntity, M extends ChestModel, S extends SingleChestRenderer.SingleChestRenderState> extends ChestRenderer<T> {
    /**
     * The material set.
     */
    protected final MaterialSet materials;
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
        this.materials = context.materials();
        this.chestModel = chestModel;
    }

    @Override
    public S createRenderState() {
        return (S) new SingleChestRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, ChestRenderState chestRenderState, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(blockEntity, chestRenderState, partialTick, cameraPosition, crumblingOverlay);
        ((S) chestRenderState).chestMaterial = this.getChestMaterial(blockEntity, this.xmasTextures);
    }

    @Override
    public void submit(ChestRenderState chestRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-chestRenderState.angle));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
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
        Material material = chestRenderState.chestMaterial;
        RenderType renderType = material.renderType(RenderType::entityCutout);
        TextureAtlasSprite textureAtlasSprite = this.materials.get(material);
        submitNodeCollector.submitModel(this.chestModel,
                chestRenderState.getOpenness(),
                poseStack,
                renderType,
                chestRenderState.lightCoords,
                OverlayTexture.NO_OVERLAY,
                -1,
                textureAtlasSprite,
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
    protected abstract Material getChestMaterial(T blockEntity, boolean xmasTextures);

    /**
     * A custom chest render state that stores the {@link Material} directly, to allow for working around vanilla's
     * {@link net.minecraft.client.renderer.blockentity.state.ChestRenderState.ChestMaterialType} enum class.
     */
    public static class SingleChestRenderState extends ChestRenderState {
        /**
         * The chest material.
         */
        public Material chestMaterial = Sheets.CHEST_LOCATION;

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
