package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.PictureInPictureRendererContext;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;

import java.util.Objects;
import java.util.function.Function;

public record PictureInPictureRendererContextNeoForgeImpl(RegisterPictureInPictureRenderersEvent event) implements PictureInPictureRendererContext {

    @Override
    public <T extends PictureInPictureRenderState> void registerPictureInPictureRenderer(Class<T> renderStateClazz, Function<MultiBufferSource.BufferSource, PictureInPictureRenderer<T>> pictureInPictureRendererFactory) {
        Objects.requireNonNull(renderStateClazz, "class is null");
        Objects.requireNonNull(pictureInPictureRendererFactory, "factory is null");
        this.event.register(renderStateClazz, pictureInPictureRendererFactory);
    }
}
