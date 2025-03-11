package fuzs.puzzleslib.neoforge.impl.client.core;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.api.client.renderer.v1.RenderPropertyKey;
import fuzs.puzzleslib.impl.client.core.ClientFactories;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.client.key.NeoForgeKeyMappingHelper;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.util.context.ContextKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

public final class NeoForgeClientFactories implements ClientFactories {
    private final Map<RenderPropertyKey<?>, ContextKey<?>> entityRenderStateKeys = new IdentityHashMap<>();

    @Override
    public void constructClientMod(String modId, ClientModConstructor modConstructor) {
        NeoForgeClientModConstructor.construct(modConstructor, modId);
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
                (RenderPropertyKey<?> keyX) -> new ContextKey<>(keyX.resourceLocation()));
    }

    @Override
    public void registerBuiltinResourcePack(ResourceLocation resourceLocation, Component displayName, boolean required) {
        NeoForgeModContainerHelper.getOptionalModEventBus(resourceLocation.getNamespace())
                .ifPresent((IEventBus eventBus) -> {
                    eventBus.addListener((final AddPackFindersEvent evt) -> {
                        if (evt.getPackType() == PackType.CLIENT_RESOURCES) {
                            evt.addPackFinders(resourceLocation.withPrefix("resourcepacks/"),
                                    PackType.CLIENT_RESOURCES,
                                    displayName,
                                    PackSource.BUILT_IN,
                                    required,
                                    Pack.Position.TOP);
                        }
                    });
                });
    }
}
