package fuzs.puzzleslib.api.client.gui.v2;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import fuzs.puzzleslib.impl.client.gui.SingleTextureAtlasSprite;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

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
     * Draws a string with a background, similar to the experience level on the gui.
     *
     * @param guiGraphics     the gui graphics instance
     * @param font            the font instance
     * @param component       the component to draw
     * @param posX            the x-position
     * @param posY            the y-position
     * @param color           the text color
     * @param backgroundColor the background text color
     * @see GuiGraphics#drawString(Font, Component, int, int, int)
     * @see Font#drawInBatch8xOutline(FormattedCharSequence, float, float, int, int, Matrix4f, MultiBufferSource,
     *         int)
     */
    public static void drawInBatch8xOutline(GuiGraphics guiGraphics, Font font, Component component, int posX, int posY, int color, int backgroundColor) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    guiGraphics.drawString(font, component, posX + i, posY + j, backgroundColor, false);
                }
            }
        }
        guiGraphics.drawString(font, component, posX, posY, color, false);
    }

    /**
     * Draws a rectangular border frame, the inside of the shape is left untouched.
     *
     * @param guiGraphics the gui graphics instance
     * @param posX        the x-position
     * @param posY        the y-position
     * @param width       the rectangle width
     * @param height      the rectangle height
     * @param borderSize  the width of the border on all sides, goes inwards
     * @param color       the color to fill with
     */
    public static void fillFrame(GuiGraphics guiGraphics, int posX, int posY, int width, int height, int borderSize, int color) {
        fillFrameArea(guiGraphics, posX, posY, posX + width, posY + height, borderSize, color);
    }

    /**
     * Draws a rectangular border frame, the inside of the shape is left untouched.
     *
     * @param guiGraphics the gui graphics instance
     * @param minX        the start x-position
     * @param minY        the start y-position
     * @param maxX        the end x-position
     * @param maxY        the end y-position
     * @param borderSize  the width of the border on all sides, goes inwards
     * @param color       the color to fill with
     */
    public static void fillFrameArea(GuiGraphics guiGraphics, int minX, int minY, int maxX, int maxY, int borderSize, int color) {
        // top
        guiGraphics.fill(minX, minY, maxX, minY + borderSize, color);
        // bottom
        guiGraphics.fill(minX, maxY - borderSize, maxX, maxY, color);
        // left
        guiGraphics.fill(minX, minY + borderSize, minX + borderSize, maxY - borderSize, color);
        // right
        guiGraphics.fill(maxX - borderSize, minY + borderSize, maxX, maxY - borderSize, color);
    }

    /**
     * Allows for drawing any sprite from a texture sheet in nine-sliced mode. This does not require the sprite to be
     * stitched onto an atlas.
     * <p>
     * The width &amp; height dimensions are filled by repeatedly drawing pre-defined slices of the original sprite.
     *
     * @param guiGraphics      the gui graphics instance
     * @param renderPipeline   the render pipeline, usually
     *                         {@link net.minecraft.client.renderer.RenderPipelines#GUI_TEXTURED}
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
    public static void blitNineSliced(GuiGraphics guiGraphics, RenderPipeline renderPipeline, ResourceLocation resourceLocation, int x, int y, int width, int height, int borderSize, int spriteWidth, int spriteHeight, int uOffset, int vOffset) {
        blitNineSliced(guiGraphics,
                renderPipeline,
                resourceLocation,
                x,
                y,
                width,
                height,
                borderSize,
                borderSize,
                spriteWidth,
                spriteHeight,
                uOffset,
                vOffset);
    }

    /**
     * Allows for drawing any sprite from a texture sheet in nine-sliced mode. This does not require the sprite to be
     * stitched onto an atlas.
     * <p>
     * The width &amp; height dimensions are filled by repeatedly drawing pre-defined slices of the original sprite.
     *
     * @param guiGraphics      the gui graphics instance
     * @param renderPipeline   the render pipeline, usually
     *                         {@link net.minecraft.client.renderer.RenderPipelines#GUI_TEXTURED}
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
    public static void blitNineSliced(GuiGraphics guiGraphics, RenderPipeline renderPipeline, ResourceLocation resourceLocation, int x, int y, int width, int height, int borderWidth, int borderHeight, int spriteWidth, int spriteHeight, int uOffset, int vOffset) {
        blitNineSliced(guiGraphics,
                renderPipeline,
                resourceLocation,
                x,
                y,
                width,
                height,
                borderWidth,
                borderHeight,
                borderWidth,
                borderHeight,
                spriteWidth,
                spriteHeight,
                uOffset,
                vOffset);
    }

    /**
     * Allows for drawing any sprite from a texture sheet in nine-sliced mode. This does not require the sprite to be
     * stitched onto an atlas.
     * <p>
     * The width &amp; height dimensions are filled by repeatedly drawing pre-defined slices of the original sprite.
     *
     * @param guiGraphics      the gui graphics instance
     * @param renderPipeline   the render pipeline, usually
     *                         {@link net.minecraft.client.renderer.RenderPipelines#GUI_TEXTURED}
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
    public static void blitNineSliced(GuiGraphics guiGraphics, RenderPipeline renderPipeline, ResourceLocation resourceLocation, int x, int y, int width, int height, int borderLeft, int borderTop, int borderRight, int borderBottom, int spriteWidth, int spriteHeight, int uOffset, int vOffset) {
        blitNineSliced(guiGraphics,
                renderPipeline,
                resourceLocation,
                x,
                y,
                width,
                height,
                borderLeft,
                borderTop,
                borderRight,
                borderBottom,
                spriteWidth,
                spriteHeight,
                uOffset,
                vOffset,
                -1);
    }

    /**
     * Allows for drawing any sprite from a texture sheet in nine-sliced mode. This does not require the sprite to be
     * stitched onto an atlas.
     * <p>
     * The width &amp; height dimensions are filled by repeatedly drawing pre-defined slices of the original sprite.
     *
     * @param guiGraphics      the gui graphics instance
     * @param renderPipeline   the render pipeline, usually
     *                         {@link net.minecraft.client.renderer.RenderPipelines#GUI_TEXTURED}
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
     * @param color            the vertex color, usually {@code -1}
     */
    public static void blitNineSliced(GuiGraphics guiGraphics, RenderPipeline renderPipeline, ResourceLocation resourceLocation, int x, int y, int width, int height, int borderLeft, int borderTop, int borderRight, int borderBottom, int spriteWidth, int spriteHeight, int uOffset, int vOffset, int color) {
        blitNineSliced(guiGraphics,
                renderPipeline,
                resourceLocation,
                x,
                y,
                width,
                height,
                borderLeft,
                borderTop,
                borderRight,
                borderBottom,
                spriteWidth,
                spriteHeight,
                uOffset,
                vOffset,
                256,
                256,
                color);
    }

    /**
     * Allows for drawing any sprite from a texture sheet in nine-sliced mode. This does not require the sprite to be
     * stitched onto an atlas.
     * <p>
     * The width &amp; height dimensions are filled by repeatedly drawing pre-defined slices of the original sprite.
     *
     * @param guiGraphics      the gui graphics instance
     * @param renderPipeline   the render pipeline, usually
     *                         {@link net.minecraft.client.renderer.RenderPipelines#GUI_TEXTURED}
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
    public static void blitNineSliced(GuiGraphics guiGraphics, RenderPipeline renderPipeline, ResourceLocation resourceLocation, int x, int y, int width, int height, int borderLeft, int borderTop, int borderRight, int borderBottom, int spriteWidth, int spriteHeight, int uOffset, int vOffset, int textureWidth, int textureHeight) {
        blitNineSliced(guiGraphics,
                renderPipeline,
                resourceLocation,
                x,
                y,
                width,
                height,
                borderLeft,
                borderTop,
                borderRight,
                borderBottom,
                spriteWidth,
                spriteHeight,
                uOffset,
                vOffset,
                textureWidth,
                textureHeight,
                -1);
    }

    /**
     * Allows for drawing any sprite from a texture sheet in nine-sliced mode. This does not require the sprite to be
     * stitched onto an atlas.
     * <p>
     * The width &amp; height dimensions are filled by repeatedly drawing pre-defined slices of the original sprite.
     *
     * @param guiGraphics      the gui graphics instance
     * @param renderPipeline   the render pipeline, usually
     *                         {@link net.minecraft.client.renderer.RenderPipelines#GUI_TEXTURED}
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
     * @param color            the vertex color, usually {@code -1}
     */
    public static void blitNineSliced(GuiGraphics guiGraphics, RenderPipeline renderPipeline, ResourceLocation resourceLocation, int x, int y, int width, int height, int borderLeft, int borderTop, int borderRight, int borderBottom, int spriteWidth, int spriteHeight, int uOffset, int vOffset, int textureWidth, int textureHeight, int color) {
        SingleTextureAtlasSprite textureAtlasSprite = new SingleTextureAtlasSprite(resourceLocation,
                spriteWidth,
                spriteHeight,
                uOffset,
                vOffset,
                textureWidth,
                textureHeight);
        GuiSpriteScaling.NineSlice nineSlice = new GuiSpriteScaling.NineSlice(spriteWidth,
                spriteHeight,
                new GuiSpriteScaling.NineSlice.Border(borderLeft, borderTop, borderRight, borderBottom),
                false);
        guiGraphics.blitNineSlicedSprite(renderPipeline, textureAtlasSprite, nineSlice, x, y, width, height, color);
    }

    /**
     * Allows for manually drawing any sprite using nine-sliced mode.
     * <p>
     * The width &amp; height dimensions are filled by repeatedly drawing pre-defined slices of the original sprite.
     *
     * @param guiGraphics    the gui graphics instance
     * @param renderPipeline the render pipeline, usually
     *                       {@link net.minecraft.client.renderer.RenderPipelines#GUI_TEXTURED}
     * @param sprite         the sprite resource location
     * @param x              the x-position on the screen
     * @param y              the y-position on the screen
     * @param width          the width to draw
     * @param height         the height to draw
     * @param borderSize     the border width &amp; height on the sides of the sprite, for drawing the frame
     */
    public static void blitNineSlicedSprite(GuiGraphics guiGraphics, RenderPipeline renderPipeline, ResourceLocation sprite, int x, int y, int width, int height, int borderSize) {
        blitNineSlicedSprite(guiGraphics,
                renderPipeline,
                sprite,
                x,
                y,
                width,
                height,
                borderSize,
                borderSize,
                borderSize,
                borderSize,
                -1);
    }

    /**
     * Allows for manually drawing any sprite using nine-sliced mode.
     * <p>
     * The width &amp; height dimensions are filled by repeatedly drawing pre-defined slices of the original sprite.
     *
     * @param guiGraphics    the gui graphics instance
     * @param renderPipeline the render pipeline, usually
     *                       {@link net.minecraft.client.renderer.RenderPipelines#GUI_TEXTURED}
     * @param sprite         the sprite resource location
     * @param x              the x-position on the screen
     * @param y              the y-position on the screen
     * @param width          the width to draw
     * @param height         the height to draw
     * @param borderLeft     the border width on the left side of the sprite, for drawing the frame
     * @param borderTop      the border height on the top side of the sprite, for drawing the frame
     * @param borderRight    the border width on the right side of the sprite, for drawing the frame
     * @param borderBottom   the border height on the bottom side of the sprite, for drawing the frame
     */
    public static void blitNineSlicedSprite(GuiGraphics guiGraphics, RenderPipeline renderPipeline, ResourceLocation sprite, int x, int y, int width, int height, int borderLeft, int borderTop, int borderRight, int borderBottom) {
        blitNineSlicedSprite(guiGraphics,
                renderPipeline,
                sprite,
                x,
                y,
                width,
                height,
                borderLeft,
                borderTop,
                borderRight,
                borderBottom,
                -1);
    }

    /**
     * Allows for manually drawing any sprite using nine-sliced mode.
     * <p>
     * The width &amp; height dimensions are filled by repeatedly drawing pre-defined slices of the original sprite.
     *
     * @param guiGraphics    the gui graphics instance
     * @param renderPipeline the render pipeline, usually
     *                       {@link net.minecraft.client.renderer.RenderPipelines#GUI_TEXTURED}
     * @param sprite         the sprite resource location
     * @param x              the x-position on the screen
     * @param y              the y-position on the screen
     * @param width          the width to draw
     * @param height         the height to draw
     * @param borderLeft     the border width on the left side of the sprite, for drawing the frame
     * @param borderTop      the border height on the top side of the sprite, for drawing the frame
     * @param borderRight    the border width on the right side of the sprite, for drawing the frame
     * @param borderBottom   the border height on the bottom side of the sprite, for drawing the frame
     * @param color          the vertex color, usually {@code -1}
     */
    public static void blitNineSlicedSprite(GuiGraphics guiGraphics, RenderPipeline renderPipeline, ResourceLocation sprite, int x, int y, int width, int height, int borderLeft, int borderTop, int borderRight, int borderBottom, int color) {
        TextureAtlasSprite textureAtlasSprite = guiGraphics.getSprite(new Material(Sheets.GUI_SHEET, sprite));
        GuiSpriteScaling.NineSlice nineSlice = new GuiSpriteScaling.NineSlice(textureAtlasSprite.contents().width(),
                textureAtlasSprite.contents().height(),
                new GuiSpriteScaling.NineSlice.Border(borderLeft, borderTop, borderRight, borderBottom),
                false);
        guiGraphics.blitNineSlicedSprite(renderPipeline, textureAtlasSprite, nineSlice, x, y, width, height, color);
    }

    /**
     * Manually draws a sprite using tile mode. Width &amp; height portions outside the texture dimensions are filled by
     * repeating the original texture.
     * <p>
     * Most sprites by default use the stretch mode, which squeezes them into the provided width &amp; height.
     *
     * @param guiGraphics    the gui graphics instance
     * @param renderPipeline the render pipeline, usually
     *                       {@link net.minecraft.client.renderer.RenderPipelines#GUI_TEXTURED}
     * @param sprite         the sprite resource location
     * @param x              the x-position on the screen
     * @param y              the y-position on the screen
     * @param width          the width to draw
     * @param height         the height to draw
     * @param spriteWidth    the sprite texture file width
     * @param spriteHeight   the sprite texture file height
     */
    public static void blitTiledSprite(GuiGraphics guiGraphics, RenderPipeline renderPipeline, ResourceLocation sprite, int x, int y, int width, int height, int spriteWidth, int spriteHeight) {
        blitTiledSprite(guiGraphics, renderPipeline, sprite, x, y, width, height, spriteWidth, spriteHeight, -1);
    }

    /**
     * Manually draws a sprite using tile mode. Width &amp; height portions outside the texture dimensions are filled by
     * repeating the original texture.
     * <p>
     * Most sprites by default use the stretch mode, which squeezes them into the provided width &amp; height.
     *
     * @param guiGraphics    the gui graphics instance
     * @param renderPipeline the render pipeline, usually
     *                       {@link net.minecraft.client.renderer.RenderPipelines#GUI_TEXTURED}
     * @param sprite         the sprite resource location
     * @param x              the x-position on the screen
     * @param y              the y-position on the screen
     * @param width          the width to draw
     * @param height         the height to draw
     * @param spriteWidth    the sprite texture file width
     * @param spriteHeight   the sprite texture file height
     * @param color          the vertex color, usually {@code -1}
     */
    public static void blitTiledSprite(GuiGraphics guiGraphics, RenderPipeline renderPipeline, ResourceLocation sprite, int x, int y, int width, int height, int spriteWidth, int spriteHeight, int color) {
        blitTiledSprite(guiGraphics,
                renderPipeline,
                sprite,
                x,
                y,
                width,
                height,
                spriteWidth,
                spriteHeight,
                0,
                0,
                color);
    }

    /**
     * Manually draws a sprite using tile mode. Width &amp; height portions outside the texture dimensions are filled by
     * repeating the original texture.
     * <p>
     * Most sprites by default use the stretch mode, which squeezes them into the provided width &amp; height.
     *
     * @param guiGraphics    the gui graphics instance
     * @param renderPipeline the render pipeline, usually
     *                       {@link net.minecraft.client.renderer.RenderPipelines#GUI_TEXTURED}
     * @param sprite         the sprite resource location
     * @param x              the x-position on the screen
     * @param y              the y-position on the screen
     * @param width          the width to draw
     * @param height         the height to draw
     * @param spriteWidth    the sprite texture file width
     * @param spriteHeight   the sprite texture file height
     * @param uOffset        the sprite u-offset on the texture sheet
     * @param vOffset        the sprite v-offset on the texture sheet
     * @param color          the vertex color, usually {@code -1}
     */
    public static void blitTiledSprite(GuiGraphics guiGraphics, RenderPipeline renderPipeline, ResourceLocation sprite, int x, int y, int width, int height, int spriteWidth, int spriteHeight, int uOffset, int vOffset, int color) {
        TextureAtlasSprite textureAtlasSprite = guiGraphics.getSprite(new Material(Sheets.GUI_SHEET, sprite));
        guiGraphics.blitTiledSprite(renderPipeline,
                textureAtlasSprite,
                x,
                y,
                width,
                height,
                uOffset,
                vOffset,
                spriteWidth,
                spriteHeight,
                spriteWidth,
                spriteHeight,
                color);
    }
}
