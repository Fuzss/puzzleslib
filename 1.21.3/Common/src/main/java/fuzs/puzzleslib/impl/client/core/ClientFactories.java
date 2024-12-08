package fuzs.puzzleslib.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.init.v1.ItemModelDisplayOverrides;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.api.client.util.v1.RenderPropertyKey;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ClientFactories {
    ClientFactories INSTANCE = ServiceProviderHelper.load(ClientFactories.class);

    void constructClientMod(String modId, ClientModConstructor modConstructor, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle);

    ItemModelDisplayOverrides getItemModelDisplayOverrides();

    KeyMappingHelper getKeyMappingActivationHelper();

    @Nullable
    <T> T getRenderProperty(EntityRenderState entityRenderState, RenderPropertyKey<T> key);

    <T> void setRenderProperty(EntityRenderState entityRenderState, RenderPropertyKey<T> key, @Nullable T t);
}
