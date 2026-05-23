package fuzs.puzzleslib.api.client.renderer.v1.model.geom.builders;

import net.minecraft.client.model.geom.builders.MaterialDefinition;

/**
 * Copied from Minecraft 1.21.10.
 */
public class LayerDefinition extends net.minecraft.client.model.geom.builders.LayerDefinition {
    public final MeshDefinition mesh;

    public LayerDefinition(net.minecraft.client.model.geom.builders.LayerDefinition other) {
        this(other.mesh, other.material);
    }

    public LayerDefinition(net.minecraft.client.model.geom.builders.MeshDefinition mesh, MaterialDefinition material) {
        this(new MeshDefinition(mesh), material);
    }

    private LayerDefinition(MeshDefinition mesh, MaterialDefinition material) {
        super(mesh, material);
        this.mesh = mesh;
    }

    public LayerDefinition apply(MeshTransformer transformer) {
        return new LayerDefinition(transformer.apply(this.mesh), this.material);
    }

    public static LayerDefinition create(MeshDefinition mesh, int texWidth, int texHeight) {
        return new LayerDefinition(mesh, new MaterialDefinition(texWidth, texHeight));
    }
}
