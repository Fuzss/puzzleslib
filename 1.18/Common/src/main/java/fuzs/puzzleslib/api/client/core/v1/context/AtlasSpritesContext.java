package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

/**
 * stitch a custom sprite onto an atlas
 */
@FunctionalInterface
public interface AtlasSpritesContext {

    /**
     * convenient overload for directly registering a material
     *
     * @param material a texture material
     */
    default void registerMaterial(Material material) {
        this.registerAtlasSprite(material.atlasLocation(), material.texture());
    }

    /**
     * registers a sprite for being stitched onto an atlas
     *
     * @param atlasId the atlas to register to, since 1.14 there are multiples
     * @param spriteId the sprite to register
     */
    void registerAtlasSprite(ResourceLocation atlasId, ResourceLocation spriteId);
}
