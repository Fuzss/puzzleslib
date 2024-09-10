package fuzs.puzzleslib.api.client.gui.v2.components;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;

/**
 * A helper for drawing nine sliced sprites from a 256x256 texture source file, opposed to having to use vanilla's new
 * one-sprite-per-image system.
 */
public final class GuiGraphicsHelper {

    private GuiGraphicsHelper() {
        // NO-OP
    }

    /**
     * Draws a rectangular border frame, the inside of the shape is left untouched.
     *
     * @param guiGraphics gui graphics instance
     * @param posX        start x
     * @param posY        start y
     * @param width       rectangle width
     * @param height      rectangle height
     * @param borderSize  width of the border on all sides, goes inwards
     * @param color       color to fill with
     */
    public static void fillFrame(GuiGraphics guiGraphics, int posX, int posY, int width, int height, int borderSize, int color) {
        fillFrame(guiGraphics, posX, posY, width, height, borderSize, 0, color);
    }

    /**
     * Draws a rectangular border frame, the inside of the shape is left untouched.
     *
     * @param guiGraphics gui graphics instance
     * @param posX        start x
     * @param posY        start y
     * @param width       rectangle width
     * @param height      rectangle height
     * @param borderSize  width of the border on all sides, goes inwards
     * @param z           z offset
     * @param color       color to fill with
     */
    public static void fillFrame(GuiGraphics guiGraphics, int posX, int posY, int width, int height, int borderSize, int z, int color) {
        fillFrameArea(guiGraphics, posX, posY, posX + width, posY + height, borderSize, z, color);
    }

    /**
     * Draws a rectangular border frame, the inside of the shape is left untouched.
     *
     * @param guiGraphics gui graphics instance
     * @param minX        start x
     * @param minY        start y
     * @param maxX        end x
     * @param maxY        end y
     * @param borderSize  width of the border on all sides, goes inwards
     * @param z           z offset
     * @param color       color to fill with
     */
    public static void fillFrameArea(GuiGraphics guiGraphics, int minX, int minY, int maxX, int maxY, int borderSize, int z, int color) {
        // top
        guiGraphics.fill(minX, minY, maxX, minY + borderSize, z, color);
        // bottom
        guiGraphics.fill(minX, maxY - borderSize, maxX, maxY, z, color);
        // left
        guiGraphics.fill(minX, minY + borderSize, minX + borderSize, maxY - borderSize, z, color);
        // right
        guiGraphics.fill(maxX - borderSize, minY + borderSize, maxX, maxY - borderSize, z, color);
    }

    /**
     * Draws a rectangular border frame, the inside of the shape is left untouched.
     *
     * @param guiGraphics gui graphics instance
     * @param minX        start x
     * @param minY        start y
     * @param maxX        end x
     * @param maxY        end y
     * @param borderSize  width of the border on all sides, goes inwards
     * @param color       color to fill with
     */
    public static void fillFrameArea(GuiGraphics guiGraphics, int minX, int minY, int maxX, int maxY, int borderSize, int color) {
        fillFrameArea(guiGraphics, minX, minY, maxX, maxY, borderSize, 0, color);
    }

    public static void blitNineSliced(GuiGraphics guiGraphics, ResourceLocation resourceLocation, int x, int y, int width, int height, int border, int spriteWidth, int spriteHeight, int uOffset, int vOffset) {
        blitNineSliced(guiGraphics, resourceLocation, x, y, width, height, border, border, border, border, spriteWidth,
                spriteHeight, uOffset, vOffset
        );
    }

    public static void blitNineSliced(GuiGraphics guiGraphics, ResourceLocation resourceLocation, int x, int y, int width, int height, int left, int top, int right, int bottom, int spriteWidth, int spriteHeight, int uOffset, int vOffset) {
        SingleTextureAtlasSprite textureAtlasSprite = new SingleTextureAtlasSprite(resourceLocation, spriteWidth,
                spriteHeight, uOffset, vOffset
        );
        GuiSpriteScaling.NineSlice nineSlice = new GuiSpriteScaling.NineSlice(spriteWidth, spriteHeight,
                new GuiSpriteScaling.NineSlice.Border(left, top, right, bottom)
        );
        guiGraphics.blitNineSlicedSprite(textureAtlasSprite, nineSlice, x, y, 0, width, height);
    }

    public static void blitNineSliced(GuiGraphics guiGraphics, ResourceLocation resourceLocation, int x, int y, int width, int height, int borderWidth, int borderHeight, int spriteWidth, int spriteHeight, int uOffset, int vOffset) {
        blitNineSliced(guiGraphics, resourceLocation, x, y, width, height, borderWidth, borderHeight, borderWidth,
                borderHeight, spriteWidth, spriteHeight, uOffset, vOffset
        );
    }

    private static class SingleTextureAtlasSprite extends TextureAtlasSprite {

        public SingleTextureAtlasSprite(ResourceLocation resourceLocation, int spriteWidth, int spriteHeight, int uOffset, int vOffset) {
            this(resourceLocation, spriteWidth, spriteHeight, uOffset, vOffset, 256, 256);
        }

        public SingleTextureAtlasSprite(ResourceLocation resourceLocation, int spriteWidth, int spriteHeight, int uOffset, int vOffset, int textureWidth, int textureHeight) {
            super(resourceLocation, new SpriteContents(resourceLocation, new FrameSize(spriteWidth, spriteHeight),
                    new NativeImage(textureWidth, textureHeight, false), ResourceMetadata.EMPTY
            ), textureWidth, textureHeight, uOffset, vOffset);
        }
    }
}
