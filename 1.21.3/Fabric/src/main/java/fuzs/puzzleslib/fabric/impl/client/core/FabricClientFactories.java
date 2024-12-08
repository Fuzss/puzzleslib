package fuzs.puzzleslib.fabric.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.init.v1.ItemModelDisplayOverrides;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.api.client.util.v1.RenderPropertyKey;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.fabric.impl.client.init.FabricItemDisplayOverrides;
import fuzs.puzzleslib.fabric.impl.client.key.FabricKeyMappingHelper;
import fuzs.puzzleslib.fabric.impl.client.util.EntityRenderStateExtension;
import fuzs.puzzleslib.impl.client.core.ClientFactories;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class FabricClientFactories implements ClientFactories {

    @Override
    public void constructClientMod(String modId, ClientModConstructor modConstructor, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        FabricClientModConstructor.construct(modConstructor, modId, availableFlags, flagsToHandle);
    }

    @Override
    public ItemModelDisplayOverrides getItemModelDisplayOverrides() {
        return new FabricItemDisplayOverrides();
    }

    @Override
    public KeyMappingHelper getKeyMappingActivationHelper() {
        return new FabricKeyMappingHelper();
    }

    @Override
    public <T> @Nullable T getRenderProperty(EntityRenderState entityRenderState, RenderPropertyKey<T> key) {
        return ((EntityRenderStateExtension) entityRenderState).puzzleslib$getRenderProperty(key);
    }

    @Override
    public <T> void setRenderProperty(EntityRenderState entityRenderState, RenderPropertyKey<T> key, @Nullable T t) {
        ((EntityRenderStateExtension) entityRenderState).puzzleslib$setRenderProperty(key, t);
    }
}
