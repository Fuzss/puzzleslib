package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.SkullRenderersContext;
import fuzs.puzzleslib.api.client.event.v2.SkullRenderersRegistry;
import fuzs.puzzleslib.api.client.init.v1.SkullRenderersFactory;

import java.util.Objects;

public final class SkullRenderersContextFabricImpl implements SkullRenderersContext {

    @Override
    public void registerSkullRenderer(SkullRenderersFactory factory) {
        Objects.requireNonNull(factory, "factory is null");
        SkullRenderersRegistry.INSTANCE.register(factory);
    }
}
