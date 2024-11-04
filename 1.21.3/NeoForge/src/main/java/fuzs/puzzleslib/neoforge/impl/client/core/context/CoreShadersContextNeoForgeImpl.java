package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.mojang.blaze3d.vertex.VertexFormat;
import fuzs.puzzleslib.api.client.core.v1.context.CoreShadersContext;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.IOException;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record CoreShadersContextNeoForgeImpl(BiConsumer<ShaderInstance, Consumer<ShaderInstance>> consumer, ResourceProvider resourceManager) implements CoreShadersContext {

    @Override
    public void registerCoreShader(ResourceLocation id, VertexFormat vertexFormat, Consumer<ShaderInstance> callback) {
        Objects.requireNonNull(id, "id is null");
        Objects.requireNonNull(vertexFormat, "vertex format is null");
        Objects.requireNonNull(callback, "load callback is null");
        try {
            this.consumer.accept(new ShaderInstance(this.resourceManager, id, vertexFormat), callback);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
