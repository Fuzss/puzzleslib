package fuzs.puzzleslib.fabric.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.api.client.renderer.v1.RenderPropertyKey;
import fuzs.puzzleslib.fabric.impl.client.key.FabricKeyMappingHelper;
import fuzs.puzzleslib.fabric.impl.client.util.EntityRenderStateExtension;
import fuzs.puzzleslib.impl.client.core.ClientFactories;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.Nullable;

public final class FabricClientFactories implements ClientFactories {

    @Override
    public void constructClientMod(String modId, ClientModConstructor modConstructor) {
        FabricClientModConstructor.construct(modConstructor, modId);
    }

    @Override
    public KeyMappingHelper getKeyMappingActivationHelper() {
        return new FabricKeyMappingHelper();
    }

    @Override
    public <T> @Nullable T getRenderProperty(EntityRenderState renderState, RenderPropertyKey<T> key) {
        return ((EntityRenderStateExtension) renderState).puzzleslib$getRenderProperty(key);
    }

    @Override
    public <T> void setRenderProperty(EntityRenderState renderState, RenderPropertyKey<T> key, @Nullable T t) {
        ((EntityRenderStateExtension) renderState).puzzleslib$setRenderProperty(key, t);
    }
}
