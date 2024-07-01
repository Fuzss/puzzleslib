package fuzs.puzzleslib.api.client.core.v1.context;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * Allows for registering new resource pack provided shaders for use in e.g. {@link net.minecraft.client.renderer.RenderType}.
 */
@FunctionalInterface
public interface CoreShadersContext {

    /**
     * Registers a shader.
     *
     * @param id           shader identifier
     * @param vertexFormat one of {@link com.mojang.blaze3d.vertex.DefaultVertexFormat}
     * @param callback     access to the shader instance for storage
     */
    void registerCoreShader(ResourceLocation id, VertexFormat vertexFormat, Consumer<ShaderInstance> callback);
}
