package fuzs.puzzleslib.impl.client.core.context;

import com.mojang.blaze3d.vertex.VertexFormat;
import fuzs.puzzleslib.api.client.core.v1.context.CoreShadersContext;
import fuzs.puzzleslib.impl.client.core.CoreShaderRegistrationCallback;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

public record CoreShadersContextFabricImpl(
        CoreShaderRegistrationCallback.RegistrationContext context) implements CoreShadersContext {

    @Override
    public void registerCoreShader(ResourceLocation id, VertexFormat vertexFormat, Consumer<ShaderInstance> callback) {
        Objects.requireNonNull(id, "id is null");
        Objects.requireNonNull(vertexFormat, "vertex format is null");
        Objects.requireNonNull(callback, "load callback is null");
        try {
            this.context.register(id, vertexFormat, callback);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
