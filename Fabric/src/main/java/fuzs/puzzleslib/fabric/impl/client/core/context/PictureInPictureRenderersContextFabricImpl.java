package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.common.api.client.core.v1.context.PictureInPictureRenderersContext;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;

import java.util.Objects;
import java.util.function.Supplier;

public final class PictureInPictureRenderersContextFabricImpl implements PictureInPictureRenderersContext {

    @Override
    public <T extends PictureInPictureRenderState> void registerPictureInPictureRenderer(Class<T> renderStateClazz, Supplier<PictureInPictureRenderer<T>> rendererFactory) {
        Objects.requireNonNull(renderStateClazz, "class is null");
        Objects.requireNonNull(rendererFactory, "factory is null");
        PictureInPictureRendererRegistry.register((PictureInPictureRendererRegistry.Context context) -> {
            return rendererFactory.get();
        });
    }
}
