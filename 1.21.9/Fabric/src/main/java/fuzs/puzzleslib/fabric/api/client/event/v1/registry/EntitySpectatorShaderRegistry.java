package fuzs.puzzleslib.fabric.api.client.event.v1.registry;

import fuzs.puzzleslib.fabric.impl.client.event.EntitySpectatorShaderRegistryImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

/**
 * Register a unique shader that is loaded when spectating a certain entity type.
 */
public interface EntitySpectatorShaderRegistry {
    /**
     * The singleton instance of the decorator registry.
     * Use this instance to call the methods in this interface.
     */
    EntitySpectatorShaderRegistry INSTANCE = new EntitySpectatorShaderRegistryImpl();

    /**
     * Register the custom shader.
     *
     * @param entityType the entity type being spectated
     * @param shaderLocation location to the shader file, usually <code>shaders/post/&lt;file&gt;.json</code>
     */
    void register(EntityType<?> entityType, ResourceLocation shaderLocation);
}
