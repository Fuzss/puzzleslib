package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.AtlasSpritesContext;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class AtlasSpritesContextFabricImpl implements AtlasSpritesContext {

    @Override
    public void registerAtlasSprite(ResourceLocation atlas, ResourceLocation sprite) {
        Objects.requireNonNull(atlas, "atlas id is null");
        Objects.requireNonNull(sprite, "sprite id is null");
        ClientSpriteRegistryCallback.event(atlas).register((TextureAtlas atlasTexture, ClientSpriteRegistryCallback.Registry registry) -> {
            registry.register(sprite);
        });
    }
}
