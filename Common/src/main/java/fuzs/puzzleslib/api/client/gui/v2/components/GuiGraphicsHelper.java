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
 * A helper for drawing nine sliced sprites from a 256x256 texture source file, opposed to having to use vanilla's new one-sprite-per-image system.
 */
public final class GuiGraphicsHelper {

    private GuiGraphicsHelper() {
        // NO-OP
    }

    public static void blitNineSliced(GuiGraphics guiGraphics, ResourceLocation resourceLocation, int x, int y, int width, int height, int border, int spriteWidth, int spriteHeight, int uOffset, int vOffset) {
        blitNineSliced(guiGraphics,
                resourceLocation,
                x,
                y,
                width,
                height,
                border,
                border,
                border,
                border,
                spriteWidth,
                spriteHeight,
                uOffset,
                vOffset
        );
    }

    public static void blitNineSliced(GuiGraphics guiGraphics, ResourceLocation resourceLocation, int x, int y, int width, int height, int left, int top, int right, int bottom, int spriteWidth, int spriteHeight, int uOffset, int vOffset) {
        SingleTextureAtlasSprite textureAtlasSprite = new SingleTextureAtlasSprite(resourceLocation,
                spriteWidth,
                spriteHeight,
                uOffset,
                vOffset
        );
        GuiSpriteScaling.NineSlice nineSlice = new GuiSpriteScaling.NineSlice(spriteWidth,
                spriteHeight,
                new GuiSpriteScaling.NineSlice.Border(left, top, right, bottom)
        );
        guiGraphics.blitNineSlicedSprite(textureAtlasSprite, nineSlice, x, y, 0, width, height);
    }

    public static void blitNineSliced(GuiGraphics guiGraphics, ResourceLocation resourceLocation, int x, int y, int width, int height, int borderWidth, int borderHeight, int spriteWidth, int spriteHeight, int uOffset, int vOffset) {
        blitNineSliced(guiGraphics,
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
                vOffset
        );
    }

    private static class SingleTextureAtlasSprite extends TextureAtlasSprite {

        public SingleTextureAtlasSprite(ResourceLocation resourceLocation, int spriteWidth, int spriteHeight, int uOffset, int vOffset) {
            this(resourceLocation, spriteWidth, spriteHeight, uOffset, vOffset, 256, 256);
        }

        public SingleTextureAtlasSprite(ResourceLocation resourceLocation, int spriteWidth, int spriteHeight, int uOffset, int vOffset, int textureWidth, int textureHeight) {
            super(resourceLocation,
                    new SpriteContents(resourceLocation,
                            new FrameSize(spriteWidth, spriteHeight),
                            new NativeImage(textureWidth, textureHeight, false),
                            ResourceMetadata.EMPTY
                    ),
                    textureWidth,
                    textureHeight,
                    uOffset,
                    vOffset
            );
        }
    }
}
