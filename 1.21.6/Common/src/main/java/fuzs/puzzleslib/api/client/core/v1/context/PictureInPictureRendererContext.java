package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.function.Function;

/**
 * Register renderers for custom gui elements, like an entity or the enchanting table book.
 * <p>
 * Requires the additional creation of a custom gui render state, which must be registered in
 * {@link
 * net.minecraft.client.gui.render.state.GuiRenderState#submitPicturesInPictureState(PictureInPictureRenderState)}.
 */
@FunctionalInterface
public interface PictureInPictureRendererContext {

    /**
     * @param pictureInPictureRendererFactory the factory for the custom renderer
     */
    void registerPictureInPictureRenderer(Function<MultiBufferSource.BufferSource, PictureInPictureRenderer<?>> pictureInPictureRendererFactory);
}
