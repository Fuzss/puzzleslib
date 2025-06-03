package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

/**
 * Register a custom shader applied when spectating a certain entity type.
 */
@FunctionalInterface
public interface EntitySpectatorShadersContext {

    /**
     * Register the custom shader.
     *
     * @param resourceLocation the location to the shader file, usually at {@code shaders/post/<file>.json}
     * @param entityTypes      the entity types being spectated
     */
    void registerSpectatorShader(ResourceLocation resourceLocation, EntityType<?>... entityTypes);
}
