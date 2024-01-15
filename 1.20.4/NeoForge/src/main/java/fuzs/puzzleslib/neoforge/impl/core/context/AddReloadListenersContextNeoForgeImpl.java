package fuzs.puzzleslib.neoforge.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.AddReloadListenersContext;
import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListenerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Objects;
import java.util.function.Consumer;

public record AddReloadListenersContextNeoForgeImpl(String modId,
                                                    Consumer<PreparableReloadListener> consumer) implements AddReloadListenersContext {

    @Override
    public void registerReloadListener(String id, PreparableReloadListener reloadListener) {
        Objects.requireNonNull(id, "id is null");
        Objects.requireNonNull(reloadListener, "reload listener is null");
        this.consumer.accept(ForwardingReloadListenerHelper.fromReloadListener(new ResourceLocation(this.modId, id), reloadListener));
    }
}
