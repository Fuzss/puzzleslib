package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.CoreShadersContext;
import net.minecraft.client.renderer.ShaderProgram;

import java.util.Objects;
import java.util.function.Consumer;

public record CoreShadersContextNeoForgeImpl(Consumer<ShaderProgram> consumer) implements CoreShadersContext {

    @Override
    public void registerCoreShader(ShaderProgram shaderProgram) {
        Objects.requireNonNull(shaderProgram, "shader programm is null");
        this.consumer.accept(shaderProgram);
    }
}
