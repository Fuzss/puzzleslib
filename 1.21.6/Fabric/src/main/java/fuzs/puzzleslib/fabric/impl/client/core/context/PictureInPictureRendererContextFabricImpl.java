package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.PictureInPictureRendererContext;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.Objects;
import java.util.function.Function;

public final class PictureInPictureRendererContextFabricImpl implements PictureInPictureRendererContext {

    @Override
    public void registerPictureInPictureRenderer(Function<MultiBufferSource.BufferSource, PictureInPictureRenderer<?>> pictureInPictureRendererFactory) {
        Objects.requireNonNull(pictureInPictureRendererFactory, "factory is null");
        SpecialGuiElementRegistry.register((SpecialGuiElementRegistry.Context context) -> {
            return pictureInPictureRendererFactory.apply(context.vertexConsumers());
        });
    }
}
