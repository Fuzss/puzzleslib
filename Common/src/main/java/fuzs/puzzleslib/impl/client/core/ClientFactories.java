package fuzs.puzzleslib.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.api.client.renderer.v1.RenderPropertyKey;
import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface ClientFactories {
    ClientFactories INSTANCE = ServiceProviderHelper.load(ClientFactories.class);

    void constructClientMod(String modId, ClientModConstructor modConstructor);

    KeyMappingHelper getKeyMappingActivationHelper();

    @Nullable <T> T getRenderProperty(EntityRenderState entityRenderState, RenderPropertyKey<T> key);

    <T> void setRenderProperty(EntityRenderState entityRenderState, RenderPropertyKey<T> key, @Nullable T t);

    void registerBuiltinResourcePack(ResourceLocation resourceLocation, Component displayName, boolean required);
}
