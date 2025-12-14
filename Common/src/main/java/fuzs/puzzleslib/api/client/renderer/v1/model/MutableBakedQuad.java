package fuzs.puzzleslib.api.client.renderer.v1.model;

import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.joml.Vector3fc;

public abstract class MutableBakedQuad {
    protected Vector3fc position0;
    protected Vector3fc position1;
    protected Vector3fc position2;
    protected Vector3fc position3;

    protected long packedUV0;
    protected long packedUV1;
    protected long packedUV2;
    protected long packedUV3;

    protected int tintIndex;
    protected Direction direction;
    protected TextureAtlasSprite sprite;
    protected boolean shade;
    protected int lightEmission;

    public MutableBakedQuad(BakedQuad bakedQuad) {
        this.position0 = bakedQuad.position0();
        this.position1 = bakedQuad.position1();
        this.position2 = bakedQuad.position2();
        this.position3 = bakedQuad.position3();

        this.packedUV0 = bakedQuad.packedUV0();
        this.packedUV1 = bakedQuad.packedUV1();
        this.packedUV2 = bakedQuad.packedUV2();
        this.packedUV3 = bakedQuad.packedUV3();

        this.tintIndex = bakedQuad.tintIndex();
        this.direction = bakedQuad.direction();
        this.sprite = bakedQuad.sprite();
        this.shade = bakedQuad.shade();
        this.lightEmission = bakedQuad.lightEmission();
    }

    public static MutableBakedQuad toMutable(BakedQuad bakedQuad) {
        return ClientProxyImpl.get().getMutableBakedQuad(bakedQuad);
    }

    public Vector3fc position0() {
        return this.position0;
    }

    public Vector3fc position1() {
        return this.position1;
    }

    public Vector3fc position2() {
        return this.position2;
    }

    public Vector3fc position3() {
        return this.position3;
    }

    public Vector3fc position(int vertexIndex) {
        return switch (vertexIndex) {
            case 0 -> this.position0();
            case 1 -> this.position1();
            case 2 -> this.position2();
            case 3 -> this.position3();
            default -> throw new IndexOutOfBoundsException(vertexIndex);
        };
    }

    public long packedUV0() {
        return this.packedUV0;
    }

    public long packedUV1() {
        return this.packedUV1;
    }

    public long packedUV2() {
        return this.packedUV2;
    }

    public long packedUV3() {
        return this.packedUV3;
    }

    public long packedUV(int vertexIndex) {
        return switch (vertexIndex) {
            case 0 -> this.packedUV0();
            case 1 -> this.packedUV1();
            case 2 -> this.packedUV2();
            case 3 -> this.packedUV3();
            default -> throw new IndexOutOfBoundsException(vertexIndex);
        };
    }

    public int tintIndex() {
        return this.tintIndex;
    }

    public Direction direction() {
        return this.direction;
    }

    public TextureAtlasSprite sprite() {
        return this.sprite;
    }

    public boolean shade() {
        return this.shade;
    }

    public int lightEmission() {
        return this.lightEmission;
    }

    public MutableBakedQuad position0(Vector3fc position) {
        this.position0 = position;
        return this;
    }

    public MutableBakedQuad position1(Vector3fc position) {
        this.position1 = position;
        return this;
    }

    public MutableBakedQuad position2(Vector3fc position) {
        this.position2 = position;
        return this;
    }

    public MutableBakedQuad position3(Vector3fc position) {
        this.position3 = position;
        return this;
    }

    public MutableBakedQuad position(int vertexIndex, Vector3fc position) {
        return switch (vertexIndex) {
            case 0 -> this.position0(position);
            case 1 -> this.position1(position);
            case 2 -> this.position2(position);
            case 3 -> this.position3(position);
            default -> throw new IndexOutOfBoundsException(vertexIndex);
        };
    }

    public MutableBakedQuad packedUV0(long packedUV) {
        this.packedUV0 = packedUV;
        return this;
    }

    public MutableBakedQuad packedUV1(long packedUV) {
        this.packedUV1 = packedUV;
        return this;
    }

    public MutableBakedQuad packedUV2(long packedUV) {
        this.packedUV2 = packedUV;
        return this;
    }

    public MutableBakedQuad packedUV3(long packedUV) {
        this.packedUV3 = packedUV;
        return this;
    }

    public MutableBakedQuad packedUV(int vertexIndex, long packedUV) {
        return switch (vertexIndex) {
            case 0 -> this.packedUV0(packedUV);
            case 1 -> this.packedUV1(packedUV);
            case 2 -> this.packedUV2(packedUV);
            case 3 -> this.packedUV3(packedUV);
            default -> throw new IndexOutOfBoundsException(vertexIndex);
        };
    }

    public MutableBakedQuad tintIndex(int tintIndex) {
        this.tintIndex = tintIndex;
        return this;
    }

    public MutableBakedQuad direction(Direction direction) {
        this.direction = direction;
        return this;
    }

    public MutableBakedQuad sprite(TextureAtlasSprite sprite) {
        this.sprite = sprite;
        return this;
    }

    public MutableBakedQuad shade(boolean shade) {
        this.shade = shade;
        return this;
    }

    public MutableBakedQuad lightEmission(int lightEmission) {
        this.lightEmission = lightEmission;
        return this;
    }

    public MutableBakedQuad packedNormal0(int packedNormal) {
        return this;
    }

    public MutableBakedQuad packedNormal1(int packedNormal) {
        return this;
    }

    public MutableBakedQuad packedNormal2(int packedNormal) {
        return this;
    }

    public MutableBakedQuad packedNormal3(int packedNormal) {
        return this;
    }

    public MutableBakedQuad packedNormal(int vertexIndex, int packedNormal) {
        return switch (vertexIndex) {
            case 0 -> this.packedNormal0(packedNormal);
            case 1 -> this.packedNormal1(packedNormal);
            case 2 -> this.packedNormal2(packedNormal);
            case 3 -> this.packedNormal3(packedNormal);
            default -> throw new IndexOutOfBoundsException(vertexIndex);
        };
    }

    public MutableBakedQuad packedNormal(int packedNormal) {
        return this.packedNormal0(packedNormal)
                .packedNormal1(packedNormal)
                .packedNormal2(packedNormal)
                .packedNormal3(packedNormal);
    }

    public MutableBakedQuad computeQuadNormals() {
        return this;
    }

    public MutableBakedQuad packedColor0(int packedColor) {
        return this;
    }

    public MutableBakedQuad packedColor1(int packedColor) {
        return this;
    }

    public MutableBakedQuad packedColor2(int packedColor) {
        return this;
    }

    public MutableBakedQuad packedColor3(int packedColor) {
        return this;
    }

    public MutableBakedQuad packedColor(int vertexIndex, int packedColor) {
        return switch (vertexIndex) {
            case 0 -> this.packedColor0(packedColor);
            case 1 -> this.packedColor1(packedColor);
            case 2 -> this.packedColor2(packedColor);
            case 3 -> this.packedColor3(packedColor);
            default -> throw new IndexOutOfBoundsException(vertexIndex);
        };
    }

    public MutableBakedQuad packedColor(int packedColor) {
        return this.packedColor0(packedColor)
                .packedColor1(packedColor)
                .packedColor2(packedColor)
                .packedColor3(packedColor);
    }

    public MutableBakedQuad hasAmbientOcclusion(boolean hasAmbientOcclusion) {
        return this;
    }

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
                this.lightEmission());
    }
}
