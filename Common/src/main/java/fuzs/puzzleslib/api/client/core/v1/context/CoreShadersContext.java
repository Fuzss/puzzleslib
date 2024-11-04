package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.renderer.ShaderProgram;

/**
 * Allows for registering new resource pack provided shaders for use in e.g.
 * {@link net.minecraft.client.renderer.RenderType}.
 */
@FunctionalInterface
public interface CoreShadersContext {

    /**
     * Registers a shader.
     *
     * @param shaderProgram the shader program for preloading
     */
    void registerCoreShader(ShaderProgram shaderProgram);
}
