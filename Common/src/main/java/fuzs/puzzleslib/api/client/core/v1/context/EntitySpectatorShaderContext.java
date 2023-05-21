package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

/**
 * register a custom shader that is applied when spectating a certain entity type
 */
@FunctionalInterface
public interface EntitySpectatorShaderContext {

    /**
     * Register the custom shader.
     *
     * @param shaderLocation location to the shader file, usually <code>shaders/post/&lt;file&gt;.json</code>
     * @param entityTypes        entity types being spectated
     */
    void registerSpectatorShader(ResourceLocation shaderLocation, EntityType<?>... entityTypes);
}
