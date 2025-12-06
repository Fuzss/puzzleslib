package fuzs.puzzleslib.api.client.renderer.v1.model.geom;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Copied from Minecraft 1.21.10.
 */
public class ModelPart extends net.minecraft.client.model.geom.ModelPart {

    public ModelPart(List<Cube> cubes, Map<String, ModelPart> children) {
        super(cubes, (Map<String, net.minecraft.client.model.geom.ModelPart>) (Map<?, ?>) children);
    }

    public ModelPart(net.minecraft.client.model.geom.ModelPart modelPart) {
        this(modelPart.cubes,
                modelPart.children.entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                (Map.Entry<String, net.minecraft.client.model.geom.ModelPart> entry) -> {
                                    return new ModelPart(entry.getValue());
                                })));
    }

    @Override
    public PartPose storePose() {
        return PartPose.offsetAndRotation(this.x, this.y, this.z, this.xRot, this.yRot, this.zRot);
    }

    @Override
    public PartPose getInitialPose() {
        return (PartPose) super.getInitialPose();
    }

    @Override
    public void setInitialPose(net.minecraft.client.model.geom.PartPose initialPose) {
        super.setInitialPose(initialPose instanceof PartPose ? initialPose : new PartPose(initialPose));
    }

    @Override
    public void loadPose(net.minecraft.client.model.geom.PartPose partPose) {
        super.loadPose(partPose);
        if (partPose instanceof PartPose) {
            this.xScale = ((PartPose) partPose).xScale;
            this.yScale = ((PartPose) partPose).yScale;
            this.zScale = ((PartPose) partPose).zScale;
        }
    }
}
