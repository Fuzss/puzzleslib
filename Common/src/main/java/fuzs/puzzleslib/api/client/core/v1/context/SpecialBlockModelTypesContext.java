package fuzs.puzzleslib.api.client.core.v1.context;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface SpecialBlockModelTypesContext {

    /**
     * Register a codec for a custom {@link SpecialModelRenderer.Unbaked} implementation.
     *
     * @param resourceLocation the special block model type resource location
     * @param codec            the corresponding codec for the type
     */
    void registerSpecialBlockModelType(ResourceLocation resourceLocation, MapCodec<? extends SpecialModelRenderer.Unbaked> codec);
}
