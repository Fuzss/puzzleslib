package fuzs.puzzleslib.api.client.renderer.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.puzzleslib.api.client.renderer.v1.model.RootedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * An extension of {@link ChestRenderer} that allows specifying custom chest texture materials for single chests (e.g.
 * ender chest).
 *
 * @param <T> the chest block entity type
 * @param <M> the chest model type
 */
public abstract class SingleChestRenderer<T extends BlockEntity & LidBlockEntity, M extends SingleChestRenderer.ChestModel> extends ChestRenderer<T> {
    protected final M model;
    @Nullable
    private T blockEntity;
    @Nullable
    private Float partialTick;
    @Nullable
    private MultiBufferSource bufferSource;

    /**
     * @param context the renderer context
     * @param model   the single chest model
     */
    protected SingleChestRenderer(BlockEntityRendererProvider.Context context, M model) {
        super(context);
        this.model = model;
    }

    @Override
    public final void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.blockEntity = blockEntity;
        this.partialTick = partialTick;
        this.bufferSource = bufferSource;
        super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        this.blockEntity = null;
        this.partialTick = null;
        this.bufferSource = null;
    }

    @ApiStatus.Internal
    @Override
    protected final void render(PoseStack poseStack, VertexConsumer consumer, ModelPart lidPart, ModelPart lockPart, ModelPart bottomPart, float openness, int packedLight, int packedOverlay) {
        Objects.requireNonNull(this.blockEntity, "block entity is null");
        Objects.requireNonNull(this.partialTick, "partial tick is null");
        Objects.requireNonNull(this.bufferSource, "buffer source is null");
        this.model.setupAnim(openness);
        this.renderModel(this.blockEntity, this.partialTick, poseStack, this.bufferSource, packedLight, packedOverlay);
    }

    /**
     * Render the single chest model after everything on the pose stack has been set up.
     *
     * @param blockEntity   the block entity
     * @param partialTick   the partial tick time
     * @param poseStack     the pose stack
     * @param bufferSource  the multi buffer source
     * @param packedLight   the packed light
     * @param packedOverlay the packed overlay
     */
    protected void renderModel(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        VertexConsumer vertexConsumer = this.getChestMaterial(blockEntity, this.getXmasTextures())
                .buffer(bufferSource, RenderType::entityCutout);
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay);
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
     * @return should use holiday textures
     */
    protected boolean getXmasTextures() {
        return this.xmasTextures;
    }

    public static class ChestModel extends RootedModel {
        private final ModelPart lid;
        private final ModelPart lock;

        public ChestModel(ModelPart root) {
            super(root, RenderType::entitySolid);
            this.lid = root.getChild("lid");
            this.lock = root.getChild("lock");
        }

        public void setupAnim(float openness) {
            this.resetPose();
            this.lid.xRot = -(openness * (float) (Math.PI / 2));
            this.lock.xRot = this.lid.xRot;
        }
    }
}
