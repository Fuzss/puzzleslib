package fuzs.puzzleslib.impl.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;

public final class SingleTextureAtlasSprite extends TextureAtlasSprite {

    public SingleTextureAtlasSprite(ResourceLocation resourceLocation, int spriteWidth, int spriteHeight, int uOffset, int vOffset) {
        this(resourceLocation, spriteWidth, spriteHeight, uOffset, vOffset, 256, 256);
    }

    public SingleTextureAtlasSprite(ResourceLocation resourceLocation, int spriteWidth, int spriteHeight, int uOffset, int vOffset, int textureWidth, int textureHeight) {
        super(resourceLocation, new SpriteContents(resourceLocation, new FrameSize(spriteWidth, spriteHeight),
                new NativeImage(textureWidth, textureHeight, false), ResourceMetadata.EMPTY
        ), textureWidth, textureHeight, uOffset, vOffset);
    }
}
