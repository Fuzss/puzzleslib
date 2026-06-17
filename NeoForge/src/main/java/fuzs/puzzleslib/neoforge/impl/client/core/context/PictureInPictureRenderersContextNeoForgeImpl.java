package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.common.api.client.core.v1.context.PictureInPictureRenderersContext;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;

import java.util.Objects;
import java.util.function.Supplier;

public record PictureInPictureRenderersContextNeoForgeImpl(RegisterPictureInPictureRenderersEvent event) implements PictureInPictureRenderersContext {

    @Override
    public <T extends PictureInPictureRenderState> void registerPictureInPictureRenderer(Class<T> renderStateClazz, Supplier<PictureInPictureRenderer<T>> rendererFactory) {
        Objects.requireNonNull(renderStateClazz, "class is null");
        Objects.requireNonNull(rendererFactory, "factory is null");
        this.event.register(renderStateClazz, rendererFactory);
    }
}
