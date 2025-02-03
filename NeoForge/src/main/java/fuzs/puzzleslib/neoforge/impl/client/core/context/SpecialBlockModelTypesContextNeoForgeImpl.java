package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.mojang.serialization.MapCodec;
import fuzs.puzzleslib.api.client.core.v1.context.SpecialBlockModelTypesContext;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

import java.util.Objects;

public record SpecialBlockModelTypesContextNeoForgeImpl(RegisterSpecialModelRendererEvent evt) implements SpecialBlockModelTypesContext {

    @Override
    public void registerSpecialBlockModelType(ResourceLocation resourceLocation, MapCodec<? extends SpecialModelRenderer.Unbaked> codec) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(codec, "codec is null");
        this.evt.register(resourceLocation, codec);
    }
}
