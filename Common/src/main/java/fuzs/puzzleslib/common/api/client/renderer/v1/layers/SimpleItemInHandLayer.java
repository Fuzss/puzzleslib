package fuzs.puzzleslib.common.api.client.renderer.v1.layers;

import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;

/**
 * An extension of {@link ItemInHandLayer} exposing {@link ItemInHandLayer#useBabyOffset(ArmedEntityRenderState)}.
 * <p>
 * By default, baby rendering offsets are never used.
 *
 * @param <S> the render state
 * @param <M> the model
 */
public class SimpleItemInHandLayer<S extends ArmedEntityRenderState, M extends EntityModel<S> & ArmedModel<S>> extends ItemInHandLayer<S, M> {

    public SimpleItemInHandLayer(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    @Override
    public boolean useBabyOffset(S state) {
        return false;
    }
}
