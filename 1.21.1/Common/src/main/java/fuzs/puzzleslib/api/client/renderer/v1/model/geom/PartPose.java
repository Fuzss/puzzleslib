package fuzs.puzzleslib.api.client.renderer.v1.model.geom;

/**
 * Copied from Minecraft 1.21.10.
 */
public class PartPose extends net.minecraft.client.model.geom.PartPose {
    public static final PartPose ZERO = new PartPose(net.minecraft.client.model.geom.PartPose.ZERO);

    public final float xScale;
    public final float yScale;
    public final float zScale;

    public PartPose(float x, float y, float z, float xRot, float yRot, float zRot, float xScale, float yScale, float zScale) {
        super(x, y, z, xRot, yRot, zRot);
        this.xScale = xScale;
        this.yScale = yScale;
        this.zScale = zScale;
    }

    public PartPose(net.minecraft.client.model.geom.PartPose partPose) {
        this(partPose.x, partPose.y, partPose.z, partPose.xRot, partPose.yRot, partPose.zRot, 1.0F, 1.0F, 1.0F);
    }

    public static PartPose offset(float x, float y, float z) {
        return new PartPose(net.minecraft.client.model.geom.PartPose.offset(x, y, z));
    }

    public static PartPose rotation(float xRot, float yRot, float zRot) {
        return new PartPose(net.minecraft.client.model.geom.PartPose.rotation(xRot, yRot, zRot));
    }

    public static PartPose offsetAndRotation(float x, float y, float z, float xRot, float yRot, float zRot) {
        return new PartPose(net.minecraft.client.model.geom.PartPose.offsetAndRotation(x, y, z, xRot, yRot, zRot));
    }

    public PartPose translated(float x, float y, float z) {
        return new PartPose(this.x + x,
                this.y + y,
                this.z + z,
                this.xRot,
                this.yRot,
                this.zRot,
                this.xScale,
                this.yScale,
                this.zScale);
    }

    public PartPose withScale(float scale) {
        return new PartPose(this.x, this.y, this.z, this.xRot, this.yRot, this.zRot, scale, scale, scale);
    }

    public PartPose scaled(float scale) {
        return scale == 1.0F ? this : this.scaled(scale, scale, scale);
    }

    public PartPose scaled(float x, float y, float z) {
        return new PartPose(this.x * x,
                this.y * y,
                this.z * z,
                this.xRot,
                this.yRot,
                this.zRot,
                this.xScale * x,
                this.yScale * y,
                this.zScale * z);
    }
}
