package fuzs.puzzleslib.impl.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.Identifier;

public final class SingleTextureAtlasSprite extends TextureAtlasSprite {

    public SingleTextureAtlasSprite(Identifier identifier, int spriteWidth, int spriteHeight, int uOffset, int vOffset) {
        this(identifier, spriteWidth, spriteHeight, uOffset, vOffset, 256, 256);
    }

    public SingleTextureAtlasSprite(Identifier identifier, int spriteWidth, int spriteHeight, int uOffset, int vOffset, int textureWidth, int textureHeight) {
        super(identifier,
                new SpriteContents(identifier,
                        new FrameSize(spriteWidth, spriteHeight),
                        new NativeImage(textureWidth, textureHeight, false)),
                textureWidth,
                textureHeight,
                uOffset,
                vOffset,
                0);
    }
}
