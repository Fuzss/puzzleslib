package fuzs.puzzleslib.neoforge.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.init.v1.ItemModelDisplayOverrides;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.api.client.util.v1.RenderPropertyKey;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.impl.client.core.ClientFactories;
import fuzs.puzzleslib.neoforge.impl.client.init.NeoForgeItemDisplayOverrides;
import fuzs.puzzleslib.neoforge.impl.client.key.NeoForgeKeyMappingHelper;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.context.ContextKey;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public final class NeoForgeClientFactories implements ClientFactories {
    private final Map<RenderPropertyKey<?>, ContextKey<?>> entityRenderStateKeys = new IdentityHashMap<>();

    @Override
    public void constructClientMod(String modId, ClientModConstructor modConstructor, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        NeoForgeClientModConstructor.construct(modConstructor, modId, availableFlags, flagsToHandle);
    }

    @Override
    public ItemModelDisplayOverrides getItemModelDisplayOverrides() {
        return new NeoForgeItemDisplayOverrides();
    }

    @Override
    public KeyMappingHelper getKeyMappingActivationHelper() {
        return new NeoForgeKeyMappingHelper();
    }

    @Override
    public <T> @Nullable T getRenderProperty(EntityRenderState entityRenderState, RenderPropertyKey<T> key) {
        return entityRenderState.getRenderData(this.getContextKey(key));
    }

    @Override
    public <T> void setRenderProperty(EntityRenderState entityRenderState, RenderPropertyKey<T> key, @Nullable T t) {
        entityRenderState.setRenderData(this.getContextKey(key), t);
    }

    private <T> ContextKey<T> getContextKey(RenderPropertyKey<T> key) {
        return (ContextKey<T>) this.entityRenderStateKeys.computeIfAbsent(key,
                keyX -> new ContextKey<>(keyX.resourceLocation()));
    }
}
