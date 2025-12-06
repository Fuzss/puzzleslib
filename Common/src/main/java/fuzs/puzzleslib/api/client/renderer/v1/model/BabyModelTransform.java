package fuzs.puzzleslib.api.client.renderer.v1.model;

import fuzs.puzzleslib.api.client.renderer.v1.model.geom.PartPose;
import fuzs.puzzleslib.api.client.renderer.v1.model.geom.builders.MeshDefinition;
import fuzs.puzzleslib.api.client.renderer.v1.model.geom.builders.MeshTransformer;
import fuzs.puzzleslib.api.client.renderer.v1.model.geom.builders.PartDefinition;

import java.util.Map.Entry;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * Copied from Minecraft 1.21.10.
 */
public record BabyModelTransform(boolean scaleHead,
                                 float babyYHeadOffset,
                                 float babyZHeadOffset,
                                 float babyHeadScale,
                                 float babyBodyScale,
                                 float bodyYOffset,
                                 Set<String> headParts) implements MeshTransformer {

    public BabyModelTransform(Set<String> headParts) {
        this(false, 5.0F, 2.0F, headParts);
    }

    public BabyModelTransform(boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset, Set<String> headParts) {
        this(scaleHead, babyYHeadOffset, babyZHeadOffset, 2.0F, 2.0F, 24.0F, headParts);
    }

    @Override
    public MeshDefinition apply(MeshDefinition meshDefinition) {
        float f = this.scaleHead ? 1.5F / this.babyHeadScale : 1.0F;
        float g = 1.0F / this.babyBodyScale;
        UnaryOperator<PartPose> unaryOperator = (PartPose partPose) -> {
            return partPose.translated(0.0F, this.babyYHeadOffset, this.babyZHeadOffset).scaled(f);
        };
        UnaryOperator<PartPose> unaryOperator2 = (PartPose partPose) -> {
            return partPose.translated(0.0F, this.bodyYOffset, 0.0F).scaled(g);
        };
        MeshDefinition meshDefinition2 = new MeshDefinition();

        for (Entry<String, PartDefinition> entry : meshDefinition.getRoot().getChildren()) {
            String string = entry.getKey();
            PartDefinition partDefinition = entry.getValue();
            meshDefinition2.getRoot()
                    .addOrReplaceChild(string,
                            partDefinition.transformed(
                                    this.headParts.contains(string) ? unaryOperator : unaryOperator2));
        }

        return meshDefinition2;
    }
}
