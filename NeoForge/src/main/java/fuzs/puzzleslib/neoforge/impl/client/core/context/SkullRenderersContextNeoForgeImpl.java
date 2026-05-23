package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.SkullRenderersContext;
import fuzs.puzzleslib.api.client.init.v1.SkullRenderersFactory;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.Objects;

public record SkullRenderersContextNeoForgeImpl(EntityRenderersEvent.CreateSkullModels event) implements SkullRenderersContext {

    @Override
    public void registerSkullRenderer(SkullRenderersFactory factory) {
        Objects.requireNonNull(factory, "factory is null");
        factory.createSkullRenderers(this.event.getEntityModelSet(), this.event::registerSkullModel);
    }
}
