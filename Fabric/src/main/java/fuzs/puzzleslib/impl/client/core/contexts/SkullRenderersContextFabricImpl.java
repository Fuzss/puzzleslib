package fuzs.puzzleslib.impl.client.core.contexts;

import fuzs.puzzleslib.api.client.core.v1.contexts.SkullRenderersContext;
import fuzs.puzzleslib.api.client.events.v2.SkullRenderersRegistry;
import fuzs.puzzleslib.api.client.registration.v1.SkullRenderersFactory;

import java.util.Objects;

public final class SkullRenderersContextFabricImpl implements SkullRenderersContext {

    @Override
    public void registerSkullRenderer(SkullRenderersFactory factory) {
        Objects.requireNonNull(factory, "factory is null");
        SkullRenderersRegistry.INSTANCE.register(factory);
    }
}
