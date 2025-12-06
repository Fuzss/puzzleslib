package fuzs.puzzleslib.api.client.renderer.v1.model.geom.builders;

import fuzs.puzzleslib.api.client.renderer.v1.model.geom.PartPose;

import java.util.function.Function;

/**
 * Copied from Minecraft 1.21.10.
 */
@FunctionalInterface
public interface MeshTransformer {
    MeshTransformer IDENTITY = Function.<MeshDefinition>identity()::apply;

    static MeshTransformer scaling(float f) {
        float g = 24.016F * (1.0F - f);
        return mesh -> mesh.transformed((PartPose partPose) -> {
            return partPose.scaled(f).translated(0.0F, g, 0.0F);
        });
    }

    MeshDefinition apply(MeshDefinition meshDefinition);
}
