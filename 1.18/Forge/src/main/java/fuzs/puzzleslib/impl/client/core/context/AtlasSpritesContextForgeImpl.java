package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.AtlasSpritesContext;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Consumer;

public record AtlasSpritesContextForgeImpl(TextureAtlas textureAtlas,
                                           Consumer<ResourceLocation> consumer) implements AtlasSpritesContext {

    @Override
    public void registerAtlasSprite(ResourceLocation atlas, ResourceLocation sprite) {
        Objects.requireNonNull(atlas, "atlas id is null");
        Objects.requireNonNull(sprite, "sprite id is null");
        if (this.textureAtlas.location().equals(atlas)) {
            this.consumer.accept(sprite);
        }
    }
}
