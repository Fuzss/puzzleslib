package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.CoreShadersContext;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.ShaderProgram;

import java.util.Objects;

public final class CoreShadersContextFabricImpl implements CoreShadersContext {

    @Override
    public void registerCoreShader(ShaderProgram shaderProgram) {
        Objects.requireNonNull(shaderProgram, "shader programm is null");
        CoreShaders.getProgramsToPreload().add(shaderProgram);
    }
}
