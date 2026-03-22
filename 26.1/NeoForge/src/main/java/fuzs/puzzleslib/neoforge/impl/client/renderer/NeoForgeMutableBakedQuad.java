package fuzs.puzzleslib.neoforge.impl.client.renderer;

import fuzs.puzzleslib.api.client.renderer.v1.model.MutableBakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.neoforged.neoforge.client.model.quad.BakedColors;
import net.neoforged.neoforge.client.model.quad.BakedNormals;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class NeoForgeMutableBakedQuad extends MutableBakedQuad {
    @Nullable
    protected BakedNormals bakedNormals;
    @Nullable
    protected Integer packedNormal0;
    @Nullable
    protected Integer packedNormal1;
    @Nullable
    protected Integer packedNormal2;
    @Nullable
    protected Integer packedNormal3;
    protected boolean computeQuadNormals;
    protected BakedColors bakedColors;
    @Nullable
    protected Integer packedColor0;
    @Nullable
    protected Integer packedColor1;
    @Nullable
    protected Integer packedColor2;
    @Nullable
    protected Integer packedColor3;
    protected boolean hasAmbientOcclusion;

    public NeoForgeMutableBakedQuad(BakedQuad bakedQuad) {
        super(bakedQuad);
        this.bakedNormals = bakedQuad.bakedNormals();
        this.bakedColors = bakedQuad.bakedColors();
        this.hasAmbientOcclusion = bakedQuad.hasAmbientOcclusion();
    }

    public BakedNormals bakedNormals() {
        if (this.computeQuadNormals) {
            return BakedNormals.of(BakedNormals.computeQuadNormal(this.position0,
                    this.position1,
                    this.position2,
                    this.position3));
        } else if (this.packedNormal0 != null && this.packedNormal1 != null && this.packedNormal2 != null
                && this.packedNormal3 != null) {
            // the values all being equal is handled automatically
            return BakedNormals.of(this.packedNormal0, this.packedNormal1, this.packedNormal2, this.packedNormal3);
        } else {
            Objects.requireNonNull(this.bakedNormals, "baked normals is null");
            return this.bakedNormals;
        }
    }

    public BakedColors bakedColors() {
        if (this.packedColor0 != null && this.packedColor1 != null && this.packedColor2 != null
                && this.packedColor3 != null) {
            // the values all being equal is handled automatically
            return BakedColors.of(this.packedColor0, this.packedColor1, this.packedColor2, this.packedColor3);
        } else {
            return this.bakedColors;
        }
    }

    public boolean hasAmbientOcclusion() {
        return this.hasAmbientOcclusion;
    }

    @Override
    public MutableBakedQuad position0(Vector3fc position) {
        if (this.bakedNormals != BakedNormals.UNSPECIFIED) {
            this.bakedNormals = null;
        }

        return super.position0(position);
    }

    @Override
    public MutableBakedQuad position1(Vector3fc position) {
        if (this.bakedNormals != BakedNormals.UNSPECIFIED) {
            this.bakedNormals = null;
        }

        return super.position1(position);
    }

    @Override
    public MutableBakedQuad position2(Vector3fc position) {
        if (this.bakedNormals != BakedNormals.UNSPECIFIED) {
            this.bakedNormals = null;
        }

        return super.position2(position);
    }

    @Override
    public MutableBakedQuad position3(Vector3fc position) {
        if (this.bakedNormals != BakedNormals.UNSPECIFIED) {
            this.bakedNormals = null;
        }

        return super.position3(position);
    }

    @Override
    public MutableBakedQuad packedNormal0(int packedNormal) {
        this.packedNormal0 = packedNormal;
        return this;
    }

    @Override
    public MutableBakedQuad packedNormal1(int packedNormal) {
        this.packedNormal1 = packedNormal;
        return this;
    }

    @Override
    public MutableBakedQuad packedNormal2(int packedNormal) {
        this.packedNormal2 = packedNormal;
        return this;
    }

    @Override
    public MutableBakedQuad packedNormal3(int packedNormal) {
        this.packedNormal3 = packedNormal;
        return this;
    }

    @Override
    public MutableBakedQuad computeQuadNormals() {
        this.computeQuadNormals = true;
        return super.computeQuadNormals();
    }

    @Override
    public MutableBakedQuad packedColor0(int packedColor) {
        this.packedColor0 = packedColor;
        return this;
    }

    @Override
    public MutableBakedQuad packedColor1(int packedColor) {
        this.packedColor1 = packedColor;
        return this;
    }

    @Override
    public MutableBakedQuad packedColor2(int packedColor) {
        this.packedColor2 = packedColor;
        return this;
    }

    @Override
    public MutableBakedQuad packedColor3(int packedColor) {
        this.packedColor3 = packedColor;
        return this;
    }

    @Override
    public MutableBakedQuad hasAmbientOcclusion(boolean hasAmbientOcclusion) {
        this.hasAmbientOcclusion = hasAmbientOcclusion;
        return super.hasAmbientOcclusion(hasAmbientOcclusion);
    }

    @Override
    public BakedQuad toImmutable() {
        return new BakedQuad(this.position0(),
                this.position1(),
                this.position2(),
                this.position3(),
                this.packedUV0(),
                this.packedUV1(),
                this.packedUV2(),
                this.packedUV3(),
                this.tintIndex(),
                this.direction(),
                this.sprite(),
                this.shade(),
                this.lightEmission(),
                this.bakedNormals(),
                this.bakedColors(),
                this.hasAmbientOcclusion());
    }
}
