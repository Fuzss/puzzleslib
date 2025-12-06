package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

/**
 * Register a custom shader applied when spectating an entity type.
 */
public interface EntitySpectatorShadersContext {

    /**
     * Register the custom shader.
     *
     * @param entityType       the entity type being spectated
     * @param resourceLocation the location to the shader file, usually at {@code shaders/post/<file>.json}
     */
    void registerSpectatorShader(EntityType<?> entityType, ResourceLocation resourceLocation);
}
