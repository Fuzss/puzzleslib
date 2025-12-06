package fuzs.puzzleslib.api.client.renderer.v1.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Function;

/**
 * A backport of the modern {@link Model} class.
 */
public abstract class RootedModel extends Model {
    protected final ModelPart root;
    private final List<ModelPart> allParts;

    public RootedModel(ModelPart root, Function<ResourceLocation, RenderType> renderType) {
        super(renderType);
        this.root = root;
        this.allParts = root.getAllParts().toList();
    }

    @Override
    public final void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.root().render(poseStack, buffer, packedLight, packedOverlay, color);
    }

    public final ModelPart root() {
        return this.root;
    }

    public final List<ModelPart> allParts() {
        return this.allParts;
    }

    public final void resetPose() {
        for (ModelPart modelPart : this.allParts) {
            modelPart.resetPose();
        }
    }
}
