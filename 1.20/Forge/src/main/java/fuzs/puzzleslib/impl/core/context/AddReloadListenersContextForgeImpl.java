package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListenerImpl;
import fuzs.puzzleslib.api.core.v1.context.AddReloadListenersContext;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Objects;
import java.util.function.Consumer;

public record AddReloadListenersContextForgeImpl(String modId,
                                                 Consumer<PreparableReloadListener> consumer) implements AddReloadListenersContext {

    @Override
    public void registerReloadListener(String id, PreparableReloadListener reloadListener) {
        Objects.requireNonNull(id, "id is null");
        Objects.requireNonNull(reloadListener, "reload listener is null");
        this.consumer.accept(new ForwardingReloadListenerImpl(this.modId, id, reloadListener));
    }
}
