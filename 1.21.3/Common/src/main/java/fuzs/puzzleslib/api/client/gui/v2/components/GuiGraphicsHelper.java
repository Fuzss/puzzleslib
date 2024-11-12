package fuzs.puzzleslib.api.client.gui.v2.components;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.impl.client.gui.SingleTextureAtlasSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.function.Function;

/**
 * A helper class for extending the functionality of {@link GuiGraphics}. Especially useful for drawing
 * {@link TextureAtlasSprite TextureAtlasSprites} with a different {@link GuiSpriteScaling} than is defined by the
 * resource pack.
 */
public final class GuiGraphicsHelper {

    private GuiGraphicsHelper() {
        // NO-OP
    }

    /**
     * Creates an on demand {@link GuiGraphics} instance from a provided {@link PoseStack}.
     *
     * @param poseStack the pose stack
     * @return the gui graphics instance
     */
    public static GuiGraphics create(PoseStack poseStack) {
        return create(poseStack.last().pose());
    }

    /**
     * Creates an on demand {@link GuiGraphics} instance from a provided {@link PoseStack}.
     *
     * @param matrix4f the matrix backing the pose stack
     * @return the gui graphics instance
     */
    public static GuiGraphics create(Matrix4f matrix4f) {
        Minecraft minecraft = Minecraft.getInstance();
        GuiGraphics guiGraphics = new GuiGraphics(minecraft, minecraft.renderBuffers().bufferSource());
        guiGraphics.pose().mulPose(matrix4f);
        return guiGraphics;
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

    /**
     * Allows for drawing any sprite from a texture sheet in nine-sliced mode. This does not require the sprite to be
     * stitched onto an atlas.
     * <p>
     * The width &amp; height dimensions are filled by repeatedly drawing pre-defined slices of the original sprite.
     *
     * @param guiGraphics      the gui graphics instance
     * @param renderType       the render type, usually {@link RenderType#guiTextured(ResourceLocation)}
     * @param resourceLocation the texture sheet resource location
     * @param x                the x-position on the screen
     * @param y                the y-position on the screen
     * @param width            the width to draw
     * @param height           the height to draw
     * @param borderSize       the border width &amp; height on the sides of the sprite, for drawing the frame
     * @param spriteWidth      the sprite texture width
     * @param spriteHeight     the sprite texture height
     * @param uOffset          the sprite u-offset on the texture sheet
     * @param vOffset          the sprite v-offset on the texture sheet
     */
    public static void blitNineSliced(GuiGraphics guiGraphics, Function<ResourceLocation, RenderType> renderType, ResourceLocation resourceLocation, int x, int y, int width, int height, int borderSize, int spriteWidth, int spriteHeight, int uOffset, int vOffset) {
        blitNineSliced(guiGraphics, renderType, resourceLocation, x, y, width, height, borderSize, borderSize,
                spriteWidth, spriteHeight, uOffset, vOffset
        );
    }

    /**
     * Allows for drawing any sprite from a texture sheet in nine-sliced mode. This does not require the sprite to be
     * stitched onto an atlas.
     * <p>
     * The width &amp; height dimensions are filled by repeatedly drawing pre-defined slices of the original sprite.
     *
     * @param guiGraphics      the gui graphics instance
     * @param renderType       the render type, usually {@link RenderType#guiTextured(ResourceLocation)}
     * @param resourceLocation the texture sheet resource location
     * @param x                the x-position on the screen
     * @param y                the y-position on the screen
     * @param width            the width to draw
     * @param height           the height to draw
     * @param borderWidth      the border width on the sides of the sprite, for drawing the frame
     * @param borderHeight     the border height on the sides of the sprite, for drawing the frame
     * @param spriteWidth      the sprite texture width
     * @param spriteHeight     the sprite texture height
     * @param uOffset          the sprite u-offset on the texture sheet
     * @param vOffset          the sprite v-offset on the texture sheet
     */
    public static void blitNineSliced(GuiGraphics guiGraphics, Function<ResourceLocation, RenderType> renderType, ResourceLocation resourceLocation, int x, int y, int width, int height, int borderWidth, int borderHeight, int spriteWidth, int spriteHeight, int uOffset, int vOffset) {
        blitNineSliced(guiGraphics, renderType, resourceLocation, x, y, width, height, borderWidth, borderHeight,
                borderWidth, borderHeight, spriteWidth, spriteHeight, uOffset, vOffset
        );
    }

    /**
     * Allows for drawing any sprite from a texture sheet in nine-sliced mode. This does not require the sprite to be
     * stitched onto an atlas.
     * <p>
     * The width &amp; height dimensions are filled by repeatedly drawing pre-defined slices of the original sprite.
     *
     * @param guiGraphics      the gui graphics instance
     * @param renderType       the render type, usually {@link RenderType#guiTextured(ResourceLocation)}
     * @param resourceLocation the texture sheet resource location
     * @param x                the x-position on the screen
     * @param y                the y-position on the screen
     * @param width            the width to draw
     * @param height           the height to draw
     * @param borderLeft       the border width on the left side of the sprite, for drawing the frame
     * @param borderTop        the border height on the top side of the sprite, for drawing the frame
     * @param borderRight      the border width on the right side of the sprite, for drawing the frame
     * @param borderBottom     the border height on the bottom side of the sprite, for drawing the frame
     * @param spriteWidth      the sprite texture width
     * @param spriteHeight     the sprite texture height
     * @param uOffset          the sprite u-offset on the texture sheet
     * @param vOffset          the sprite v-offset on the texture sheet
     */
    public static void blitNineSliced(GuiGraphics guiGraphics, Function<ResourceLocation, RenderType> renderType, ResourceLocation resourceLocation, int x, int y, int width, int height, int borderLeft, int borderTop, int borderRight, int borderBottom, int spriteWidth, int spriteHeight, int uOffset, int vOffset) {
        blitNineSliced(guiGraphics, renderType, resourceLocation, x, y, 0, width, height, borderLeft, borderTop,
                borderRight, borderBottom, spriteWidth, spriteHeight, uOffset, vOffset, 256, 256
        );
    }

    /**
     * Allows for drawing any sprite from a texture sheet in nine-sliced mode. This does not require the sprite to be
     * stitched onto an atlas.
     * <p>
     * The width &amp; height dimensions are filled by repeatedly drawing pre-defined slices of the original sprite.
     *
     * @param guiGraphics      the gui graphics instance
     * @param renderType       the render type, usually {@link RenderType#guiTextured(ResourceLocation)}
     * @param resourceLocation the texture sheet resource location
     * @param x                the x-position on the screen
     * @param y                the y-position on the screen
     * @param width            the width to draw
     * @param height           the height to draw
     * @param borderLeft       the border width on the left side of the sprite, for drawing the frame
     * @param borderTop        the border height on the top side of the sprite, for drawing the frame
     * @param borderRight      the border width on the right side of the sprite, for drawing the frame
     * @param borderBottom     the border height on the bottom side of the sprite, for drawing the frame
     * @param spriteWidth      the sprite texture width
     * @param spriteHeight     the sprite texture height
     * @param uOffset          the sprite u-offset on the texture sheet
     * @param vOffset          the sprite v-offset on the texture sheet
     * @param textureWidth     the texture sheet width
     * @param textureHeight    the texture sheet height
     */
    public static void blitNineSliced(GuiGraphics guiGraphics, Function<ResourceLocation, RenderType> renderType, ResourceLocation resourceLocation, int x, int y, int width, int height, int borderLeft, int borderTop, int borderRight, int borderBottom, int spriteWidth, int spriteHeight, int uOffset, int vOffset, int textureWidth, int textureHeight) {
        blitNineSliced(guiGraphics, renderType, resourceLocation, x, y, 0, width, height, borderLeft, borderTop,
                borderRight, borderBottom, spriteWidth, spriteHeight, uOffset, vOffset, textureWidth, textureHeight
        );
    }

    /**
     * Allows for drawing any sprite from a texture sheet in nine-sliced mode. This does not require the sprite to be
     * stitched onto an atlas.
     * <p>
     * The width &amp; height dimensions are filled by repeatedly drawing pre-defined slices of the original sprite.
     *
     * @param guiGraphics      the gui graphics instance
     * @param renderType       the render type, usually {@link RenderType#guiTextured(ResourceLocation)}
     * @param resourceLocation the texture sheet resource location
     * @param x                the x-position on the screen
     * @param y                the y-position on the screen
     * @param blitOffset       the z-level offset
     * @param width            the width to draw
     * @param height           the height to draw
     * @param borderLeft       the border width on the left side of the sprite, for drawing the frame
     * @param borderTop        the border height on the top side of the sprite, for drawing the frame
     * @param borderRight      the border width on the right side of the sprite, for drawing the frame
     * @param borderBottom     the border height on the bottom side of the sprite, for drawing the frame
     * @param spriteWidth      the sprite texture width
     * @param spriteHeight     the sprite texture height
     * @param uOffset          the sprite u-offset on the texture sheet
     * @param vOffset          the sprite v-offset on the texture sheet
     * @param textureWidth     the texture sheet width
     * @param textureHeight    the texture sheet height
     */
    public static void blitNineSliced(GuiGraphics guiGraphics, Function<ResourceLocation, RenderType> renderType, ResourceLocation resourceLocation, int x, int y, int blitOffset, int width, int height, int borderLeft, int borderTop, int borderRight, int borderBottom, int spriteWidth, int spriteHeight, int uOffset, int vOffset, int textureWidth, int textureHeight) {
        SingleTextureAtlasSprite textureAtlasSprite = new SingleTextureAtlasSprite(resourceLocation, spriteWidth,
                spriteHeight, uOffset, vOffset, textureWidth, textureHeight
        );
        GuiSpriteScaling.NineSlice nineSlice = new GuiSpriteScaling.NineSlice(spriteWidth, spriteHeight,
                new GuiSpriteScaling.NineSlice.Border(borderLeft, borderTop, borderRight, borderBottom), false
        );
        guiGraphics.blitNineSlicedSprite(renderType, textureAtlasSprite, nineSlice, x, y, blitOffset, width, height);
    }

    /**
     * Allows for manually drawing any sprite using nine-sliced mode.
     * <p>
     * The width &amp; height dimensions are filled by repeatedly drawing pre-defined slices of the original sprite.
     *
     * @param guiGraphics the gui graphics instance
     * @param renderType  the render type, usually {@link RenderType#guiTextured(ResourceLocation)}
     * @param sprite      the sprite resource location
     * @param x           the x-position on the screen
     * @param y           the y-position on the screen
     * @param width       the width to draw
     * @param height      the height to draw
     * @param borderSize  the border width &amp; height on the sides of the sprite, for drawing the frame
     */
    public static void blitNineSlicedSprite(GuiGraphics guiGraphics, Function<ResourceLocation, RenderType> renderType, ResourceLocation sprite, int x, int y, int width, int height, int borderSize) {
        blitNineSlicedSprite(guiGraphics, renderType, sprite, x, y, 0, width, height, borderSize, borderSize,
                borderSize, borderSize
        );
    }

    /**
     * Allows for manually drawing any sprite using nine-sliced mode.
     * <p>
     * The width &amp; height dimensions are filled by repeatedly drawing pre-defined slices of the original sprite.
     *
     * @param guiGraphics  the gui graphics instance
     * @param renderType   the render type, usually {@link RenderType#guiTextured(ResourceLocation)}
     * @param sprite       the sprite resource location
     * @param x            the x-position on the screen
     * @param y            the y-position on the screen
     * @param width        the width to draw
     * @param height       the height to draw
     * @param borderLeft   the border width on the left side of the sprite, for drawing the frame
     * @param borderTop    the border height on the top side of the sprite, for drawing the frame
     * @param borderRight  the border width on the right side of the sprite, for drawing the frame
     * @param borderBottom the border height on the bottom side of the sprite, for drawing the frame
     */
    public static void blitNineSlicedSprite(GuiGraphics guiGraphics, Function<ResourceLocation, RenderType> renderType, ResourceLocation sprite, int x, int y, int width, int height, int borderLeft, int borderTop, int borderRight, int borderBottom) {
        blitNineSlicedSprite(guiGraphics, renderType, sprite, x, y, 0, width, height, borderLeft, borderTop,
                borderRight, borderBottom
        );
    }

    /**
     * Allows for manually drawing any sprite using nine-sliced mode.
     * <p>
     * The width &amp; height dimensions are filled by repeatedly drawing pre-defined slices of the original sprite.
     *
     * @param guiGraphics  the gui graphics instance
     * @param renderType   the render type, usually {@link RenderType#guiTextured(ResourceLocation)}
     * @param sprite       the sprite resource location
     * @param x            the x-position on the screen
     * @param y            the y-position on the screen
     * @param blitOffset   the z-level offset
     * @param width        the width to draw
     * @param height       the height to draw
     * @param borderLeft   the border width on the left side of the sprite, for drawing the frame
     * @param borderTop    the border height on the top side of the sprite, for drawing the frame
     * @param borderRight  the border width on the right side of the sprite, for drawing the frame
     * @param borderBottom the border height on the bottom side of the sprite, for drawing the frame
     */
    public static void blitNineSlicedSprite(GuiGraphics guiGraphics, Function<ResourceLocation, RenderType> renderType, ResourceLocation sprite, int x, int y, int blitOffset, int width, int height, int borderLeft, int borderTop, int borderRight, int borderBottom) {
        TextureAtlasSprite textureAtlasSprite = Minecraft.getInstance().getGuiSprites().getSprite(sprite);
        GuiSpriteScaling.NineSlice nineSlice = new GuiSpriteScaling.NineSlice(textureAtlasSprite.contents().width(),
                textureAtlasSprite.contents().height(),
                new GuiSpriteScaling.NineSlice.Border(borderLeft, borderTop, borderRight, borderBottom), false
        );
        guiGraphics.blitNineSlicedSprite(renderType, textureAtlasSprite, nineSlice, x, y, blitOffset, width, height);
    }

    /**
     * Manually draws a sprite using tile mode. Width &amp; height portions outside the texture dimensions are filled by
     * repeating the original texture.
     * <p>
     * Most sprites by default use the stretch mode, which squeezes them into the provided width &amp; height.
     *
     * @param guiGraphics  the gui graphics instance
     * @param renderType   the render type, usually {@link RenderType#guiTextured(ResourceLocation)}
     * @param sprite       the sprite resource location
     * @param x            the x-position on the screen
     * @param y            the y-position on the screen
     * @param width        the width to draw
     * @param height       the height to draw
     * @param spriteWidth  the sprite texture file width
     * @param spriteHeight the sprite texture file height
     */
    public static void blitTiledSprite(GuiGraphics guiGraphics, Function<ResourceLocation, RenderType> renderType, ResourceLocation sprite, int x, int y, int width, int height, int spriteWidth, int spriteHeight) {
        blitTiledSprite(guiGraphics, renderType, sprite, x, y, 0, width, height, spriteWidth, spriteHeight);
    }

    /**
     * Manually draws a sprite using tile mode. Width &amp; height portions outside the texture dimensions are filled by
     * repeating the original texture.
     * <p>
     * Most sprites by default use the stretch mode, which squeezes them into the provided width &amp; height.
     *
     * @param guiGraphics  the gui graphics instance
     * @param renderType   the render type, usually {@link RenderType#guiTextured(ResourceLocation)}
     * @param sprite       the sprite resource location
     * @param x            the x-position on the screen
     * @param y            the y-position on the screen
     * @param blitOffset   the z-level offset
     * @param width        the width to draw
     * @param height       the height to draw
     * @param spriteWidth  the sprite texture file width
     * @param spriteHeight the sprite texture file height
     */
    public static void blitTiledSprite(GuiGraphics guiGraphics, Function<ResourceLocation, RenderType> renderType, ResourceLocation sprite, int x, int y, int blitOffset, int width, int height, int spriteWidth, int spriteHeight) {
        blitTiledSprite(guiGraphics, renderType, sprite, x, y, blitOffset, width, height, spriteWidth, spriteHeight, 0,
                0
        );
    }

    /**
     * Manually draws a sprite using tile mode. Width &amp; height portions outside the texture dimensions are filled by
     * repeating the original texture.
     * <p>
     * Most sprites by default use the stretch mode, which squeezes them into the provided width &amp; height.
     *
     * @param guiGraphics  the gui graphics instance
     * @param renderType   the render type, usually {@link RenderType#guiTextured(ResourceLocation)}
     * @param sprite       the sprite resource location
     * @param x            the x-position on the screen
     * @param y            the y-position on the screen
     * @param blitOffset   the z-level offset
     * @param width        the width to draw
     * @param height       the height to draw
     * @param spriteWidth  the sprite texture file width
     * @param spriteHeight the sprite texture file height
     * @param uOffset      the sprite u-offset on the texture sheet
     * @param vOffset      the sprite v-offset on the texture sheet
     */
    public static void blitTiledSprite(GuiGraphics guiGraphics, Function<ResourceLocation, RenderType> renderType, ResourceLocation sprite, int x, int y, int blitOffset, int width, int height, int spriteWidth, int spriteHeight, int uOffset, int vOffset) {
        Minecraft minecraft = Minecraft.getInstance();
        TextureAtlasSprite textureatlassprite = minecraft.getGuiSprites().getSprite(sprite);
        guiGraphics.blitTiledSprite(renderType, textureatlassprite, x, y, blitOffset, width, height, uOffset, vOffset,
                spriteWidth, spriteHeight, spriteWidth, spriteHeight
        );
    }
}
