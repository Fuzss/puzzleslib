package fuzs.puzzleslib.fabric.impl.client.core.context;

import com.mojang.serialization.MapCodec;
import fuzs.puzzleslib.api.client.core.v1.context.SpecialBlockModelTypesContext;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class SpecialBlockModelTypesContextFabricImpl implements SpecialBlockModelTypesContext {

    @Override
    public void registerSpecialBlockModelType(ResourceLocation resourceLocation, MapCodec<? extends SpecialModelRenderer.Unbaked> codec) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(codec, "codec is null");
        SpecialModelRenderers.ID_MAPPER.put(resourceLocation, codec);
    }
}
